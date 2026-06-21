package pe.edu.upla.textil_control.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upla.textil_control.model.OrdenTrabajo;
import pe.edu.upla.textil_control.repository.OrdenTrabajoRepository;
import pe.edu.upla.textil_control.repository.OrdenTrabajoResumenDTO;
import java.time.Year;
import java.util.List;

@Service
public class OrdenTrabajoService {
    @Autowired private OrdenTrabajoRepository repository;
    // 🔥 1. Agrega esto para ejecutar el SQL masivo instantáneo
    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbc;

    public List<OrdenTrabajoResumenDTO> listarTodas() {
        return repository.listarResumen();
    }

    public String generarSiguienteCodigo() {
        int anio = Year.now().getValue();
        String prefijo = "OT-" + anio + "-";
        String maxCodigo = repository.findMaxCodigoByPrefijo(prefijo + "%");

        if (maxCodigo != null && maxCodigo.length() >= 12) {
            try {
                int numeroActual = Integer.parseInt(maxCodigo.substring(8));
                return prefijo + String.format("%04d", numeroActual + 1);
            } catch (NumberFormatException e) {
                return prefijo + "0001";
            }
        }
        return prefijo + "0001";
    }

    @Transactional
    public void guardarOrden(OrdenTrabajo ot, String accion) {
        if ("actualizar".equals(accion) && ot.getIdOt() != null) {
            OrdenTrabajo otExistente = repository.findById(ot.getIdOt()).orElseThrow();
            if (!"CREADA".equals(otExistente.getEstado())) {
                throw new IllegalStateException("Solo se pueden editar órdenes en estado CREADA.");
            }
            otExistente.setCliente(ot.getCliente());
            otExistente.setIdModelo(ot.getIdModelo());
            otExistente.setCantidadEst(ot.getCantidadEst());
            repository.save(otExistente);
        } else {
            ot.setCodigoOt(generarSiguienteCodigo());
            ot.setEstado("CREADA");
            repository.save(ot);
        }
    }

    @Transactional
    public void cambiarEstado(Integer idOt, String nuevoEstado) {
        OrdenTrabajo ot = repository.findById(idOt).orElseThrow();
        String estadoActual = ot.getEstado();

        if ("CREADA".equals(estadoActual) && (!"EN_PROCESO".equals(nuevoEstado) && !"ANULADA".equals(nuevoEstado))) {
            throw new IllegalStateException("Transición no permitida.");
        }
        if ("EN_PROCESO".equals(estadoActual) && (!"FINALIZADA".equals(nuevoEstado) && !"ANULADA".equals(nuevoEstado))) {
            throw new IllegalStateException("Transición no permitida.");
        }

        // 🔥 TRIPLE CANDADO DE SEGURIDAD ANTES DE PASAR A EN_PROCESO
        if ("CREADA".equals(estadoActual) && "EN_PROCESO".equals(nuevoEstado)) {

            // 1. Validación Física: ¿Se registró el ingreso de material en almacén?
            long cantidadTelas = repository.countTelasByIdOt(idOt);
            if (cantidadTelas == 0) {
                throw new IllegalStateException("No se puede iniciar la producción. Debes registrar al menos un rollo de tela en Inventario para esta Orden de Trabajo.");
            }

            // 2. Validación de Calidad: ¿Hay material aprobado para trabajar?
            long telasAceptadas = repository.countTelasAceptadasByIdOt(idOt);
            if (telasAceptadas == 0) {
                throw new IllegalStateException("No se puede iniciar la producción. Las telas registradas para esta Orden de Trabajo se encuentran actualmente en estado OBSERVADO o RECHAZADO. Modifica el estado a ACEPTADO desde el módulo de Inventario para poder proceder.");
            }

            // 3. Validación de Proceso Técnico: ¿Las telas críticas completaron el tiempo de reposo obligatorio?
            long telasPendientesReposo = repository.countTelasSinReposoCompletado(idOt);
            if (telasPendientesReposo > 0) {
                throw new IllegalStateException("No se puede iniciar la producción. Existen rollos de tela vinculados a esta Orden de Trabajo que requieren tiempo de reposo obligatorio y aún no han finalizado el proceso técnico (Estado: APTO_CORTE).");
            }

            // 4. Si supera todos los filtros del taller, se autoriza la generación de tareas de maquinistas
            generarCargasDeTrabajoAutomatica(ot.getIdOt(), ot.getIdModelo(), ot.getCantidadEst());
        }

        ot.setEstado(nuevoEstado);
        repository.save(ot);
    }

    // 🔥 3. El método que automatiza y multiplica las piezas
    private void generarCargasDeTrabajoAutomatica(Integer idOt, Integer idModelo, Integer cantidadEst) {
        // A) Generar cargas para las PIEZAS INDIVIDUALES (Corte, Costura, etc.)
        // Aquí multiplicamos la cantidad del modelo por la cantidad de la OT
        jdbc.update("""
            INSERT INTO asignaciones_carga (id_ot, id_pieza, id_fase, cantidad_piezas, estado_fase, tipo_tarea)
            SELECT ?, p.id_pieza, prf.id_fase, (p.cantidad * ?), 'PENDIENTE', 'NORMAL'
            FROM piezas_modelo p
            JOIN pieza_ruta_fase prf ON p.id_pieza = prf.id_pieza
            WHERE p.id_modelo = ?
        """, idOt, cantidadEst, idModelo);

        // B) Generar cargas para FASES GLOBALES (Ensamblaje, Acabado)
        // Donde no hay pieza específica, la cantidad es directamente la cantidad de la OT
        jdbc.update("""
            INSERT INTO asignaciones_carga (id_ot, id_pieza, id_fase, cantidad_piezas, estado_fase, tipo_tarea)
            SELECT ?, NULL, prf.id_fase, ?, 'PENDIENTE', 
                   CASE WHEN prf.id_fase = 6 THEN 'ENSAMBLAJE' ELSE 'NORMAL' END
            FROM pieza_ruta_fase prf
            WHERE prf.id_modelo = ? AND prf.id_pieza IS NULL
        """, idOt, cantidadEst, idModelo);
    }
    @Transactional
    public boolean anularOrden(Integer idOt) {
        if (repository.countTelasByIdOt(idOt) > 0) return false; // Candado MVC
        return repository.deleteSiCreada(idOt) > 0;
    }
    // ── API Externa RENIEC / SUNAT ──
    public String buscarClienteApi(String documento) {
        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String url = (documento.length() == 8)
                    ? "https://api.apis.net.pe/v1/dni?numero=" + documento
                    : "https://api.apis.net.pe/v1/ruc?numero=" + documento;
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return "{}"; // Si no lo encuentra, devuelve JSON vacío
        }
    }
}
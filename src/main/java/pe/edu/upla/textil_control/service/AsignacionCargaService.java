package pe.edu.upla.textil_control.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upla.textil_control.repository.AsignacionCargaRepository;
import pe.edu.upla.textil_control.repository.FasePendienteDTO;
import pe.edu.upla.textil_control.repository.ResumenCargaMaquinistaDTO;
import pe.edu.upla.textil_control.repository.TareaMaquinistaDTO;

import java.util.List;

@Service
public class AsignacionCargaService {

    @Autowired
    private AsignacionCargaRepository repository;
    // 🔥 1. Inyectamos JdbcTemplate para la automatización
    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbc;
    @Autowired
    private NotificacionService notificacionService;

    public List<FasePendienteDTO> listarFasesPendientes() {
        return repository.listarFasesPendientes();
    }

    public List<ResumenCargaMaquinistaDTO> resumenCargaPorMaquinista() {
        return repository.resumenCargaPorMaquinista();
    }

    public List<TareaMaquinistaDTO> listarTareasPorMaquinista(int idMaquinista) {
        return repository.listarTareasPorMaquinista(idMaquinista);
    }

    public List<TareaMaquinistaDTO> listarTareasCompletadasPorMaquinista(int idMaquinista) {
        return repository.listarTareasCompletadasPorMaquinista(idMaquinista);
    }

    @Transactional
    public void asignarMaquinista(int idAsignacion, int idMaquinista) {
        // Regla original de tu MVC: Bloqueo de fase previa
        String estadoPrev = repository.obtenerEstadoFasePrevia(idAsignacion);
        if (estadoPrev != null && !"COMPLETADA".equalsIgnoreCase(estadoPrev)) {
            throw new IllegalStateException("Bloqueo activo: la fase previa aún no está completada");
        }

        if (repository.asignarMaquinista(idAsignacion, idMaquinista) == 0) {
            throw new IllegalStateException("No se pudo asignar (verifique que la tarea esté PENDIENTE)");
        }
    }

    @Transactional
    public void reasignarMaquinista(int idAsignacion, int idMaquinista) {
        if (repository.reasignarMaquinista(idAsignacion, idMaquinista) == 0) {
            throw new IllegalStateException("No se pudo reasignar (la tarea debe estar en proceso)");
        }
    }

    @Transactional
    public void completarFase(int idAsignacion, int piezasCompletadas) {

        // 1. Obtenemos datos ampliados para la notificación y los cálculos
        java.util.Map<String, Object> tarea = jdbc.queryForMap(
                """
                SELECT 
                    ac.id_ot, ac.id_pieza, ac.id_maquinista, ac.cantidad_piezas,
                    ot.codigo_ot, fp.nombre AS nombre_fase, pm.nombre_pieza,
                    u.nombre AS maq_nombre, u.apellido AS maq_apellido
                FROM asignaciones_carga ac
                JOIN orden_trabajo ot ON ac.id_ot = ot.id_ot
                JOIN fases_produccion fp ON ac.id_fase = fp.id_fase
                JOIN usuarios u ON ac.id_maquinista = u.id_usuario
                LEFT JOIN piezas_modelo pm ON ac.id_pieza = pm.id_pieza
                WHERE ac.id_asignacion = ?
                """, idAsignacion
        );

        int cantidadOriginal = ((Number) tarea.get("cantidad_piezas")).intValue();
        int faltante = cantidadOriginal - piezasCompletadas;

        // 2. Guardamos el progreso de la tarea actual en la BD
        if (repository.completarFase(idAsignacion, piezasCompletadas) == 0) {
            throw new IllegalStateException("No se pudo completar la tarea");
        }

        // 🔥 3. RESTAURACIÓN DE NOTIFICACIONES
        String maqNombre = (String) tarea.get("maq_nombre");
        String maqApellido = (String) tarea.get("maq_apellido");
        String codigoOt = (String) tarea.get("codigo_ot");
        String nombreFase = (String) tarea.get("nombre_fase");
        String nombrePieza = (String) tarea.get("nombre_pieza");
        if (nombrePieza == null) nombrePieza = "Ensamblaje";

        String mensaje = String.format("El maquinista %s %s completó la fase '%s' de '%s' para la OT %s (%d/%d unidades).",
                maqNombre, maqApellido, nombreFase, nombrePieza, codigoOt, piezasCompletadas, cantidadOriginal);

        notificacionService.registrarNotificacion(
                "Tarea completada",
                mensaje,
                "INFO",
                idAsignacion,
                "ADMINISTRADOR,SUPERVISOR"
        );

        // 4. AUTOMATIZACIÓN DE CALIDAD
        if (faltante > 0) {
            jdbc.update("""
                INSERT INTO defectos_reproceso (id_ot, id_pieza, id_maquinista, cantidad_faltante, estado, id_asignacion)
                VALUES (?, ?, ?, ?, 'PENDIENTE', ?)
            """,
                    tarea.get("id_ot"), tarea.get("id_pieza"), tarea.get("id_maquinista"), faltante, idAsignacion);
        }

        // 5. AUTOMATIZACIÓN DE DESPACHO
        Integer idOt = ((Number) tarea.get("id_ot")).intValue();
        Integer tareasPendientes = jdbc.queryForObject(
                "SELECT COUNT(*) FROM asignaciones_carga WHERE id_ot = ? AND estado_fase != 'COMPLETADA'",
                Integer.class, idOt
        );

        if (tareasPendientes != null && tareasPendientes == 0) {
            jdbc.update("UPDATE orden_trabajo SET estado = 'FINALIZADA' WHERE id_ot = ?", idOt);

            // 🔥 NUEVA NOTIFICACIÓN: Aviso de que ya se puede despachar
            notificacionService.registrarNotificacion(
                    "¡Lote Terminado!",
                    "La " + codigoOt + " ha completado todas sus fases y está lista para Conciliación y Despacho.",
                    "SUCCESS",
                    idOt,
                    "ADMINISTRADOR,SUPERVISOR" // También puedes añadir el rol del encargado de despacho
            );
        }
    }
}
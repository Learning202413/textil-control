package pe.edu.upla.textil_control.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upla.textil_control.model.FallaTela;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.repository.FallaTelaRepository;
import pe.edu.upla.textil_control.repository.FallaTelaResumenDTO;
import pe.edu.upla.textil_control.repository.TelaMapeoDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Service: FallaTela
 * HU02: Mapeo Digital de Imperfecciones y Fallas en la Tela
 *
 * LÓGICA DE NEGOCIO PRESERVADA ÍNTEGRAMENTE del FallasTelaServlet:
 *  - Listado según rol: Admin→todas, Tizador→propias, JefeProduccion→todas(readonly)
 *  - esAreaNoApta = true por defecto al registrar
 *  - Telas para mapeo: solo estado_calidad IN ('ACEPTADO','OBSERVADO')
 */
@Service
public class FallaTelaService {

    @Autowired
    private FallaTelaRepository fallaTelaRepo;

    // ── LECTURA ───────────────────────────────────────────────

    /**
     * Lista fallas según el rol del usuario — LÓGICA PRESERVADA del FallasTelaServlet.doGet()
     *  - Admin → ve todas
     *  - Tizador → solo las suyas
     *  - Jefe Producción / otros → ve todas (sin poder registrar)
     */
    public List<FallaTelaResumenDTO> listarSegunRol(Usuario usuario) {
        boolean esAdmin   = "ADMINISTRADOR".equalsIgnoreCase(usuario.getNombreRol());
        boolean esTizador = "TIZADOR".equalsIgnoreCase(usuario.getNombreRol());

        if (esAdmin) {
            return fallaTelaRepo.listarTodas();
        } else if (esTizador) {
            return fallaTelaRepo.listarPorTizador(usuario.getIdUsuario());
        } else {
            return fallaTelaRepo.listarTodas();
        }
    }

    /** Filtrar fallas por tela específica */
    public List<FallaTelaResumenDTO> listarPorTela(int idTela) {
        return fallaTelaRepo.listarPorTela(idTela);
    }

    /** Telas disponibles para mapeo — solo ACEPTADO/OBSERVADO */
    public List<TelaMapeoDTO> listarTelasParaMapeo() {
        return fallaTelaRepo.listarTelasParaMapeo();
    }

    /** Telas que tienen fallas registradas (para el filtro) */
    public List<TelaMapeoDTO> listarTelasConFallas() {
        return fallaTelaRepo.listarTelasConFallas();
    }

    /** Conteo de fallas por tipo para una tela (resumen visual) — PRESERVADO de FallaTelaDAO */
    public int contarFallasPorTipoYTela(int idTela, String tipoFalla) {
        return fallaTelaRepo.contarFallasPorTipoYTela(idTela, tipoFalla);
    }

    // ── ESCRITURA ─────────────────────────────────────────────

    /**
     * Registrar falla — LÓGICA PRESERVADA del FallasTelaServlet.doPost("registrar")
     * esAreaNoApta se activa siempre por defecto (CUS 2.3)
     */
    @Transactional
    public FallaTela registrar(FallaTela falla) {
        return fallaTelaRepo.save(falla);
    }

    /**
     * Actualizar falla — LÓGICA PRESERVADA del FallasTelaServlet.doPost("actualizar")
     * Se recupera la entidad existente para evitar que fecha_registro o id_tizador original se pierdan.
     */
    @Transactional
    public FallaTela actualizar(FallaTela fallaActualizada) {
        // 🔥 VALIDACIÓN: Verificar el estado de la OT antes de permitir la edición
        String estadoOt = fallaTelaRepo.obtenerEstadoOtPorFalla(fallaActualizada.getIdFalla());
        if ("FINALIZADA".equals(estadoOt) || "ANULADA".equals(estadoOt)) {
            throw new IllegalStateException("No se puede editar: La Orden de Trabajo ya se encuentra en estado " + estadoOt + ".");
        }

        FallaTela existente = fallaTelaRepo.findById(fallaActualizada.getIdFalla())
                .orElseThrow(() -> new RuntimeException("Falla no encontrada"));

        existente.setIdTela(fallaActualizada.getIdTela());
        existente.setTipoFalla(fallaActualizada.getTipoFalla());
        existente.setPosicionRollo(fallaActualizada.getPosicionRollo());
        existente.setPosicionMetro(fallaActualizada.getPosicionMetro());
        existente.setAnchoCm(fallaActualizada.getAnchoCm());
        existente.setLargoCm(fallaActualizada.getLargoCm());
        existente.setDescripcion(fallaActualizada.getDescripcion());
        existente.setEsAreaNoApta(fallaActualizada.getEsAreaNoApta());

        return fallaTelaRepo.save(existente);
    }

    @Transactional
    public void eliminar(int idFalla) {
        // 🔥 VALIDACIÓN: Verificar el estado de la OT antes de permitir la eliminación
        String estadoOt = fallaTelaRepo.obtenerEstadoOtPorFalla(idFalla);
        if ("FINALIZADA".equals(estadoOt) || "ANULADA".equals(estadoOt)) {
            throw new IllegalStateException("No se puede eliminar: La Orden de Trabajo ya se encuentra en estado " + estadoOt + ".");
        }

        fallaTelaRepo.deleteById(idFalla);
    }
}

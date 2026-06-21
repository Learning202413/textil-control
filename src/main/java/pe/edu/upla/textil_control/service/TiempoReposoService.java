package pe.edu.upla.textil_control.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upla.textil_control.model.TiempoReposo;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.repository.TelaReposoDTO;
import pe.edu.upla.textil_control.repository.TiempoReposoRepository;
import pe.edu.upla.textil_control.repository.TiempoReposoResumenDTO;

import java.util.List;

/**
 * Service: TiempoReposo
 * HU03: Gestión de Tiempos de Reposo y Corte
 *
 * LÓGICA DE NEGOCIO PRESERVADA ÍNTEGRAMENTE del TiemposReposoServlet:
 *  - registrarInicio(): validar tela disponible, INSERT con NOW(), EN_REPOSO, notificacion=0
 *  - marcarAptoCorte(): solo si estado=EN_REPOSO
 *  - cancelar(): solo si estado=EN_REPOSO
 *  - verificarYNotificar(): UPDATE masivo reposos vencidos
 *  - construirJsonActivos(): JSON para polling AJAX
 *  - listarSegunRol(): Admin→todos, otros→propios
 */
@Service
public class TiempoReposoService {

    @Autowired
    private TiempoReposoRepository reposoRepo;

    // ── LECTURA ───────────────────────────────────────────────

    /**
     * Lista reposos según rol — PRESERVADO del TiemposReposoServlet.doGet()
     */
    public List<TiempoReposoResumenDTO> listarSegunRol(Usuario usuario) {
        if ("ADMINISTRADOR".equalsIgnoreCase(usuario.getNombreRol())) {
            return reposoRepo.listarTodos();
        } else {
            return reposoRepo.listarPorUsuario(usuario.getIdUsuario());
        }
    }

    /** Telas disponibles para iniciar reposo */
    public List<TelaReposoDTO> listarTelasDisponibles() {
        return reposoRepo.listarTelasDisponiblesParaReposo();
    }

    /** Obtener un reposo por ID — PRESERVADO de TiempoReposoDAO.obtenerPorId() */
    public TiempoReposoResumenDTO obtenerPorId(int idReposo) {
        return reposoRepo.obtenerPorId(idReposo);
    }

    // ── ESCRITURA ─────────────────────────────────────────────

    /**
     * CUS 3.2: Registrar Inicio de Reposo
     * PRESERVADO del TiemposReposoServlet.doPost("iniciar"):
     *  1. Validar que la tela está disponible para reposo
     *  2. Insertar con NOW(), estado EN_REPOSO, notificacion=0
     *
     * @return true si se registró exitosamente
     * @throws IllegalStateException si la tela no está disponible
     */
    @Transactional
    public boolean registrarInicio(int idTela, int idUsuario,
                                    int duracionMinutos, String observaciones) {
        // VALIDACIÓN PRESERVADA: verificar tela disponible
        int disponible = reposoRepo.verificarTelaDisponible(idTela);
        if (disponible == 0) {
            throw new IllegalStateException("telaNoDisponible");
        }

        reposoRepo.registrarInicio(idTela, idUsuario, duracionMinutos,
                                    observaciones != null ? observaciones : "");
        return true;
    }

    /**
     * CUS 3.3: Marcar Apto para Corte
     * PRESERVADO: solo actualiza si estado=EN_REPOSO
     */
    @Transactional
    public boolean marcarAptoCorte(int idReposo) {
        return reposoRepo.marcarAptoCorte(idReposo) > 0;
    }

    /**
     * Cancelar reposo activo
     * PRESERVADO: solo actualiza si estado=EN_REPOSO
     */
    @Transactional
    public boolean cancelar(int idReposo) {
        return reposoRepo.cancelar(idReposo) > 0;
    }

    /** Eliminar reposo */
    @Transactional
    public boolean eliminar(int idReposo) {
        // 1. Buscamos el estado de la OT asociada
        String estadoOt = reposoRepo.obtenerEstadoOtPorReposo(idReposo);

        // 2. Bloqueamos si la tela ya está comprometida en producción
        if ("EN_PROCESO".equals(estadoOt) || "FINALIZADA".equals(estadoOt)) {
            throw new IllegalStateException("No se puede eliminar: La Orden de Trabajo ya se encuentra en estado " + estadoOt + " y la tela está en producción.");
        }

        // 3. Si es CREADA o ANULADA, permitimos eliminar
        reposoRepo.deleteById(idReposo); // Usa deleteById o tu consulta @Query nativa si tienes una
        return true;
    }

    /**
     * Verificar reposos vencidos y marcarlos como APTO_CORTE.
     * PRESERVADO del TiempoReposoDAO.verificarYNotificar()
     * Llamado por el scheduler y por el polling AJAX.
     *
     * @return número de registros actualizados
     */
    @Transactional
    public int verificarYNotificar() {
        return reposoRepo.verificarYNotificar();
    }

    /**
     * Construir JSON para polling AJAX — PRESERVADO del TiemposReposoServlet
     * Incluye porcentaje, minutos restantes, estado y notificación para cada reposo.
     */
    public String construirJsonActivos(List<TiempoReposoResumenDTO> lista) {
        StringBuilder sb = new StringBuilder("{\"reposos\":[");
        boolean primero = true;
        for (TiempoReposoResumenDTO tr : lista) {
            if (!primero) sb.append(",");
            primero = false;

            // Usar valores calculados directamente por SQL — equivale a TiempoReposo.getPorcentajeCompletado() y getMinutosRestantes()
            int porcentaje = tr.getPorcentajeCompletado() != null ? tr.getPorcentajeCompletado() : 0;
            int minutosRestantes = tr.getMinutosRestantes() != null ? tr.getMinutosRestantes() : 0;

            sb.append("{");
            sb.append("\"id\":").append(tr.getIdReposo()).append(",");
            sb.append("\"codigo\":\"").append(escaparJson(tr.getCodigoTela())).append("\",");
            sb.append("\"ot\":\"").append(escaparJson(tr.getCodigoOt())).append("\",");
            sb.append("\"estado\":\"").append(tr.getEstado()).append("\",");
            sb.append("\"pct\":").append(porcentaje).append(",");
            sb.append("\"minRest\":").append(minutosRestantes).append(",");
            sb.append("\"notif\":").append(tr.getNotificacionEnviada() != null && tr.getNotificacionEnviada().intValue() == 1);
            sb.append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    /** Escapar caracteres para JSON — PRESERVADO del TiemposReposoServlet */
    private String escaparJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}

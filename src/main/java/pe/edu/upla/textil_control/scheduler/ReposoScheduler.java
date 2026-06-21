package pe.edu.upla.textil_control.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pe.edu.upla.textil_control.service.TiempoReposoService;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Scheduler: ReposoScheduler
 * HU03: Verificación automática de reposos vencidos
 *
 * Equivalente a la parte de verificación del HorarioScheduler original.
 * Cada 60 segundos verifica si hay reposos cuyo tiempo ya venció
 * y los marca automáticamente como APTO_CORTE.
 *
 * LÓGICA PRESERVADA: verificarYNotificar() ejecuta el mismo UPDATE
 * masivo del TiempoReposoDAO original.
 */
@Component
public class ReposoScheduler {

    private static final Logger LOG = Logger.getLogger(ReposoScheduler.class.getName());

    @Autowired
    private TiempoReposoService reposoService;

    /**
     * Se ejecuta cada 60 segundos (mismo intervalo que el scheduler original).
     * Verifica reposos vencidos y los marca como APTO_CORTE.
     */
    @Scheduled(fixedRate = 60000)
    public void verificarRepososVencidos() {
        try {
            int actualizados = reposoService.verificarYNotificar();
            if (actualizados > 0) {
                LOG.info("[ReposoScheduler] " + actualizados
                        + " reposo(s) marcados como APTO_CORTE automáticamente.");
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING,
                    "[ReposoScheduler] Error al verificar reposos: " + e.getMessage(), e);
        }
    }
}

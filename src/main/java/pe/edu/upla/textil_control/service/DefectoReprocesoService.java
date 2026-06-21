package pe.edu.upla.textil_control.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import pe.edu.upla.textil_control.model.DefectoReproceso;
import pe.edu.upla.textil_control.repository.DefectoReprocesoRepository;
import pe.edu.upla.textil_control.repository.DefectoResumenDTO;
import pe.edu.upla.textil_control.repository.ResumenReprocesosDTO;
import java.util.List;

@Service
public class DefectoReprocesoService {
    @Autowired private DefectoReprocesoRepository repository;
    // 🔥 1. Agregamos el JdbcTemplate para ejecutar la inserción automática
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private NotificacionService notificacionService;

    public List<DefectoResumenDTO> listarTodos() {
        return repository.listarTodos();
    }

    public List<ResumenReprocesosDTO> resumenReprocesos() {
        return repository.resumenReprocesosPorMaquinista();
    }

    @Transactional
    public void registrarDefecto(DefectoReproceso defecto) {
        defecto.setEstado(DefectoReproceso.Estado.PENDIENTE);
        repository.save(defecto);
    }

    @Transactional
    public void corregirOriginal(Integer idDefecto, Integer idAsignacion, Integer cantidad) {
        // 1. Restaurar las piezas en la asignación de carga original
        repository.corregirCargaTrabajoOriginal(idAsignacion, cantidad);

        // 2. Marcar el defecto como CORREGIDO (en lugar de eliminarlo)
        int actualizados = repository.marcarComoCorregido(idDefecto);
        if (actualizados == 0) {
            throw new RuntimeException("No se pudo actualizar el defecto a CORREGIDO");
        }
    }

    @Transactional
    public void marcarRepuesto(Integer idDefecto, String obs) {
        // 1. Inserción de reposición (Crea la carga de trabajo hija)
        jdbc.update("""
            INSERT INTO asignaciones_carga (id_ot, id_pieza, id_fase, cantidad_piezas, estado_fase, tipo_tarea, id_asignacion_padre)
            SELECT dr.id_ot, dr.id_pieza, ac.id_fase, dr.cantidad_faltante, 'PENDIENTE', 'REPOSICION', dr.id_asignacion
            FROM defectos_reproceso dr
            JOIN asignaciones_carga ac ON dr.id_asignacion = ac.id_asignacion
            WHERE dr.id_defecto = ?
        """, idDefecto);

        // 2. Actualizamos el defecto indicando que ya fue repuesto (genera_reposicion = 1)
        repository.marcarConReposicion(idDefecto, obs);
        // 🔥 NUEVA NOTIFICACIÓN: Aviso de falla en calidad
        notificacionService.registrarNotificacion(
                "Alerta de Calidad: Reposición",
                "Se ha generado una tarea de reposición por falla/merma en el defecto #" + idDefecto + ".",
                "WARNING",
                idDefecto,
                "ADMINISTRADOR,SUPERVISOR"
        );
    }

    @Transactional
    public void completarDefecto(Integer idDefecto, String tipoFalla, String observaciones) {
        // Solo clasifica la falla y cambia a REGISTRADO, SIN crear tareas nuevas
        repository.actualizarObservacionYEstado(idDefecto, observaciones);
        repository.actualizarTipoFalla(idDefecto, tipoFalla);
    }

    @Transactional
    public void actualizarTipoFalla(Integer idDefecto, String tipoFalla) {
        repository.actualizarTipoFalla(idDefecto, tipoFalla);
    }
}
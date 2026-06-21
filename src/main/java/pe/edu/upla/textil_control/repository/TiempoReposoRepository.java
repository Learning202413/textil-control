package pe.edu.upla.textil_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upla.textil_control.model.TiempoReposo;

import java.util.List;

/**
 * Repository: tiempos_reposo
 * HU03: Gestión de Tiempos de Reposo y Corte
 *
 * SQL PRESERVADO ÍNTEGRAMENTE del TiempoReposoDAO original.
 */
@Repository
public interface TiempoReposoRepository extends JpaRepository<TiempoReposo, Integer> {

    // ── LECTURA ───────────────────────────────────────────────

    /** Lista TODOS los registros de reposo — Admin ve todo (SQL idéntico a TiempoReposoDAO.listarTodos) */
    @Query(value = """
        SELECT tr.id_reposo AS idReposo, tr.id_tela AS idTela,
               tr.id_usuario_inicio AS idUsuarioInicio,
               tr.fecha_inicio AS fechaInicio,
               DATE_FORMAT(tr.fecha_inicio, '%d/%m/%y %H:%i') AS fechaInicioStr,
               tr.duracion_minutos AS duracionMinutos,
               DATE_FORMAT(tr.fecha_fin_estimada, '%d/%m/%y %H:%i') AS fechaFinEstimadaStr,
               DATE_FORMAT(tr.fecha_fin_real, '%d/%m/%y %H:%i') AS fechaFinRealStr,
               tr.estado,
               CAST(tr.notificacion_enviada AS SIGNED) AS notificacionEnviada,
               tr.observaciones, tr.fecha_crea AS fechaCrea,
               t.codigo_tela AS codigoTela, t.tipo_tejido AS tipoTejido,
               ot.codigo_ot AS codigoOt,
               CONCAT(u.nombre,' ',u.apellido) AS nombreRegistrador,
               CASE WHEN tr.estado = 'EN_REPOSO' AND tr.fecha_inicio IS NOT NULL AND tr.duracion_minutos > 0 THEN
                   CAST(LEAST(100, GREATEST(0, TIMESTAMPDIFF(MINUTE, tr.fecha_inicio, NOW()) * 100 / tr.duracion_minutos)) AS SIGNED)
               ELSE 0 END AS porcentajeCompletado,
               CASE WHEN tr.estado = 'EN_REPOSO' AND tr.fecha_inicio IS NOT NULL THEN
                   CAST(GREATEST(0, tr.duracion_minutos - TIMESTAMPDIFF(MINUTE, tr.fecha_inicio, NOW())) AS SIGNED)
               ELSE 0 END AS minutosRestantes
        FROM tiempos_reposo tr
        JOIN telas          t  ON tr.id_tela           = t.id_tela
        JOIN orden_trabajo  ot ON t.id_ot               = ot.id_ot
        JOIN usuarios       u  ON tr.id_usuario_inicio  = u.id_usuario
        ORDER BY tr.fecha_inicio DESC
        """, nativeQuery = true)
    List<TiempoReposoResumenDTO> listarTodos();

    /** Lista registros por usuario (SQL idéntico a TiempoReposoDAO.listarPorUsuario) */
    @Query(value = """
        SELECT tr.id_reposo AS idReposo, tr.id_tela AS idTela,
               tr.id_usuario_inicio AS idUsuarioInicio,
               tr.fecha_inicio AS fechaInicio,
               DATE_FORMAT(tr.fecha_inicio, '%d/%m/%y %H:%i') AS fechaInicioStr,
               tr.duracion_minutos AS duracionMinutos,
               DATE_FORMAT(tr.fecha_fin_estimada, '%d/%m/%y %H:%i') AS fechaFinEstimadaStr,
               DATE_FORMAT(tr.fecha_fin_real, '%d/%m/%y %H:%i') AS fechaFinRealStr,
               tr.estado,
               CAST(tr.notificacion_enviada AS SIGNED) AS notificacionEnviada,
               tr.observaciones, tr.fecha_crea AS fechaCrea,
               t.codigo_tela AS codigoTela, t.tipo_tejido AS tipoTejido,
               ot.codigo_ot AS codigoOt,
               CONCAT(u.nombre,' ',u.apellido) AS nombreRegistrador,
               CASE WHEN tr.estado = 'EN_REPOSO' AND tr.fecha_inicio IS NOT NULL AND tr.duracion_minutos > 0 THEN
                   CAST(LEAST(100, GREATEST(0, TIMESTAMPDIFF(MINUTE, tr.fecha_inicio, NOW()) * 100 / tr.duracion_minutos)) AS SIGNED)
               ELSE 0 END AS porcentajeCompletado,
               CASE WHEN tr.estado = 'EN_REPOSO' AND tr.fecha_inicio IS NOT NULL THEN
                   CAST(GREATEST(0, tr.duracion_minutos - TIMESTAMPDIFF(MINUTE, tr.fecha_inicio, NOW())) AS SIGNED)
               ELSE 0 END AS minutosRestantes
        FROM tiempos_reposo tr
        JOIN telas          t  ON tr.id_tela           = t.id_tela
        JOIN orden_trabajo  ot ON t.id_ot               = ot.id_ot
        JOIN usuarios       u  ON tr.id_usuario_inicio  = u.id_usuario
        WHERE tr.id_usuario_inicio = :idUsuario
        ORDER BY tr.fecha_inicio DESC
        """, nativeQuery = true)
    List<TiempoReposoResumenDTO> listarPorUsuario(@Param("idUsuario") int idUsuario);

    /** Telas que requieren reposo y NO tienen reposo registrado (SQL idéntico a TiempoReposoDAO) */
    @Query(value = """
        SELECT t.id_tela AS idTela, t.codigo_tela AS codigoTela,
               t.tipo_tejido AS tipoTejido, t.color,
               t.num_rollos AS numRollos, ot.codigo_ot AS codigoOt,
               ct.tiempo_reposo AS tiempoReposo
        FROM telas t
        JOIN orden_trabajo ot ON t.id_ot = ot.id_ot
        LEFT JOIN catalogo_telas ct ON t.id_catalogo_tela = ct.id_catalogo
        WHERE t.requiere_reposo = true
          AND NOT EXISTS (
              SELECT 1 FROM tiempos_reposo tr WHERE tr.id_tela = t.id_tela
          )
        ORDER BY t.fecha_ingreso DESC
        """, nativeQuery = true)
    List<TelaReposoDTO> listarTelasDisponiblesParaReposo();

    /** Obtener un registro por ID (SQL idéntico a TiempoReposoDAO.obtenerPorId) */
    @Query(value = """
        SELECT tr.id_reposo AS idReposo, tr.id_tela AS idTela,
               tr.id_usuario_inicio AS idUsuarioInicio,
               tr.fecha_inicio AS fechaInicio,
               DATE_FORMAT(tr.fecha_inicio, '%d/%m/%y %H:%i') AS fechaInicioStr,
               tr.duracion_minutos AS duracionMinutos,
               DATE_FORMAT(tr.fecha_fin_estimada, '%d/%m/%y %H:%i') AS fechaFinEstimadaStr,
               DATE_FORMAT(tr.fecha_fin_real, '%d/%m/%y %H:%i') AS fechaFinRealStr,
               tr.estado,
               CAST(tr.notificacion_enviada AS SIGNED) AS notificacionEnviada,
               tr.observaciones, tr.fecha_crea AS fechaCrea,
               t.codigo_tela AS codigoTela, t.tipo_tejido AS tipoTejido,
               ot.codigo_ot AS codigoOt,
               CONCAT(u.nombre,' ',u.apellido) AS nombreRegistrador,
               CASE WHEN tr.estado = 'EN_REPOSO' AND tr.fecha_inicio IS NOT NULL AND tr.duracion_minutos > 0 THEN
                   CAST(LEAST(100, GREATEST(0, TIMESTAMPDIFF(MINUTE, tr.fecha_inicio, NOW()) * 100 / tr.duracion_minutos)) AS SIGNED)
               ELSE 0 END AS porcentajeCompletado,
               CASE WHEN tr.estado = 'EN_REPOSO' AND tr.fecha_inicio IS NOT NULL THEN
                   CAST(GREATEST(0, tr.duracion_minutos - TIMESTAMPDIFF(MINUTE, tr.fecha_inicio, NOW())) AS SIGNED)
               ELSE 0 END AS minutosRestantes
        FROM tiempos_reposo tr
        JOIN telas          t  ON tr.id_tela           = t.id_tela
        JOIN orden_trabajo  ot ON t.id_ot               = ot.id_ot
        JOIN usuarios       u  ON tr.id_usuario_inicio  = u.id_usuario
        WHERE tr.id_reposo = :idReposo
        """, nativeQuery = true)
    TiempoReposoResumenDTO obtenerPorId(@Param("idReposo") int idReposo);

    /** Verificar si una tela está disponible para reposo (SQL idéntico a TiempoReposoDAO) */
    @Query(value = """
        SELECT COUNT(*) FROM telas t
        WHERE t.id_tela = :idTela AND t.requiere_reposo = true
          AND NOT EXISTS (SELECT 1 FROM tiempos_reposo WHERE id_tela = t.id_tela)
        """, nativeQuery = true)
    int verificarTelaDisponible(@Param("idTela") int idTela);

    // ── ESCRITURA ─────────────────────────────────────────────

    /** Insertar registro de reposo con INSERT nativo (SQL idéntico a TiempoReposoDAO.registrarInicio) */
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO tiempos_reposo
          (id_tela, id_usuario_inicio, fecha_inicio, duracion_minutos,
           estado, notificacion_enviada, observaciones)
        VALUES (:idTela, :idUsuario, NOW(), :duracion, 'EN_REPOSO', 0, :obs)
        """, nativeQuery = true)
    void registrarInicio(@Param("idTela") int idTela,
                         @Param("idUsuario") int idUsuario,
                         @Param("duracion") int duracionMinutos,
                         @Param("obs") String observaciones);

    /** CUS 3.3: Marcar apto para corte (SQL idéntico a TiempoReposoDAO.marcarAptoCorte) */
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE tiempos_reposo
        SET estado = 'APTO_CORTE',
            fecha_fin_real = NOW(),
            notificacion_enviada = 1
        WHERE id_reposo = :idReposo AND estado = 'EN_REPOSO'
        """, nativeQuery = true)
    int marcarAptoCorte(@Param("idReposo") int idReposo);

    /** Cancelar reposo activo (SQL idéntico a TiempoReposoDAO.cancelar) */
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE tiempos_reposo
        SET estado = 'CANCELADO'
        WHERE id_reposo = :idReposo AND estado = 'EN_REPOSO'
        """, nativeQuery = true)
    int cancelar(@Param("idReposo") int idReposo);

    /** Verificar y notificar reposos vencidos (SQL idéntico a TiempoReposoDAO.verificarYNotificar) */
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE tiempos_reposo
        SET estado = 'APTO_CORTE',
            fecha_fin_real = NOW(),
            notificacion_enviada = 1
        WHERE estado = 'EN_REPOSO'
          AND fecha_fin_estimada <= NOW()
          AND notificacion_enviada = 0
        """, nativeQuery = true)
    int verificarYNotificar();

    /** Eliminar un registro de reposo por ID */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tiempos_reposo WHERE id_reposo = :idReposo", nativeQuery = true)
    int eliminarPorId(@Param("idReposo") int idReposo);

    // 🌟 NUEVO CANDADO: Obtener el estado de la OT asociada a este registro de reposo
    @Query(value = """
        SELECT ot.estado 
        FROM tiempos_reposo tr
        JOIN telas t ON tr.id_tela = t.id_tela
        JOIN orden_trabajo ot ON t.id_ot = ot.id_ot
        WHERE tr.id_reposo = :idReposo
    """, nativeQuery = true)
    String obtenerEstadoOtPorReposo(@Param("idReposo") int idReposo);
}

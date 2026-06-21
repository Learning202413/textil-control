package pe.edu.upla.textil_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upla.textil_control.model.DefectoReproceso;
import java.util.List;

public interface DefectoReprocesoRepository extends JpaRepository<DefectoReproceso, Integer> {

    @Query(value = """
    SELECT
        dr.id_defecto AS idDefecto,
        dr.id_ot AS idOt,
        dr.id_pieza AS idPieza,
        dr.id_maquinista AS idMaquinista,
        dr.tipo_falla AS tipoFalla,
        dr.observaciones,
        dr.fecha_registro AS fechaRegistro,
        ot.codigo_ot AS codigoOt,
        mp.nombre AS nombreModelo,
        pm.nombre_pieza AS nombrePieza,
        CONCAT(u.nombre, ' ', u.apellido) AS nombreMaquinista,
        dr.estado,
        dr.cantidad_faltante AS cantidadFaltante,
        dr.id_asignacion AS idAsignacion,
        dr.genera_reposicion AS generaReposicion   -- 👈 agregar esta línea
    FROM defectos_reproceso dr
    JOIN orden_trabajo ot ON dr.id_ot = ot.id_ot
    JOIN modelos_prenda mp ON ot.id_modelo = mp.id_modelo
    LEFT JOIN piezas_modelo pm ON dr.id_pieza = pm.id_pieza
    LEFT JOIN usuarios u ON dr.id_maquinista = u.id_usuario
    ORDER BY dr.fecha_registro DESC
    """, nativeQuery = true)
    List<DefectoResumenDTO> listarTodos();

    @Query(value = """
        SELECT
            u.id_usuario AS idMaquinista, CONCAT(u.nombre, ' ', u.apellido) AS nombreMaquinista,
            e.nombre AS especialidad, COUNT(dr.id_defecto) AS totalReprocesos, u.reprocesos_acum AS reprocesosHistorico
        FROM usuarios u
        JOIN roles r ON u.id_rol = r.id_rol
        LEFT JOIN usuario_especialidad ue ON u.id_usuario = ue.id_usuario
        LEFT JOIN especialidades e ON ue.id_especialidad = e.id_especialidad
        LEFT JOIN defectos_reproceso dr ON dr.id_maquinista = u.id_usuario AND dr.estado = 'PENDIENTE'
        WHERE u.id_rol = 6 AND u.activo = TRUE
        GROUP BY u.id_usuario, nombreMaquinista, especialidad, reprocesosHistorico
        ORDER BY totalReprocesos DESC, reprocesosHistorico DESC
    """, nativeQuery = true)
    List<ResumenReprocesosDTO> resumenReprocesosPorMaquinista();

    @Modifying
    @Transactional
    @Query(value = "UPDATE defectos_reproceso SET estado = 'REGISTRADO', observaciones = :observaciones WHERE id_defecto = :idDefecto", nativeQuery = true)
    void actualizarObservacionYEstado(@Param("idDefecto") int idDefecto, @Param("observaciones") String observaciones);

    @Modifying
    @Transactional
    @Query(value = "UPDATE asignaciones_carga SET piezas_completadas = piezas_completadas + :cantidad, estado_fase = 'COMPLETADA' WHERE id_asignacion = :idAsignacion", nativeQuery = true)
    int corregirCargaTrabajoOriginal(@Param("idAsignacion") int idAsignacion, @Param("cantidad") int cantidad);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM defectos_reproceso WHERE id_defecto = :idDefecto", nativeQuery = true)
    void eliminarDefecto(@Param("idDefecto") int idDefecto);

    @Modifying
    @Transactional
    @Query(value = "UPDATE defectos_reproceso SET tipo_falla = :tipoFalla WHERE id_defecto = :idDefecto", nativeQuery = true)
    void actualizarTipoFalla(@Param("idDefecto") int idDefecto, @Param("tipoFalla") String tipoFalla);

    @Modifying
    @Transactional
    @Query("UPDATE DefectoReproceso d SET d.estado = 'CORREGIDO', d.cantidadFaltante = 0, d.generaReposicion = false WHERE d.idDefecto = :idDefecto")
    int marcarComoCorregido(@Param("idDefecto") Integer idDefecto);

    @Modifying
    @Transactional
    @Query(value = "UPDATE defectos_reproceso SET estado = 'REGISTRADO', observaciones = :observaciones, genera_reposicion = 1 WHERE id_defecto = :idDefecto", nativeQuery = true)
    void marcarConReposicion(@Param("idDefecto") int idDefecto, @Param("observaciones") String observaciones);
}
package pe.edu.upla.textil_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upla.textil_control.model.AsignacionCarga;
import java.util.List;

public interface AsignacionCargaRepository extends JpaRepository<AsignacionCarga, Integer> {

    @Query(value = """
        WITH asignaciones_con_prev AS (
            SELECT
                ac.id_asignacion, ac.id_ot, ac.id_pieza, ac.id_fase,
                ac.id_maquinista, ac.estado_fase, ac.fecha_asignacion,
                ac.cantidad_piezas, ac.piezas_completadas, ac.tipo_tarea,
                LAG(ac.id_fase) OVER (PARTITION BY ac.id_ot, ac.id_pieza ORDER BY fp.orden) AS id_fase_previa
            FROM asignaciones_carga ac
            JOIN fases_produccion fp ON ac.id_fase = fp.id_fase
            WHERE ac.estado_fase IN ('PENDIENTE', 'EN_PROCESO', 'COMPLETADA')
        )
        SELECT DISTINCT
            ac.id_asignacion AS idAsignacion, ac.id_ot AS idOt, ac.id_pieza AS idPieza, ac.id_fase AS idFase,
            ac.id_maquinista AS idMaquinista,
            ac.estado_fase AS estadoFase, ac.fecha_asignacion AS fechaAsignacion,
            ac.cantidad_piezas AS cantidadPiezas, ac.piezas_completadas AS piezasCompletadas,
            ot.codigo_ot AS codigoOt,
            ot.fecha_crea AS fechaCrea,
            mp.nombre AS nombreModelo,
            pm.nombre_pieza AS nombrePieza,
            fp.nombre AS nombreFase,
            fp.orden AS orden,
            
            -- 🔥 ESTA ES LA MAGIA QUE RESTAURA TU LÓGICA MVC PARA EL ENSAMBLAJE 🔥
            CASE
                WHEN ac.tipo_tarea = 'ENSAMBLAJE' THEN
                    IF((SELECT COUNT(*) FROM asignaciones_carga sub 
                        WHERE sub.id_ot = ac.id_ot AND sub.id_pieza IS NOT NULL 
                        AND sub.estado_fase != 'COMPLETADA') > 0,
                       'PIEZAS PENDIENTES', 'COMPLETADA')
                ELSE ac_prev.estado_fase
            END AS fasePreviaEstado,
            
            CONCAT(u.nombre, ' ', u.apellido) AS nombreMaquinista,
            ac.tipo_tarea AS tipoTarea
        FROM asignaciones_con_prev ac
        JOIN orden_trabajo ot ON ac.id_ot = ot.id_ot
        JOIN modelos_prenda mp ON ot.id_modelo = mp.id_modelo
        LEFT JOIN piezas_modelo pm ON ac.id_pieza = pm.id_pieza
        JOIN fases_produccion fp ON ac.id_fase = fp.id_fase
        LEFT JOIN asignaciones_carga ac_prev
               ON ac_prev.id_ot    = ac.id_ot
              AND ac_prev.id_pieza = ac.id_pieza
              AND ac_prev.id_fase  = ac.id_fase_previa
              AND ac_prev.tipo_tarea = 'NORMAL'
        LEFT JOIN usuarios u ON ac.id_maquinista = u.id_usuario
        WHERE ac.estado_fase IN ('PENDIENTE', 'EN_PROCESO', 'COMPLETADA')
        ORDER BY ot.fecha_crea DESC, pm.nombre_pieza, fp.orden
    """, nativeQuery = true)
    List<FasePendienteDTO> listarFasesPendientes();

    @Query(value = """
        SELECT u.id_usuario AS idMaquinista,
               CONCAT(u.nombre, ' ', u.apellido) AS nombreMaquinista,
               e.nombre AS especialidad,
               COUNT(ac.id_asignacion) AS totalActivas
        FROM usuarios u
        JOIN roles r ON u.id_rol = r.id_rol
        LEFT JOIN usuario_especialidad ue ON u.id_usuario = ue.id_usuario
        LEFT JOIN especialidades e ON ue.id_especialidad = e.id_especialidad
        LEFT JOIN asignaciones_carga ac
               ON ac.id_maquinista = u.id_usuario
              AND ac.estado_fase = 'EN_PROCESO'
        WHERE u.id_rol = 6 AND u.activo = TRUE
        GROUP BY u.id_usuario, nombreMaquinista, especialidad
        ORDER BY totalActivas DESC
    """, nativeQuery = true)
    List<ResumenCargaMaquinistaDTO> resumenCargaPorMaquinista();

    @Query(value = """
        SELECT
            ac.id_asignacion AS idAsignacion, ac.id_ot AS idOt, ac.id_pieza AS idPieza, ac.id_fase AS idFase,
            ac.id_maquinista AS idMaquinista, ac.estado_fase AS estadoFase,
            ac.fecha_asignacion AS fechaAsignacion, ac.fecha_completado AS fechaCompletado,
            ac.cantidad_piezas AS cantidadPiezas, ac.piezas_completadas AS piezasCompletadas,
            ac.tipo_tarea AS tipoTarea,
            ot.codigo_ot AS codigoOt, pm.nombre_pieza AS nombrePieza, fp.nombre AS nombreFase
        FROM asignaciones_carga ac
        JOIN orden_trabajo ot ON ac.id_ot = ot.id_ot
        LEFT JOIN piezas_modelo pm ON ac.id_pieza = pm.id_pieza
        JOIN fases_produccion fp ON ac.id_fase = fp.id_fase
        WHERE ac.id_maquinista = :idMaquinista
        AND ac.estado_fase = 'EN_PROCESO'
        ORDER BY ac.fecha_asignacion DESC
    """, nativeQuery = true)
    List<TareaMaquinistaDTO> listarTareasPorMaquinista(@Param("idMaquinista") int idMaquinista);

    @Query(value = """
        SELECT
            ac.id_asignacion AS idAsignacion, ac.id_ot AS idOt, ac.id_pieza AS idPieza, ac.id_fase AS idFase,
            ac.id_maquinista AS idMaquinista, ac.estado_fase AS estadoFase,
            ac.fecha_asignacion AS fechaAsignacion, ac.fecha_completado AS fechaCompletado,
            ac.cantidad_piezas AS cantidadPiezas, ac.piezas_completadas AS piezasCompletadas,
            ac.tipo_tarea AS tipoTarea,
            ot.codigo_ot AS codigoOt, pm.nombre_pieza AS nombrePieza, fp.nombre AS nombreFase
        FROM asignaciones_carga ac
        JOIN orden_trabajo ot ON ac.id_ot = ot.id_ot
        LEFT JOIN piezas_modelo pm ON ac.id_pieza = pm.id_pieza
        JOIN fases_produccion fp ON ac.id_fase = fp.id_fase
        WHERE ac.id_maquinista = :idMaquinista
        AND ac.estado_fase IN ('COMPLETADA', 'REGISTRADO')
        ORDER BY ac.fecha_completado DESC
    """, nativeQuery = true)
    List<TareaMaquinistaDTO> listarTareasCompletadasPorMaquinista(@Param("idMaquinista") int idMaquinista);

    @Query(value = """
        SELECT
            CASE
                WHEN ac.tipo_tarea = 'ENSAMBLAJE' THEN
                    IF((SELECT COUNT(*) FROM asignaciones_carga sub 
                        WHERE sub.id_ot = ac.id_ot AND sub.id_pieza IS NOT NULL 
                        AND sub.estado_fase != 'COMPLETADA') > 0,
                       'PIEZAS PENDIENTES', 'COMPLETADA')
                ELSE
                    (SELECT ac_prev.estado_fase FROM asignaciones_carga ac_prev
                     JOIN fases_produccion fp_prev ON ac_prev.id_fase = fp_prev.id_fase
                     JOIN fases_produccion fp_actual ON ac.id_fase = fp_actual.id_fase
                     WHERE ac_prev.id_ot = ac.id_ot AND ac_prev.id_pieza = ac.id_pieza
                     AND fp_prev.orden < fp_actual.orden ORDER BY fp_prev.orden DESC LIMIT 1)
            END
        FROM asignaciones_carga ac WHERE ac.id_asignacion = :idAsignacion
    """, nativeQuery = true)
    String obtenerEstadoFasePrevia(@Param("idAsignacion") Integer idAsignacion);

    @Modifying
    @Transactional
    @Query(value = "UPDATE asignaciones_carga SET id_maquinista = :idMaquinista, estado_fase = 'EN_PROCESO', fecha_asignacion = NOW() WHERE id_asignacion = :idAsignacion AND estado_fase = 'PENDIENTE'", nativeQuery = true)
    int asignarMaquinista(@Param("idAsignacion") int idAsignacion, @Param("idMaquinista") int idMaquinista);

    @Modifying
    @Transactional
    @Query(value = "UPDATE asignaciones_carga SET id_maquinista = :idMaquinista, fecha_asignacion = NOW() WHERE id_asignacion = :idAsignacion AND estado_fase = 'EN_PROCESO'", nativeQuery = true)
    int reasignarMaquinista(@Param("idAsignacion") int idAsignacion, @Param("idMaquinista") int idMaquinista);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE asignaciones_carga SET estado_fase = 'COMPLETADA', fecha_completado = NOW(), piezas_completadas = :piezasCompletadas WHERE id_asignacion = :idAsignacion AND estado_fase = 'EN_PROCESO'", nativeQuery = true)
    int completarFase(@Param("idAsignacion") int idAsignacion, @Param("piezasCompletadas") int piezasCompletadas);
}
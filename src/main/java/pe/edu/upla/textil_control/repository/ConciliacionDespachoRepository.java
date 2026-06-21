package pe.edu.upla.textil_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upla.textil_control.model.ConciliacionDespacho;
import java.util.List;

public interface ConciliacionDespachoRepository extends JpaRepository<ConciliacionDespacho, Integer> {

    @Query(value = """
        SELECT
            COALESCE(cd.id_conciliacion, 0)           AS idConciliacion,
            ot.id_ot                                  AS idOt,
            ot.cantidad_est                           AS cantidadEstimada,
            COALESCE(cd.cantidad_final, 0)            AS cantidadFinal,
            COALESCE(cd.diferencia, 0)                AS diferencia,
            COALESCE(cd.estado, 'PENDIENTE')          AS estado,
            COALESCE(cd.id_responsable, 0)            AS idResponsable,
            cd.fecha_conciliacion                     AS fechaConciliacion,
            cd.fecha_despacho                         AS fechaDespacho,
            cd.observaciones,
            ot.codigo_ot                              AS codigoOt,
            ot.cliente,
            mp.nombre                                 AS nombreModelo,
            CONCAT(u.nombre, ' ', u.apellido)         AS nombreResponsable,
            
            -- 🔥 AQUÍ RESTAURAMOS LA AUTOMATIZACIÓN DEL MVC 🔥
            COALESCE((SELECT piezas_completadas FROM asignaciones_carga ac WHERE ac.id_ot = ot.id_ot AND ac.tipo_tarea = 'ENSAMBLAJE' LIMIT 1), 0) AS cantidadEnsamblaje
            
        FROM orden_trabajo ot
        JOIN modelos_prenda mp ON ot.id_modelo = mp.id_modelo
        LEFT JOIN conciliacion_despacho cd ON cd.id_ot = ot.id_ot
        LEFT JOIN usuarios u ON cd.id_responsable = u.id_usuario
        WHERE ot.estado = 'FINALIZADA'
        ORDER BY ot.fecha_crea DESC
    """, nativeQuery = true)
    List<ConciliacionDespachoResumenDTO> listarLotesParaDespacho();

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO conciliacion_despacho
            (id_ot, cantidad_final, diferencia, estado, id_responsable, fecha_conciliacion, observaciones)
        VALUES (:idOt, :cantidadFinal, :diferencia, :estado, :idResponsable, NOW(), :observaciones)
        ON DUPLICATE KEY UPDATE
            cantidad_final = VALUES(cantidad_final),
            diferencia = VALUES(diferencia),
            estado = VALUES(estado),
            id_responsable = VALUES(id_responsable),
            fecha_conciliacion = NOW(),
            observaciones = VALUES(observaciones)
    """, nativeQuery = true)
    int insertarConciliacion(@Param("idOt") int idOt, @Param("cantidadFinal") int cantidadFinal, @Param("diferencia") int diferencia, @Param("estado") String estado, @Param("idResponsable") int idResponsable, @Param("observaciones") String observaciones);

    @Modifying
    @Transactional
    @Query(value = "UPDATE conciliacion_despacho SET estado = 'DESPACHADO', fecha_despacho = NOW() WHERE id_conciliacion = :idConciliacion AND estado IN ('CONCILIADO_OK', 'MERMA_DETECTADA')", nativeQuery = true)
    int confirmarDespacho(@Param("idConciliacion") int idConciliacion);

    ConciliacionDespacho findByIdOt(Integer idOt);
}
package pe.edu.upla.textil_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upla.textil_control.model.OrdenTrabajo;

import java.util.List;

/**
 * Repository: Reportes y Analíticas
 *
 * Migración del ReporteDAO.java (Java EE / JDBC manual) a Spring Data JPA.
 * SQL PRESERVADO ÍNTEGRAMENTE del original.
 *
 * Extiende JpaRepository<OrdenTrabajo, Integer> como base técnica
 * ya que las queries principales parten de la tabla orden_trabajo.
 */
@Repository
public interface ReportesRepository extends JpaRepository<OrdenTrabajo, Integer> {

    // ── EFICIENCIA GLOBAL ─────────────────────────────────────────────────────

    /**
     * Estados de OTs con conteo — SQL idéntico a ReporteDAO.obtenerEficienciaGlobal()
     */
    @Query(value = """
        SELECT estado AS estado, COUNT(*) AS total
        FROM orden_trabajo
        GROUP BY estado
        """, nativeQuery = true)
    List<ReporteEficienciaDTO> obtenerEficienciaGlobal();

    // ── MERMAS ────────────────────────────────────────────────────────────────

    /**
     * Merma agrupada por OT para todos (admin/supervisor) — SQL idéntico a ReporteDAO.obtenerMermaPorOT() sin filtro
     */
    @Query(value = """
        SELECT ot.codigo_ot AS codigoOt,
               SUM(m.peso_utilizado_kg) AS pesoUtilizado,
               SUM(m.peso_merma_kg)    AS pesoMerma
        FROM mermas m
        JOIN orden_trabajo ot ON m.id_ot = ot.id_ot
        GROUP BY ot.codigo_ot
        ORDER BY ot.codigo_ot
        """, nativeQuery = true)
    List<ReporteMermaDTO> obtenerMermaPorOTGlobal();

    /**
     * Merma agrupada por OT filtrada por tizador — SQL idéntico a ReporteDAO.obtenerMermaPorOT() con filtro
     */
    @Query(value = """
        SELECT ot.codigo_ot AS codigoOt,
               SUM(m.peso_utilizado_kg) AS pesoUtilizado,
               SUM(m.peso_merma_kg)    AS pesoMerma
        FROM mermas m
        JOIN orden_trabajo ot ON m.id_ot = ot.id_ot
        WHERE m.id_tizador = :idUsuario
        GROUP BY ot.codigo_ot
        ORDER BY ot.codigo_ot
        """, nativeQuery = true)
    List<ReporteMermaDTO> obtenerMermaPorOTPorTizador(@Param("idUsuario") int idUsuario);

    // ── TIEMPOS MAQUINISTAS ───────────────────────────────────────────────────

    /**
     * Tiempos de todos los maquinistas por OT — SQL idéntico a ReporteDAO.obtenerTiemposMaquinistasPorOT() sin filtro
     */
    @Query(value = """
        SELECT ot.codigo_ot AS codigoOt,
               CONCAT(u.nombre, ' ', u.apellido) AS maquinista,
               DATE_FORMAT(MIN(ac.fecha_asignacion), '%d/%m/%Y %H:%i') AS inicioReal,
               DATE_FORMAT(MAX(ac.fecha_completado), '%d/%m/%Y %H:%i') AS finReal,
               TIMESTAMPDIFF(MINUTE, MIN(ac.fecha_asignacion), MAX(ac.fecha_completado)) AS minutosAbsolutos,
               SUM(TIMESTAMPDIFF(MINUTE, ac.fecha_asignacion, ac.fecha_completado)) AS minutosTrabajados
        FROM asignaciones_carga ac
        JOIN orden_trabajo ot ON ac.id_ot = ot.id_ot
        JOIN usuarios u        ON ac.id_maquinista = u.id_usuario
        WHERE ac.estado_fase IN ('COMPLETADA', 'REGISTRADO')
          AND ac.fecha_completado IS NOT NULL
        GROUP BY ot.codigo_ot, maquinista
        ORDER BY ot.codigo_ot, maquinista
        """, nativeQuery = true)
    List<ReporteTiempoMaquinistaDTO> obtenerTiemposMaquinistasGlobal();

    /**
     * Tiempos de un maquinista específico — SQL idéntico a ReporteDAO.obtenerTiemposMaquinistasPorOT() con filtro
     */
    @Query(value = """
        SELECT ot.codigo_ot AS codigoOt,
               CONCAT(u.nombre, ' ', u.apellido) AS maquinista,
               DATE_FORMAT(MIN(ac.fecha_asignacion), '%d/%m/%Y %H:%i') AS inicioReal,
               DATE_FORMAT(MAX(ac.fecha_completado), '%d/%m/%Y %H:%i') AS finReal,
               TIMESTAMPDIFF(MINUTE, MIN(ac.fecha_asignacion), MAX(ac.fecha_completado)) AS minutosAbsolutos,
               SUM(TIMESTAMPDIFF(MINUTE, ac.fecha_asignacion, ac.fecha_completado)) AS minutosTrabajados
        FROM asignaciones_carga ac
        JOIN orden_trabajo ot ON ac.id_ot = ot.id_ot
        JOIN usuarios u        ON ac.id_maquinista = u.id_usuario
        WHERE ac.estado_fase IN ('COMPLETADA', 'REGISTRADO')
          AND ac.fecha_completado IS NOT NULL
          AND ac.id_maquinista = :idUsuario
        GROUP BY ot.codigo_ot, maquinista
        ORDER BY ot.codigo_ot, maquinista
        """, nativeQuery = true)
    List<ReporteTiempoMaquinistaDTO> obtenerTiemposMaquinistaPorId(@Param("idUsuario") int idUsuario);

    // ── FALLAS POR TELA ───────────────────────────────────────────────────────

    /**
     * Fallas agrupadas por tipo de tela para todos — SQL idéntico a ReporteDAO.obtenerFallasPorTela() sin filtro
     */
    @Query(value = """
        SELECT t.codigo_tela AS codigoTela, COUNT(ft.id_falla) AS fallasTotales
        FROM fallas_tela ft
        JOIN telas t ON ft.id_tela = t.id_tela
        GROUP BY t.codigo_tela
        ORDER BY fallasTotales DESC
        """, nativeQuery = true)
    List<ReporteFallaDTO> obtenerFallasPorTelaGlobal();

    /**
     * Fallas agrupadas por tipo de tela filtradas por tizador — SQL idéntico a ReporteDAO.obtenerFallasPorTela() con filtro
     */
    @Query(value = """
        SELECT t.codigo_tela AS codigoTela, COUNT(ft.id_falla) AS fallasTotales
        FROM fallas_tela ft
        JOIN telas t ON ft.id_tela = t.id_tela
        WHERE ft.id_tizador = :idUsuario
        GROUP BY t.codigo_tela
        ORDER BY fallasTotales DESC
        """, nativeQuery = true)
    List<ReporteFallaDTO> obtenerFallasPorTelaPorTizador(@Param("idUsuario") int idUsuario);

    // ── INVENTARIO TELAS ──────────────────────────────────────────────────────

    /**
     * Inventario de telas agrupado por estado de calidad — SQL idéntico a ReporteDAO.obtenerInventarioTelas()
     */
    @Query(value = """
        SELECT estado_calidad AS estado,
               COUNT(*)       AS cantidad,
               SUM(peso_real) AS pesoTotal,
               AVG(peso_real) AS pesoPromedio
        FROM telas
        GROUP BY estado_calidad
        """, nativeQuery = true)
    List<ReporteInventarioDTO> obtenerInventarioTelas();

    // ── CALIDAD VS PRODUCTIVIDAD ──────────────────────────────────────────────

    /**
     * Cruce calidad vs productividad por maquinista — SQL idéntico a ReporteDAO.obtenerCalidadVsProductividad()
     */
    @Query(value = """
        SELECT CONCAT(u.nombre, ' ', u.apellido) AS maquinista,
               SUM(TIMESTAMPDIFF(MINUTE, ac.fecha_asignacion, ac.fecha_completado)) AS minutos,
               COALESCE((SELECT COUNT(*)
                         FROM defectos_reproceso dr
                         WHERE dr.id_maquinista = u.id_usuario), 0) AS defectos
        FROM asignaciones_carga ac
        JOIN usuarios u ON ac.id_maquinista = u.id_usuario
        WHERE ac.estado_fase IN ('COMPLETADA', 'REGISTRADO')
          AND ac.fecha_completado IS NOT NULL
        GROUP BY u.id_usuario, u.nombre, u.apellido
        """, nativeQuery = true)
    List<ReporteCalidadProductividadDTO> obtenerCalidadVsProductividad();

    // ── OTS PROBLEMÁTICAS ─────────────────────────────────────────────────────

    /**
     * Top 15 OTs con más defectos y merma — SQL idéntico a ReporteDAO.obtenerOTsProblematicas()
     */
    @Query(value = """
        SELECT ot.codigo_ot AS codigoOt,
               ot.cantidad_est AS cantidadEst,
               COALESCE(SUM(m.peso_merma_kg), 0) AS merma,
               COALESCE((SELECT COUNT(*) FROM defectos_reproceso dr WHERE dr.id_ot = ot.id_ot), 0) AS defectos
        FROM orden_trabajo ot
        LEFT JOIN mermas m ON ot.id_ot = m.id_ot
        GROUP BY ot.id_ot, ot.codigo_ot, ot.cantidad_est
        ORDER BY defectos DESC, merma DESC
        LIMIT 15
        """, nativeQuery = true)
    List<ReporteOTProblematicaDTO> obtenerOTsProblematicas();

    // ── DESVIACIONES DE DESPACHO ──────────────────────────────────────────────

    /**
     * Desviaciones planeado vs final — SQL idéntico a ReporteDAO.obtenerDespacho()
     */
    @Query(value = """
        SELECT ot.codigo_ot AS codigoOt,
               ot.cantidad_est AS estimada,
               cd.cantidad_final AS final,
               cd.diferencia AS diferencia
        FROM conciliacion_despacho cd
        JOIN orden_trabajo ot ON cd.id_ot = ot.id_ot
        """, nativeQuery = true)
    List<ReporteDespachoDTO> obtenerDesviacionesDespacho();

    // ── RENDIMIENTO PERSONAL MAQUINISTA ───────────────────────────────────────

    /**
     * Rendimiento personal del maquinista logueado — SQL idéntico a ReporteDAO.obtenerRendimientoMaquinista()
     */
    @Query(value = """
        SELECT ot.codigo_ot AS codigoOt,
               ot.cantidad_est AS cantidadEst,
               DATE_FORMAT(ac.fecha_asignacion, '%d/%m/%Y') AS fechaAsignada,
               TIMESTAMPDIFF(MINUTE, ac.fecha_asignacion, ac.fecha_completado) AS minutosTrabajados,
               DATEDIFF(ac.fecha_completado, ac.fecha_asignacion) AS diasRetraso,
               COALESCE((SELECT COUNT(*)
                         FROM defectos_reproceso dr
                         WHERE dr.id_ot = ot.id_ot AND dr.id_maquinista = :idUsuario), 0) AS defectosPropios
        FROM asignaciones_carga ac
        JOIN orden_trabajo ot ON ac.id_ot = ot.id_ot
        WHERE ac.id_maquinista = :idUsuario
          AND ac.estado_fase IN ('COMPLETADA', 'REGISTRADO')
          AND ac.fecha_completado IS NOT NULL
        ORDER BY ac.fecha_completado DESC
        """, nativeQuery = true)
    List<ReporteRendimientoDTO> obtenerRendimientoMaquinista(@Param("idUsuario") int idUsuario);
}

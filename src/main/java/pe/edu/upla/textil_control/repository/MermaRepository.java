package pe.edu.upla.textil_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upla.textil_control.model.Merma;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository: mermas
 * HU04: Registro de Merma por Tipo de Tejido
 *
 * SQL PRESERVADO ÍNTEGRAMENTE del MermaDAO original.
 */
@Repository
public interface MermaRepository extends JpaRepository<Merma, Integer> {

    // ── LECTURA ───────────────────────────────────────────────

    /** Todas las mermas — Admin ve todo (SQL idéntico a MermaDAO.listarTodas) */
    @Query(value = """
        SELECT m.id_merma AS idMerma, m.id_tela AS idTela, m.id_ot AS idOt,
               m.id_tizador AS idTizador, m.fase, m.peso_utilizado_kg AS pesoUtilizadoKg,
               m.peso_merma_kg AS pesomermaKg, m.porcentaje_merma AS porcentajeMerma,
               m.observaciones, m.fecha_registro AS fechaRegistro,
               t.codigo_tela AS codigoTela, t.tipo_tejido AS tipoTejido,
               ot.codigo_ot AS codigoOt, ot.cliente,
               CONCAT(u.nombre,' ',u.apellido) AS nombreTizador
        FROM mermas m
        JOIN telas t         ON m.id_tela    = t.id_tela
        JOIN orden_trabajo ot ON m.id_ot      = ot.id_ot
        JOIN usuarios u      ON m.id_tizador  = u.id_usuario
        ORDER BY m.fecha_registro DESC
        """, nativeQuery = true)
    List<MermaResumenDTO> listarTodas();

    /** Mermas del tizador logueado (SQL idéntico a MermaDAO.listarPorTizador) */
    @Query(value = """
        SELECT m.id_merma AS idMerma, m.id_tela AS idTela, m.id_ot AS idOt,
               m.id_tizador AS idTizador, m.fase, m.peso_utilizado_kg AS pesoUtilizadoKg,
               m.peso_merma_kg AS pesomermaKg, m.porcentaje_merma AS porcentajeMerma,
               m.observaciones, m.fecha_registro AS fechaRegistro,
               t.codigo_tela AS codigoTela, t.tipo_tejido AS tipoTejido,
               ot.codigo_ot AS codigoOt, ot.cliente,
               CONCAT(u.nombre,' ',u.apellido) AS nombreTizador
        FROM mermas m
        JOIN telas t         ON m.id_tela    = t.id_tela
        JOIN orden_trabajo ot ON m.id_ot      = ot.id_ot
        JOIN usuarios u      ON m.id_tizador  = u.id_usuario
        WHERE m.id_tizador = :idTizador
        ORDER BY m.fecha_registro DESC
        """, nativeQuery = true)
    List<MermaResumenDTO> listarPorTizador(@Param("idTizador") int idTizador);

    /** Mermas filtradas por OT (SQL idéntico a MermaDAO.listarPorOt) */
    @Query(value = """
        SELECT m.id_merma AS idMerma, m.id_tela AS idTela, m.id_ot AS idOt,
               m.id_tizador AS idTizador, m.fase, m.peso_utilizado_kg AS pesoUtilizadoKg,
               m.peso_merma_kg AS pesomermaKg, m.porcentaje_merma AS porcentajeMerma,
               m.observaciones, m.fecha_registro AS fechaRegistro,
               t.codigo_tela AS codigoTela, t.tipo_tejido AS tipoTejido,
               ot.codigo_ot AS codigoOt, ot.cliente,
               CONCAT(u.nombre,' ',u.apellido) AS nombreTizador
        FROM mermas m
        JOIN telas t         ON m.id_tela    = t.id_tela
        JOIN orden_trabajo ot ON m.id_ot      = ot.id_ot
        JOIN usuarios u      ON m.id_tizador  = u.id_usuario
        WHERE m.id_ot = :idOt
        ORDER BY m.fecha_registro DESC
        """, nativeQuery = true)
    List<MermaResumenDTO> listarPorOt(@Param("idOt") int idOt);

    /** CUS 4.2: Porcentaje total de merma acumulado por OT (SQL idéntico a MermaDAO.calcularPorcentajePorOt) */
    @Query(value = """
        SELECT CASE WHEN SUM(peso_utilizado_kg) > 0
               THEN ROUND(SUM(peso_merma_kg) / SUM(peso_utilizado_kg) * 100, 3)
               ELSE 0 END AS pct
        FROM mermas WHERE id_ot = :idOt
        """, nativeQuery = true)
    BigDecimal calcularPorcentajePorOt(@Param("idOt") int idOt);

    /** Telas disponibles para merma — ACEPTADO/OBSERVADO + OT EN_PROCESO (SQL idéntico a MermaDAO) */
    @Query(value = """
        SELECT t.id_tela AS idTela, t.codigo_tela AS codigoTela,
               t.tipo_tejido AS tipoTejido, t.color,
               t.peso_real AS pesoReal, t.num_rollos AS numRollos,
               ot.id_ot AS idOt, ot.codigo_ot AS codigoOt, ot.cliente
        FROM telas t
        JOIN orden_trabajo ot ON t.id_ot = ot.id_ot
        WHERE t.estado_calidad IN ('ACEPTADO','OBSERVADO')
          AND ot.estado = 'EN_PROCESO'
        ORDER BY t.codigo_tela
        """, nativeQuery = true)
    List<TelaMermaDTO> listarTelasParaMerma();

    /** OTs activas para el filtro del listado (SQL idéntico a MermaDAO.listarOtsConMermas) */
    @Query(value = """
        SELECT DISTINCT ot.id_ot AS idOt, ot.codigo_ot AS codigoOt, ot.cliente
        FROM mermas m
        JOIN orden_trabajo ot ON m.id_ot = ot.id_ot
        ORDER BY ot.codigo_ot
        """, nativeQuery = true)
    List<OtResumenDTO> listarOtsConMermas();

    // 🌟 NUEVO: Obtiene el estado de la OT asociada a una merma específica
    @Query(value = """
        SELECT ot.estado 
        FROM mermas m
        JOIN orden_trabajo ot ON m.id_ot = ot.id_ot
        WHERE m.id_merma = :idMerma
        """, nativeQuery = true)
    String obtenerEstadoOtPorMerma(@Param("idMerma") int idMerma);
}

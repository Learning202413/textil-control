package pe.edu.upla.textil_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upla.textil_control.model.FallaTela;

import java.util.List;

/**
 * Repository: fallas_tela
 * HU02: Mapeo Digital de Imperfecciones y Fallas en la Tela
 *
 * SQL PRESERVADO ÍNTEGRAMENTE del FallaTelaDAO original.
 */
@Repository
public interface FallaTelaRepository extends JpaRepository<FallaTela, Integer> {

    // ── LECTURA ───────────────────────────────────────────────

    /** Todas las fallas — Admin ve todo (SQL idéntico a FallaTelaDAO.listarTodas) */
    @Query(value = """
        SELECT ft.id_falla AS idFalla, ft.id_tela AS idTela, ft.id_tizador AS idTizador,
               ft.tipo_falla AS tipoFalla, ft.posicion_rollo AS posicionRollo,
               ft.posicion_metro AS posicionMetro, ft.ancho_cm AS anchoCm,
               ft.largo_cm AS largoCm, ft.descripcion, 
               CAST(ft.es_area_no_apta AS SIGNED) AS esAreaNoApta,
               DATE_FORMAT(ft.fecha_registro, '%d/%m/%y %H:%i') AS fechaRegistroFormateada,
               t.codigo_tela AS codigoTela, t.tipo_tejido AS tipoTejido,
               ot.codigo_ot AS codigoOt,
               CONCAT(u.nombre,' ',u.apellido) AS nombreTizador
        FROM fallas_tela ft
        JOIN telas t        ON ft.id_tela    = t.id_tela
        JOIN orden_trabajo ot ON t.id_ot     = ot.id_ot
        JOIN usuarios u     ON ft.id_tizador = u.id_usuario
        ORDER BY ft.fecha_registro DESC
        """, nativeQuery = true)
    List<FallaTelaResumenDTO> listarTodas();

    /** Fallas registradas por un tizador específico (SQL idéntico a FallaTelaDAO.listarPorTizador) */
    @Query(value = """
        SELECT ft.id_falla AS idFalla, ft.id_tela AS idTela, ft.id_tizador AS idTizador,
               ft.tipo_falla AS tipoFalla, ft.posicion_rollo AS posicionRollo,
               ft.posicion_metro AS posicionMetro, ft.ancho_cm AS anchoCm,
               ft.largo_cm AS largoCm, ft.descripcion, 
               CAST(ft.es_area_no_apta AS SIGNED) AS esAreaNoApta,
               DATE_FORMAT(ft.fecha_registro, '%d/%m/%y %H:%i') AS fechaRegistroFormateada,
               t.codigo_tela AS codigoTela, t.tipo_tejido AS tipoTejido,
               ot.codigo_ot AS codigoOt,
               CONCAT(u.nombre,' ',u.apellido) AS nombreTizador
        FROM fallas_tela ft
        JOIN telas t        ON ft.id_tela    = t.id_tela
        JOIN orden_trabajo ot ON t.id_ot     = ot.id_ot
        JOIN usuarios u     ON ft.id_tizador = u.id_usuario
        WHERE ft.id_tizador = :idTizador
        ORDER BY ft.fecha_registro DESC
        """, nativeQuery = true)
    List<FallaTelaResumenDTO> listarPorTizador(@Param("idTizador") int idTizador);

    /** Todas las fallas de una tela concreta (SQL idéntico a FallaTelaDAO.listarPorTela) */
    @Query(value = """
        SELECT ft.id_falla AS idFalla, ft.id_tela AS idTela, ft.id_tizador AS idTizador,
               ft.tipo_falla AS tipoFalla, ft.posicion_rollo AS posicionRollo,
               ft.posicion_metro AS posicionMetro, ft.ancho_cm AS anchoCm,
               ft.largo_cm AS largoCm, ft.descripcion, 
               CAST(ft.es_area_no_apta AS SIGNED) AS esAreaNoApta,
               DATE_FORMAT(ft.fecha_registro, '%d/%m/%y %H:%i') AS fechaRegistroFormateada,
               t.codigo_tela AS codigoTela, t.tipo_tejido AS tipoTejido,
               ot.codigo_ot AS codigoOt,
               CONCAT(u.nombre,' ',u.apellido) AS nombreTizador
        FROM fallas_tela ft
        JOIN telas t        ON ft.id_tela    = t.id_tela
        JOIN orden_trabajo ot ON t.id_ot     = ot.id_ot
        JOIN usuarios u     ON ft.id_tizador = u.id_usuario
        WHERE ft.id_tela = :idTela
        ORDER BY ft.posicion_rollo, ft.posicion_metro
        """, nativeQuery = true)
    List<FallaTelaResumenDTO> listarPorTela(@Param("idTela") int idTela);

    // 🌟 1. ACTUALIZADO: Filtra telas por calidad y por el estado de la OT
    @Query(value = """
        SELECT t.id_tela AS idTela, t.codigo_tela AS codigoTela, t.tipo_tejido AS tipoTejido,
               t.color, t.num_rollos AS numRollos, ot.codigo_ot AS codigoOt
        FROM telas t
        JOIN orden_trabajo ot ON t.id_ot = ot.id_ot
        WHERE t.estado_calidad IN ('ACEPTADO','OBSERVADO')
          AND ot.estado IN ('CREADA', 'EN_PROCESO')
        ORDER BY t.codigo_tela
        """, nativeQuery = true)
    List<TelaMapeoDTO> listarTelasParaMapeo();

    // 🌟 2. NUEVO: Obtiene el estado de la OT asociada a una falla específica
    @Query(value = """
        SELECT ot.estado 
        FROM fallas_tela ft
        JOIN telas t ON ft.id_tela = t.id_tela
        JOIN orden_trabajo ot ON t.id_ot = ot.id_ot
        WHERE ft.id_falla = :idFalla
        """, nativeQuery = true)
    String obtenerEstadoOtPorFalla(@Param("idFalla") int idFalla);

    /** Telas que tienen al menos una falla registrada (SQL idéntico a FallaTelaDAO.listarTelasConFallas) */
    @Query(value = """
        SELECT DISTINCT t.id_tela AS idTela, t.codigo_tela AS codigoTela,
               t.tipo_tejido AS tipoTejido, t.color, ot.codigo_ot AS codigoOt
        FROM fallas_tela ft
        JOIN telas t        ON ft.id_tela = t.id_tela
        JOIN orden_trabajo ot ON t.id_ot  = ot.id_ot
        ORDER BY t.codigo_tela
        """, nativeQuery = true)
    List<TelaMapeoDTO> listarTelasConFallas();

    /** Conteo de fallas por tipo para una tela — resumen visual (SQL idéntico a FallaTelaDAO.contarFallasPorTipoYTela) */
    @Query(value = "SELECT COUNT(*) FROM fallas_tela WHERE id_tela = :idTela AND tipo_falla = :tipoFalla", nativeQuery = true)
    int contarFallasPorTipoYTela(@Param("idTela") int idTela, @Param("tipoFalla") String tipoFalla);
}

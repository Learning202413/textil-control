package pe.edu.upla.textil_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upla.textil_control.model.OrdenTrabajo;
import java.util.List;

public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Integer> {

    @Query(value = "SELECT codigo_ot FROM orden_trabajo WHERE codigo_ot LIKE :prefijo ORDER BY id_ot DESC LIMIT 1", nativeQuery = true)
    String findMaxCodigoByPrefijo(@Param("prefijo") String prefijo);

    @Query(value = """
        SELECT ot.id_ot AS idOt, ot.codigo_ot AS codigoOt, ot.cliente, ot.cantidad_est AS cantidadEst,
               ot.estado, ot.fecha_crea AS fechaCrea, ot.id_modelo AS idModelo, 
               CONCAT(u.nombre, ' ', u.apellido) AS nombreResponsable,
               m.nombre AS nombreModelo
        FROM orden_trabajo ot
        LEFT JOIN usuarios u ON ot.id_responsable = u.id_usuario
        LEFT JOIN modelos_prenda m ON ot.id_modelo = m.id_modelo
        ORDER BY ot.id_ot DESC
    """, nativeQuery = true)
    List<OrdenTrabajoResumenDTO> listarResumen();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM orden_trabajo WHERE id_ot = :idOt AND estado = 'CREADA'", nativeQuery = true)
    int deleteSiCreada(@Param("idOt") Integer idOt);

    @Query(value = "SELECT COUNT(*) FROM telas WHERE id_ot = :idOt", nativeQuery = true)
    long countTelasByIdOt(@Param("idOt") Integer idOt);

    // 🌟 SEGUNDO CANDADO DE CALIDAD: Cuenta cuántas telas asociadas a la OT están aprobadas
    @Query(value = "SELECT COUNT(*) FROM telas WHERE id_ot = :idOt AND estado_calidad = 'ACEPTADO'", nativeQuery = true)
    long countTelasAceptadasByIdOt(@Param("idOt") Integer idOt);

    // 🌟 TERCER CANDADO DE CONFECCIÓN: Cuenta telas de la OT que requieren reposo obligatorio pero aún no terminan (no están APTO_CORTE)
    @Query(value = """
        SELECT COUNT(*) FROM telas t 
        WHERE t.id_ot = :idOt 
          AND t.requiere_reposo = 1 
          AND t.id_tela NOT IN (
              SELECT tr.id_tela FROM tiempos_reposo tr WHERE tr.estado = 'APTO_CORTE'
          )
        """, nativeQuery = true)
    long countTelasSinReposoCompletado(@Param("idOt") Integer idOt);
}
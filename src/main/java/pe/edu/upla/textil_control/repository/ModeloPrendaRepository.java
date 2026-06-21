package pe.edu.upla.textil_control.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upla.textil_control.model.ModeloPrenda;

import java.util.List;

@Repository
public interface ModeloPrendaRepository extends JpaRepository<ModeloPrenda, Integer> {

    @Query(value = """
        SELECT m.id_modelo AS idModelo, m.nombre AS nombre, m.temporada AS temporada, 
               COUNT(DISTINCT p.nombre_pieza) AS totalPiezas,
               (SELECT COUNT(*) FROM orden_trabajo ot WHERE ot.id_modelo = m.id_modelo) > 0 AS enUso
        FROM modelos_prenda m
        LEFT JOIN piezas_modelo p ON m.id_modelo = p.id_modelo
        GROUP BY m.id_modelo, m.nombre, m.temporada
        ORDER BY m.nombre
        """, nativeQuery = true)
    List<ModeloResumenDTO> listarResumen();

    @Query(value = "SELECT COUNT(*) FROM orden_trabajo WHERE id_modelo = :idModelo", nativeQuery = true)
    long countOrdenesByModelo(@Param("idModelo") Integer idModelo);

    // Consultas nativas para manejar la tabla intermedia
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM pieza_ruta_fase WHERE id_pieza = :idPieza", nativeQuery = true)
    void deleteRutasByPieza(@Param("idPieza") Integer idPieza);

    @Modifying
    @Transactional
    @Query(value = "DELETE prf FROM pieza_ruta_fase prf JOIN piezas_modelo p ON prf.id_pieza = p.id_pieza WHERE p.id_modelo = :idModelo", nativeQuery = true)
    void deleteRutasByModelo(@Param("idModelo") Integer idModelo);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO pieza_ruta_fase (id_pieza, id_fase) VALUES (:idPieza, :idFase)", nativeQuery = true)
    void insertRutaPieza(@Param("idPieza") Integer idPieza, @Param("idFase") Integer idFase);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO pieza_ruta_fase (id_modelo, id_pieza, id_fase) VALUES (:idModelo, NULL, :idFase)", nativeQuery = true)
    void insertRutaGlobal(@Param("idModelo") Integer idModelo, @Param("idFase") Integer idFase);
}
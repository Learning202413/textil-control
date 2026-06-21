package pe.edu.upla.textil_control.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upla.textil_control.model.FaseProduccion;

@Repository
public interface FaseProduccionRepository extends JpaRepository<FaseProduccion, Integer> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE fases_produccion SET orden = orden + 1 WHERE orden >= :orden", nativeQuery = true)
    void desplazarOrdenes(@Param("orden") Integer orden);

    @Query(value = "SELECT COALESCE(MAX(orden), 0) FROM fases_produccion", nativeQuery = true)
    int getMaxOrden();
}
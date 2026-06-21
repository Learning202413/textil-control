package pe.edu.upla.textil_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upla.textil_control.model.PiezaModelo;
import java.util.List;

@Repository
public interface PiezaModeloRepository extends JpaRepository<PiezaModelo, Integer> {
    List<PiezaModelo> findByIdModelo(Integer idModelo);
    void deleteByIdModelo(Integer idModelo);
}
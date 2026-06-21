package pe.edu.upla.textil_control.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upla.textil_control.model.FotoTela;
import java.util.List;

@Repository
public interface FotoTelaRepository extends JpaRepository<FotoTela, Integer> {
    List<FotoTela> findByIdTelaOrderByFechaSubidaAsc(Integer idTela);
}
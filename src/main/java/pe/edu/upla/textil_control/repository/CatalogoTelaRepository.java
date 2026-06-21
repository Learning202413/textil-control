package pe.edu.upla.textil_control.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upla.textil_control.model.CatalogoTela;

@Repository
public interface CatalogoTelaRepository extends JpaRepository<CatalogoTela, Integer> {

    // Reemplaza a tieneUsoEnInventario()
    @Query(value = "SELECT COUNT(*) FROM telas WHERE id_catalogo_tela = :idCatalogo", nativeQuery = true)
    int countTelasByCatalogoId(@Param("idCatalogo") Integer idCatalogo);

    // Reemplaza a actualizarReposoEnTelas()
    @Modifying
    @Transactional
    @Query(value = "UPDATE telas SET requiere_reposo = :nuevoValor WHERE id_catalogo_tela = :idCatalogo", nativeQuery = true)
    void actualizarReposoEnTelas(@Param("idCatalogo") Integer idCatalogo, @Param("nuevoValor") boolean nuevoValor);
}
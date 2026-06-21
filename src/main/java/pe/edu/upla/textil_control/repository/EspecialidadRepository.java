package pe.edu.upla.textil_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.upla.textil_control.model.Especialidad;

import java.util.List;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Integer> {

    // Lista todas las especialidades ordenadas alfabéticamente
    List<Especialidad> findAllByOrderByNombreAsc();

    @Query(value = "SELECT e.* FROM especialidades e JOIN usuario_especialidad ue ON e.id_especialidad = ue.id_especialidad WHERE ue.id_usuario = :idUsuario ORDER BY e.nombre", nativeQuery = true)
    List<Especialidad> findByUsuarioId(@org.springframework.data.repository.query.Param("idUsuario") Integer idUsuario);

    @org.springframework.data.jpa.repository.Modifying
    @jakarta.transaction.Transactional
    @Query(value = "DELETE FROM usuario_especialidad WHERE id_usuario = :idUsuario", nativeQuery = true)
    void deleteEspecialidadesByUsuario(@org.springframework.data.repository.query.Param("idUsuario") Integer idUsuario);

    @org.springframework.data.jpa.repository.Modifying
    @jakarta.transaction.Transactional
    @Query(value = "INSERT INTO usuario_especialidad (id_usuario, id_especialidad) VALUES (:idUsuario, :idEspecialidad)", nativeQuery = true)
    void insertUsuarioEspecialidad(@org.springframework.data.repository.query.Param("idUsuario") Integer idUsuario, @org.springframework.data.repository.query.Param("idEspecialidad") Integer idEspecialidad);
}
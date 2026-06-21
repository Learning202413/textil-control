package pe.edu.upla.textil_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upla.textil_control.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Para el Login
    Optional<Usuario> findByUsernameAndActivoTrue(String username);

    // Para listar usuarios ordenados por nombre
    List<Usuario> findAllByOrderByNombreAsc();

    // Verificar si tiene órdenes de trabajo como responsable
    @Query(value = "SELECT COUNT(*) FROM orden_trabajo WHERE id_responsable = :idUsuario", nativeQuery = true)
    int countOrdenesTrabajoByResponsable(@Param("idUsuario") Integer idUsuario);

    // Verificar si tiene telas registradas
    @Query(value = "SELECT COUNT(*) FROM telas WHERE id_registrador = :idUsuario", nativeQuery = true)
    int countTelasByRegistrador(@Param("idUsuario") Integer idUsuario);

    // Borrar especialidades del usuario antes de eliminarlo
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM usuario_especialidad WHERE id_usuario = :idUsuario", nativeQuery = true)
    void deleteEspecialidadesByUsuario(@Param("idUsuario") Integer idUsuario);

    // Buscar usuarios por su ID de Rol
    List<Usuario> findByIdRol(Integer idRol);

    // Para evitar eliminar maquinistas con órdenes asignadas
    @Query(value = "SELECT COUNT(*) FROM orden_trabajo WHERE id_usuario = :idUsuario", nativeQuery = true)
    long countActividadesByUsuario(@org.springframework.data.repository.query.Param("idUsuario") Integer idUsuario);

    // --- MÉTODOS PARA BLOQUEO DE LOGIN ---

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.intentosFallidos = :intentos, u.bloqueadoHasta = :bloqueo WHERE u.idUsuario = :idUsuario")
    void actualizarIntentosYBloqueo(@Param("idUsuario") Integer idUsuario, @Param("intentos") Integer intentos, @Param("bloqueo") java.time.LocalDateTime bloqueo);

    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.intentosFallidos = 0, u.bloqueadoHasta = NULL WHERE u.idUsuario = :idUsuario")
    void resetearIntentos(@Param("idUsuario") Integer idUsuario);
}

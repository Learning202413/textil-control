package pe.edu.upla.textil_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upla.textil_control.model.Notificacion;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    @Query(value = "SELECT * FROM notificaciones WHERE para_rol LIKE CONCAT('%', :rol, '%') AND leida = 0 ORDER BY fecha_creacion DESC", nativeQuery = true)
    List<Notificacion> listarNoLeidasPorRol(@Param("rol") String rol);

    @Query(value = "SELECT * FROM notificaciones WHERE para_rol LIKE CONCAT('%', :rol, '%') ORDER BY fecha_creacion DESC LIMIT :limite", nativeQuery = true)
    List<Notificacion> listarTodasPorRol(@Param("rol") String rol, @Param("limite") int limite);

    @Modifying
    @Transactional
    @Query(value = "UPDATE notificaciones SET leida = 1 WHERE id_notificacion = :id", nativeQuery = true)
    void marcarComoLeida(@Param("id") Integer id);
}
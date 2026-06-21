package pe.edu.upla.textil_control.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upla.textil_control.model.Permiso;

import java.util.List;
import java.util.Set;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Integer> {

    // 1. Reemplaza a obtenerCodigosPorRol() - Para el Login / Interceptor
    @Query(value = "SELECT p.codigo FROM permisos p JOIN rol_permiso rp ON p.id_permiso = rp.id_permiso WHERE rp.id_rol = :idRol", nativeQuery = true)
    Set<String> findCodigosByIdRol(@Param("idRol") Integer idRol);

    // 2. Reemplaza a obtenerModulosPorRol() - Para el menú dinámico
    @Query(value = "SELECT DISTINCT p.modulo FROM permisos p JOIN rol_permiso rp ON p.id_permiso = rp.id_permiso WHERE rp.id_rol = :idRol ORDER BY p.modulo", nativeQuery = true)
    List<String> findModulosByIdRol(@Param("idRol") Integer idRol);

    // 3. Utilidad para tu matriz de checkboxes (listarTodosConFlag)
    @Query(value = "SELECT id_permiso FROM rol_permiso WHERE id_rol = :idRol", nativeQuery = true)
    Set<Integer> findIdsPermisosByIdRol(@Param("idRol") Integer idRol);

    // --- NUEVO: Para guardar la matriz de checkboxes ---

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM rol_permiso WHERE id_rol = :idRol", nativeQuery = true)
    void deletePermisosByRol(@Param("idRol") Integer idRol);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO rol_permiso (id_rol, id_permiso) VALUES (:idRol, :idPermiso)", nativeQuery = true)
    void insertRolPermiso(@Param("idRol") Integer idRol, @Param("idPermiso") Integer idPermiso);
}
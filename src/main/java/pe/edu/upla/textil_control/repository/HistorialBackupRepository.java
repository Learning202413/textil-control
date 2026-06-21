package pe.edu.upla.textil_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.upla.textil_control.model.HistorialBackup;
import java.util.List;

@Repository
public interface HistorialBackupRepository extends JpaRepository<HistorialBackup, Integer> {

    // Regla de Negocio: SQL original preservado intacto
    @Query(value = """
        SELECT h.id_backup AS idBackup, h.fecha_solicitud AS fechaSolicitud, 
               h.usuario_solicitante AS usuarioSolicitante, h.nombre_archivo AS nombreArchivo, 
               h.tamanio_bytes AS tamanioBytes, h.estado AS estado, h.observaciones AS observaciones, 
               CONCAT(u.nombre,' ',u.apellido) AS nombreUsuario 
        FROM historial_backups h 
        JOIN usuarios u ON h.usuario_solicitante = u.id_usuario 
        ORDER BY h.fecha_solicitud DESC
    """, nativeQuery = true)
    List<HistorialBackupDTO> listarTodos();
}
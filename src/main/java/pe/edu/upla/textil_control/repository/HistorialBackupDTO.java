package pe.edu.upla.textil_control.repository;
import java.time.LocalDateTime; // <-- NUEVO IMPORT

public interface HistorialBackupDTO {
    Integer getIdBackup();
    LocalDateTime getFechaSolicitud(); // <-- CAMBIADO
    Integer getUsuarioSolicitante();
    String getNombreArchivo();
    Long getTamanioBytes();
    String getEstado();
    String getObservaciones();
    String getNombreUsuario();
}
package pe.edu.upla.textil_control.repository;
import java.time.LocalDateTime;
public interface TareaMaquinistaDTO {
    Integer getIdAsignacion();
    Integer getIdOt();
    Integer getIdPieza();
    Integer getIdFase();
    Integer getIdMaquinista();
    String getEstadoFase();
    LocalDateTime getFechaAsignacion(); // 🔥 CAMBIA Timestamp POR LocalDateTime
    LocalDateTime getFechaCompletado();
    Integer getCantidadPiezas();
    Integer getPiezasCompletadas();
    String getCodigoOt();
    String getNombrePieza();
    String getNombreFase();
    String getTipoTarea();
}
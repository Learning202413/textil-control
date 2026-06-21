package pe.edu.upla.textil_control.repository;
import java.time.LocalDateTime;
public interface FasePendienteDTO {
    Integer getIdAsignacion();
    Integer getIdOt();
    Integer getIdPieza();
    Integer getIdFase();
    Integer getIdMaquinista();
    String getEstadoFase();
    LocalDateTime getFechaAsignacion();
    Integer getCantidadPiezas();
    Integer getPiezasCompletadas();
    String getCodigoOt();
    LocalDateTime getFechaCrea();
    String getNombreModelo();
    String getNombrePieza();
    String getNombreFase();
    Integer getOrden();
    String getFasePreviaEstado();
    String getNombreMaquinista();
    String getTipoTarea();
}
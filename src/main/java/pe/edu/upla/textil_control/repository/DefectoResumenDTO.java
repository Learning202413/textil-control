package pe.edu.upla.textil_control.repository;
import java.time.LocalDateTime;
public interface DefectoResumenDTO {
    Integer getIdDefecto();
    Integer getIdOt();
    Integer getIdPieza();
    Integer getIdMaquinista();
    String getTipoFalla();
    String getObservaciones();
    LocalDateTime getFechaRegistro();
    String getCodigoOt();
    String getNombreModelo();
    String getNombrePieza();
    String getNombreMaquinista();
    String getEstado();
    Integer getCantidadFaltante();
    Integer getIdAsignacion();
    Boolean getGeneraReposicion();  // o Integer
}
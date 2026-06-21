package pe.edu.upla.textil_control.repository;
import java.time.LocalDateTime;

public interface OrdenTrabajoResumenDTO {
    Integer getIdOt();
    String getCodigoOt();
    String getCliente();
    Integer getCantidadEst();
    String getEstado();
    String getNombreResponsable();
    Integer getIdModelo();
    String getNombreModelo();
    LocalDateTime getFechaCrea();
}
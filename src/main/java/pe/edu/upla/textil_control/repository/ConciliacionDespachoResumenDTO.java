package pe.edu.upla.textil_control.repository;
import java.time.LocalDateTime;
public interface ConciliacionDespachoResumenDTO {
    Integer getIdConciliacion();
    Integer getIdOt();
    Integer getCantidadEstimada();
    Integer getCantidadFinal();
    Integer getDiferencia();
    String  getEstado();
    Integer getIdResponsable();
    LocalDateTime getFechaConciliacion(); // 🔥 Cambiar a LocalDateTime
    LocalDateTime getFechaDespacho();
    String  getObservaciones();
    String  getCodigoOt();
    String  getCliente();
    String  getNombreModelo();
    String  getNombreResponsable();

    // 🔥 Añadimos el campo que nos faltaba para jalar el dato de producción
    Integer getCantidadEnsamblaje();
}
package pe.edu.upla.textil_control.repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TelaResumenDTO {
    Integer getIdTela();
    Integer getIdOt();
    String getCodigoTela();
    String getOrigen();
    String getProveedor();
    BigDecimal getPesoGuia();
    BigDecimal getPesoReal();
    BigDecimal getDiferenciaPeso();
    String getTipoTejido();
    String getColor();
    Integer getNumRollos();
    String getEstadoCalidad();
    Boolean getRequiereReposo();
    String getObservaciones();
    LocalDateTime getFechaIngreso();
    String getCodigoOt();
    String getClienteOt(); // <-- NUEVO (Para la vista)
    String getNombreRegistrador();
    String getNombreCatalogoTela();
    String getComposicionCatalogo(); // <-- NUEVO (Para la vista)
    Integer getIdCatalogoTela();
    Integer getTiempoReposo();
}
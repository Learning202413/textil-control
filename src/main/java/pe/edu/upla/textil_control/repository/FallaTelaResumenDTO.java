package pe.edu.upla.textil_control.repository;

/**
 * DTO de proyección para fallas_tela con datos de JOIN.
 * Preserva EXACTAMENTE los campos del ResultSet en FallaTelaDAO.mapear()
 */
public interface FallaTelaResumenDTO {
    Integer getIdFalla();
    Integer getIdTela();
    Integer getIdTizador();
    String  getTipoFalla();
    Integer getPosicionRollo();
    java.math.BigDecimal getPosicionMetro();
    java.math.BigDecimal getAnchoCm();
    java.math.BigDecimal getLargoCm();
    String  getDescripcion();
    Number  getEsAreaNoApta();
    String  getFechaRegistroFormateada();
    // Campos de JOIN
    String  getCodigoTela();
    String  getTipoTejido();
    String  getCodigoOt();
    String  getNombreTizador();
}

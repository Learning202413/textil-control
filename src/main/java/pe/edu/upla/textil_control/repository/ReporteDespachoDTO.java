package pe.edu.upla.textil_control.repository;

/**
 * DTO proyección: Desviaciones de despacho (planeado vs final).
 * Migrado de ReporteDAO.obtenerDespacho()
 */
public interface ReporteDespachoDTO {
    String getCodigoOt();
    Integer getEstimada();
    Integer getFinal();
    Integer getDiferencia();
}

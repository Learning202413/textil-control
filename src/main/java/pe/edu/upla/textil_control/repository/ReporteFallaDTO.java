package pe.edu.upla.textil_control.repository;

/**
 * DTO proyección: Fallas agrupadas por tipo de tela.
 * Migrado de ReporteDAO.obtenerFallasPorTela()
 */
public interface ReporteFallaDTO {
    String getCodigoTela();
    Integer getFallasTotales();
}

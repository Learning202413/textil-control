package pe.edu.upla.textil_control.repository;

/**
 * DTO proyección: Calidad vs Productividad por maquinista.
 * Migrado de ReporteDAO.obtenerCalidadVsProductividad()
 */
public interface ReporteCalidadProductividadDTO {
    String getMaquinista();
    Integer getMinutos();
    Integer getDefectos();
}

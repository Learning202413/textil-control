package pe.edu.upla.textil_control.repository;

/**
 * DTO proyección: Eficiencia global - estados de OTs.
 * Migrado de ReporteDAO.obtenerEficienciaGlobal()
 */
public interface ReporteEficienciaDTO {
    String getEstado();
    Integer getTotal();
}

package pe.edu.upla.textil_control.repository;

/**
 * DTO proyección: Rendimiento personal de un maquinista.
 * Migrado de ReporteDAO.obtenerRendimientoMaquinista()
 */
public interface ReporteRendimientoDTO {
    String getCodigoOt();
    Integer getCantidadEst();
    String getFechaAsignada();
    Integer getMinutosTrabajados();
    Integer getDiasRetraso();
    Integer getDefectosPropios();
}

package pe.edu.upla.textil_control.repository;

/**
 * DTO proyección: Tiempos de maquinistas por OT.
 * Migrado de ReporteDAO.obtenerTiemposMaquinistasPorOT()
 */
public interface ReporteTiempoMaquinistaDTO {
    String getCodigoOt();
    String getMaquinista();
    String getInicioReal();
    String getFinReal();
    Integer getMinutosAbsolutos();
    Integer getMinutosTrabajados();
}

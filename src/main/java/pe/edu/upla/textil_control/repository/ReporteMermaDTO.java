package pe.edu.upla.textil_control.repository;

import java.math.BigDecimal;

/**
 * DTO proyección: Merma agrupada por Orden de Trabajo.
 * Migrado de ReporteDAO.obtenerMermaPorOT()
 */
public interface ReporteMermaDTO {
    String getCodigoOt();
    BigDecimal getPesoUtilizado();
    BigDecimal getPesoMerma();
}

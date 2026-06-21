package pe.edu.upla.textil_control.repository;

import java.math.BigDecimal;

/**
 * DTO proyección: OTs problemáticas (top 15 por defectos y merma).
 * Migrado de ReporteDAO.obtenerOTsProblematicas()
 */
public interface ReporteOTProblematicaDTO {
    String getCodigoOt();
    Integer getCantidadEst();
    BigDecimal getMerma();
    Integer getDefectos();
}

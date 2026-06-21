package pe.edu.upla.textil_control.repository;

import java.math.BigDecimal;

/**
 * DTO proyección: Inventario de telas agrupado por estado de calidad.
 * Migrado de ReporteDAO.obtenerInventarioTelas()
 */
public interface ReporteInventarioDTO {
    String getEstado();
    Integer getCantidad();
    BigDecimal getPesoTotal();
    BigDecimal getPesoPromedio();
}

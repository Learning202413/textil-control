package pe.edu.upla.textil_control.repository;

import java.math.BigDecimal;

/**
 * DTO de proyección para telas disponibles para registrar merma.
 * SQL preservado del MermaDAO.listarTelasParaMerma()
 */
public interface TelaMermaDTO {
    Integer getIdTela();
    String  getCodigoTela();
    String  getTipoTejido();
    String  getColor();
    BigDecimal getPesoReal();
    Integer getNumRollos();
    Integer getIdOt();
    String  getCodigoOt();
    String  getCliente();
}

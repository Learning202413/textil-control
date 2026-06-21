package pe.edu.upla.textil_control.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de proyección para mermas con datos de JOIN.
 * Preserva EXACTAMENTE los campos del ResultSet en MermaDAO.mapear()
 */
public interface MermaResumenDTO {
    Integer getIdMerma();
    Integer getIdTela();
    Integer getIdOt();
    Integer getIdTizador();
    String  getFase();
    BigDecimal getPesoUtilizadoKg();
    BigDecimal getPesomermaKg();
    BigDecimal getPorcentajeMerma();
    String  getObservaciones();
    LocalDateTime getFechaRegistro();
    // Campos de JOIN
    String  getCodigoTela();
    String  getTipoTejido();
    String  getCodigoOt();
    String  getCliente();
    String  getNombreTizador();
}

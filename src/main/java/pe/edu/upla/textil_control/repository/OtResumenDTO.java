package pe.edu.upla.textil_control.repository;

/**
 * DTO de proyección para OTs que tienen mermas registradas.
 * SQL preservado del MermaDAO.listarOtsConMermas()
 */
public interface OtResumenDTO {
    Integer getIdOt();
    String  getCodigoOt();
    String  getCliente();
}

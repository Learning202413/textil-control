package pe.edu.upla.textil_control.repository;

/**
 * DTO de proyección para telas disponibles para mapeo y telas con fallas.
 * Usado por FallaTelaRepository.listarTelasParaMapeo() y listarTelasConFallas()
 */
public interface TelaMapeoDTO {
    Integer getIdTela();
    String  getCodigoTela();
    String  getTipoTejido();
    String  getColor();
    Integer getNumRollos();
    String  getCodigoOt();
}

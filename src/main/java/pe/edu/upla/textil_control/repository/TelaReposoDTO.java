package pe.edu.upla.textil_control.repository;

/**
 * DTO de proyección para telas disponibles para reposo.
 * Usado por TiempoReposoRepository.listarTelasDisponiblesParaReposo()
 * SQL preservado del TiempoReposoDAO original.
 */
public interface TelaReposoDTO {
    Integer getIdTela();
    String  getCodigoTela();
    String  getTipoTejido();
    String  getColor();
    Integer getNumRollos();
    String  getCodigoOt();
    /** Tiempo de reposo del catálogo en minutos */
    Integer getTiempoReposo();
}

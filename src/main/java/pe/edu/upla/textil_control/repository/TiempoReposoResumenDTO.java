package pe.edu.upla.textil_control.repository;

/**
 * DTO de proyección para tiempos_reposo con datos de JOIN.
 * Preserva EXACTAMENTE los campos del ResultSet en TiempoReposoDAO.mapearResultados()
 */
public interface TiempoReposoResumenDTO {
    Integer getIdReposo();
    Integer getIdTela();
    Integer getIdUsuarioInicio();
    java.util.Date getFechaInicio();
    String getFechaInicioStr();
    Integer getDuracionMinutos();
    String getFechaFinEstimadaStr();
    String getFechaFinRealStr();
    String  getEstado();
    Number getNotificacionEnviada();
    String  getObservaciones();
    java.util.Date getFechaCrea();
    // Campos de JOIN
    String  getCodigoTela();
    String  getTipoTejido();
    String  getCodigoOt();
    String  getNombreRegistrador();
    // Campos calculados por SQL — equivale a TiempoReposo.getPorcentajeCompletado() y getMinutosRestantes()
    Integer getPorcentajeCompletado();
    Integer getMinutosRestantes();
}

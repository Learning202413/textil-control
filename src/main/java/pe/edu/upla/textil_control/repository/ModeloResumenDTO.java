package pe.edu.upla.textil_control.repository;

public interface ModeloResumenDTO {
    Integer getIdModelo();
    String getNombre();
    String getTemporada();
    Integer getTotalPiezas();

    // Cambiamos Boolean por Long para que coincida con lo que devuelve TiDB (0 o 1)
    Long getEnUso();
}
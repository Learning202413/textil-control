package pe.edu.upla.textil_control.model;

import lombok.Data;
import java.util.List;

@Data
public class MaquinistaDTO {
    private Usuario usuario;
    private List<Especialidad> especialidades;

    public MaquinistaDTO(Usuario usuario, List<Especialidad> especialidades) {
        this.usuario = usuario;
        this.especialidades = especialidades;
    }
}
package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "permisos")
@Data
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Integer idPermiso;

    private String codigo;
    private String nombre;
    private String modulo;
    private String descripcion;

    // @Transient le dice a Spring: "Este atributo existe en Java,
    // pero NO lo busques en la tabla de la base de datos"
    @Transient
    private boolean asignado;
}
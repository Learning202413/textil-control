package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "fases_produccion")
@Data
public class FaseProduccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fase")
    private Integer idFase;
    private String nombre;
    private Integer orden;
    private String descripcion;
}
package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modelos_prenda")
@Data
public class ModeloPrenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modelo")
    private Integer idModelo;
    private String nombre;
    private String temporada;

    @Transient
    private List<PiezaModelo> piezas = new ArrayList<>();
}
package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "piezas_modelo")
@Data
public class PiezaModelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pieza")
    private Integer idPieza;

    @Column(name = "id_modelo")
    private Integer idModelo;

    @Column(name = "nombre_pieza")
    private String nombrePieza;

    private Integer cantidad;

    // No está en la BD directamente en esta tabla, lo usamos para la UI y la lógica
    @Transient
    private List<Integer> idFasesAsignadas;
}
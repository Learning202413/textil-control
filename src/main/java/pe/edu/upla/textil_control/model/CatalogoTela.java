package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "catalogo_telas")
@Data
public class CatalogoTela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_catalogo")
    private Integer idCatalogo;

    private String nombre;
    private String composicion;

    @Column(name = "proveedor_base")
    private String proveedorBase;

    @Column(name = "requiere_reposo")
    private boolean requiereReposo;

    @Column(name = "tiempo_reposo")
    private Integer tiempoReposo; // en minutos
}
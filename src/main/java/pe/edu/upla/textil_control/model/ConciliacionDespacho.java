package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Table(name = "conciliacion_despacho")
@Data
public class ConciliacionDespacho {

    public enum EstadoConciliacion { PENDIENTE, CONCILIADO_OK, MERMA_DETECTADA, DESPACHADO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conciliacion")
    private Integer idConciliacion;
    @Column(name = "id_ot")
    private Integer idOt;
    @Column(name = "cantidad_final")
    private Integer cantidadFinal;
    private Integer diferencia;

    @Enumerated(EnumType.STRING)
    private EstadoConciliacion estado;

    @Column(name = "id_responsable")
    private Integer idResponsable;
    @Column(name = "fecha_conciliacion")
    private Timestamp fechaConciliacion;
    @Column(name = "fecha_despacho")
    private Timestamp fechaDespacho;
    private String observaciones;
    @Column(name = "cantidad_ensamblaje")
    private Integer cantidadEnsamblaje;
    @Column(name = "id_asignacion_ensamblaje")
    private Integer idAsignacionEnsamblaje;

    @Transient private String codigoOt;
    @Transient private String cliente;
    @Transient private String nombreModelo;
    @Transient private String nombreResponsable;
    @Transient private Integer cantidadEstimada; // Mapeado del JOIN con OT
}
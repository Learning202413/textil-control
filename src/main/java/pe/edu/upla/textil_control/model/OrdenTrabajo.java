package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "orden_trabajo")
@Data
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ot")
    private Integer idOt;

    @Column(name = "codigo_ot", unique = true)
    private String codigoOt;

    private String cliente;

    @Column(name = "cantidad_est")
    private Integer cantidadEst;

    private String estado; // CREADA, EN_PROCESO, FINALIZADA, ANULADA

    @Column(name = "id_responsable")
    private Integer idResponsable;

    @Column(name = "id_modelo")
    private Integer idModelo;

    @CreationTimestamp
    @Column(name = "fecha_crea", updatable = false)
    private LocalDateTime fechaCrea;

    @Transient private String nombreResponsable;
    @Transient private String nombreModelo;
}
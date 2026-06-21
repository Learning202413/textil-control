package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer idNotificacion;

    private String titulo;
    private String mensaje;
    private String tipo;

    @Column(name = "id_referencia")
    private Integer idReferencia;

    @Column(name = "para_rol")
    private String paraRol;

    private Boolean leida = false;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}

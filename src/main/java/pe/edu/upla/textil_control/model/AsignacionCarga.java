package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Table(name = "asignaciones_carga")
@Data
public class AsignacionCarga {

    public enum EstadoFase { PENDIENTE, EN_PROCESO, COMPLETADA, BLOQUEADA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Integer idAsignacion;
    @Column(name = "id_ot")
    private Integer idOt;
    @Column(name = "id_pieza")
    private Integer idPieza;
    @Column(name = "id_fase")
    private Integer idFase;
    @Column(name = "id_maquinista")
    private Integer idMaquinista;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_fase")
    private EstadoFase estadoFase;

    @Column(name = "fecha_asignacion")
    private Timestamp fechaAsignacion;
    @Column(name = "fecha_completado")
    private Timestamp fechaCompletado;
    @Column(name = "cantidad_piezas")
    private Integer cantidadPiezas;
    @Column(name = "piezas_completadas")
    private Integer piezasCompletadas;
    @Column(name = "tipo_tarea")
    private String tipoTarea = "NORMAL";

    @Transient private String codigoOt;
    @Transient private String nombreModelo;
    @Transient private String nombrePieza;
    @Transient private String nombreFase;
    @Transient private String fasePreviaEstado;
    @Transient private String nombreMaquinista;
    @Transient private String especialidadMaquinista;
}
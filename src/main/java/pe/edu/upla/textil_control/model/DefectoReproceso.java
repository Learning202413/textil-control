package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Entity
@Table(name = "defectos_reproceso")
@Data
public class DefectoReproceso {

    public enum TipoFalla { ERROR_COSTURA, SALTO_PUNTADA, MANCHA_SUCIEDAD, TENSION_INCORRECTA, CORTE_IRREGULAR, ENSAMBLAJE, OTRO }
    public enum Estado { PENDIENTE, REGISTRADO, CORREGIDO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_defecto")
    private Integer idDefecto;
    @Column(name = "id_ot")
    private Integer idOt;
    @Column(name = "id_pieza")
    private Integer idPieza;
    @Column(name = "id_maquinista")
    private Integer idMaquinista;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_falla")
    private TipoFalla tipoFalla;

    private String observaciones;

    @Column(name = "cantidad_faltante")
    private Integer cantidadFaltante;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Column(name = "id_asignacion")
    private Integer idAsignacion;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private Timestamp fechaRegistro;

    @Column(name = "genera_reposicion")
    private Boolean generaReposicion;   // true = 1 (reposición), false = 0 (revertido)

    @Transient private String codigoOt;
    @Transient private String nombreModelo;
    @Transient private String nombrePieza;
    @Transient private String nombreMaquinista;
}
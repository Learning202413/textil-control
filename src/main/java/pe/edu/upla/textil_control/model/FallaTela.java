package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad: fallas_tela
 * HU02: Mapeo Digital de Imperfecciones y Fallas en la Tela
 *
 * REGLAS DE NEGOCIO PRESERVADAS:
 *  - esAreaNoApta = true por defecto (CUS 2.3)
 *  - TipoFalla: MANCHA, HUECO, DEFECTO_TEJIDO (CUS 2.2)
 */
@Entity
@Table(name = "fallas_tela")
@Data
public class FallaTela {

    public enum TipoFalla {
        MANCHA, HUECO, DEFECTO_TEJIDO;

        /** Etiqueta legible para la UI — PRESERVADA del original */
        public String etiqueta() {
            switch (this) {
                case MANCHA:         return "Mancha";
                case HUECO:          return "Hueco";
                case DEFECTO_TEJIDO: return "Defecto de Tejido";
                default:             return name();
            }
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_falla")
    private Integer idFalla;

    @Column(name = "id_tela")
    private Integer idTela;

    @Column(name = "id_tizador")
    private Integer idTizador;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_falla")
    private TipoFalla tipoFalla;

    @Column(name = "posicion_rollo")
    private Integer posicionRollo;

    @Column(name = "posicion_metro")
    private BigDecimal posicionMetro;

    @Column(name = "ancho_cm")
    private BigDecimal anchoCm;

    @Column(name = "largo_cm")
    private BigDecimal largoCm;

    private String descripcion;

    // REGLA PRESERVADA: esAreaNoApta = true por defecto (CUS 2.3)
    @Column(name = "es_area_no_apta")
    private Boolean esAreaNoApta = true;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    // ── Campos @Transient para vistas (JOIN) ──────────────────
    @Transient
    private String codigoTela;
    @Transient
    private String codigoOt;
    @Transient
    private String tipoTejido;
    @Transient
    private String nombreTizador;
}

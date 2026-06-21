package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad: mermas
 * HU04: Registro de Merma por Tipo de Tejido
 *
 * REGLAS DE NEGOCIO PRESERVADAS:
 *  - Fase: TIZADO, CORTE
 *  - porcentajeMerma: columna STORED en BD (insertable=false, updatable=false)
 *  - getNivelMerma(): ≤5% BAJA, ≤10% MEDIA, >10% ALTA
 */
@Entity
@Table(name = "mermas")
@Data
public class Merma {

    public enum Fase { TIZADO, CORTE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_merma")
    private Integer idMerma;

    @Column(name = "id_tela")
    private Integer idTela;

    @Column(name = "id_ot")
    private Integer idOt;

    @Column(name = "id_tizador")
    private Integer idTizador;

    @Enumerated(EnumType.STRING)
    private Fase fase;

    @Column(name = "peso_utilizado_kg")
    private BigDecimal pesoUtilizadoKg;

    @Column(name = "peso_merma_kg")
    private BigDecimal pesomermaKg;

    // REGLA PRESERVADA: columna STORED en BD — no se inserta ni actualiza
    @Column(name = "porcentaje_merma", insertable = false, updatable = false)
    private BigDecimal porcentajeMerma;

    private String observaciones;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    // ── Campos @Transient para vistas (JOIN) ──────────────────
    @Transient
    private String codigoTela;
    @Transient
    private String tipoTejido;
    @Transient
    private String codigoOt;
    @Transient
    private String cliente;
    @Transient
    private String nombreTizador;

    // ── Método de utilidad — PRESERVADO ÍNTEGRAMENTE del original ──

    /**
     * Clasificación visual del porcentaje de merma:
     *  ≤ 5%   → BAJA  (verde)
     *  ≤ 10%  → MEDIA (amarillo)
     *  > 10%  → ALTA  (rojo)
     */
    public String getNivelMerma() {
        if (porcentajeMerma == null) return "BAJA";
        double pct = porcentajeMerma.doubleValue();
        if (pct <= 5.0)  return "BAJA";
        if (pct <= 10.0) return "MEDIA";
        return "ALTA";
    }
}

package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidad: tiempos_reposo
 * HU03: Gestión de Tiempos de Reposo y Corte
 *
 * REGLAS DE NEGOCIO PRESERVADAS:
 *  - duracionMinutos = 60 por defecto
 *  - estado = EN_REPOSO por defecto
 *  - notificacionEnviada = false por defecto
 *  - fechaFinEstimada: columna GENERATED en BD (insertable=false, updatable=false)
 *  - Métodos de cálculo: getMinutosTranscurridos(), getMinutosRestantes(),
 *    getPorcentajeCompletado(), estaListo() — PRESERVADOS ÍNTEGRAMENTE
 */
@Entity
@Table(name = "tiempos_reposo")
@Data
public class TiempoReposo {

    public enum Estado { EN_REPOSO, APTO_CORTE, CANCELADO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reposo")
    private Integer idReposo;

    @Column(name = "id_tela")
    private Integer idTela;

    @Column(name = "id_usuario_inicio")
    private Integer idUsuarioInicio;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    // REGLA PRESERVADA: duración por defecto 60 minutos
    @Column(name = "duracion_minutos")
    private Integer duracionMinutos = 60;

    // REGLA PRESERVADA: columna GENERATED en BD — no se inserta ni actualiza
    @Column(name = "fecha_fin_estimada", insertable = false, updatable = false)
    private LocalDateTime fechaFinEstimada;

    @Column(name = "fecha_fin_real")
    private LocalDateTime fechaFinReal;

    // REGLA PRESERVADA: estado por defecto EN_REPOSO
    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.EN_REPOSO;

    // REGLA PRESERVADA: notificación no enviada por defecto
    @Column(name = "notificacion_enviada")
    private Boolean notificacionEnviada = false;

    private String observaciones;

    @Column(name = "fecha_crea", insertable = false, updatable = false)
    private LocalDateTime fechaCrea;

    // ── Campos @Transient para vistas (JOIN) ──────────────────
    @Transient
    private String codigoTela;
    @Transient
    private String codigoOt;
    @Transient
    private String tipoTejido;
    @Transient
    private String nombreRegistrador;

    // ── Métodos de utilidad — PRESERVADOS ÍNTEGRAMENTE del original ──

    /**
     * Minutos transcurridos desde el inicio del reposo.
     * Retorna 0 si no hay fecha de inicio.
     */
    public long getMinutosTranscurridos() {
        if (fechaInicio == null) return 0;
        long ahora  = System.currentTimeMillis();
        long inicio = java.sql.Timestamp.valueOf(fechaInicio).getTime();
        return Math.max(0, (ahora - inicio) / 60_000);
    }

    /**
     * Minutos restantes para que termine el reposo.
     * Retorna 0 si ya venció o si el estado no es EN_REPOSO.
     */
    public long getMinutosRestantes() {
        if (estado != Estado.EN_REPOSO || fechaInicio == null) return 0;
        long transcurridos = getMinutosTranscurridos();
        return Math.max(0, duracionMinutos - transcurridos);
    }

    /**
     * Porcentaje completado del reposo (0-100).
     */
    public int getPorcentajeCompletado() {
        if (duracionMinutos == null || duracionMinutos <= 0) return 100;
        long transcurridos = getMinutosTranscurridos();
        int pct = (int) ((transcurridos * 100) / duracionMinutos);
        return Math.min(100, pct);
    }

    /**
     * Indica si el tiempo de reposo ya venció y la tela está lista para corte.
     */
    public boolean estaListo() {
        return getMinutosRestantes() == 0 && estado == Estado.EN_REPOSO;
    }
}

package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entidad: usuarios
 * CORRECCIONES aplicadas:
 *  1. Se agregaron fecha_crea, fecha_mod y reprocesos_acum que existen en TiDB
 *     pero faltaban en la entidad. Sin ellos, Hibernate genera un UPDATE que
 *     omite esas columnas NOT NULL y TiDB lanza error 500.
 *  2. horario_inicio y horario_fin se declaran como String (VARCHAR-compatible)
 *     y se marca columnDefinition="TIME" para que Hibernate sepa el tipo real.
 *  3. fecha_crea y fecha_mod son insertOnly/updatable=false para que Hibernate
 *     nunca las pise en un UPDATE (las gestiona la BD con DEFAULT/ON UPDATE).
 */
@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    private String username;
    private String password; // Hash BCrypt

    private String nombre;
    private String apellido;
    private String email;

    @Column(name = "id_rol")
    private Integer idRol;

    private Boolean activo;

    // ── Campos de horario ──────────────────────────────────────
    @Column(name = "horario_restringido")
    private Boolean horarioRestringido = true;

    @Column(name = "horario_dias")
    private String horarioDias;

    /**
     * Tipo TIME en TiDB. Se mapea como String para compatibilidad con
     * el proyecto original que lo usa como "HH:mm:ss".
     * columnDefinition le dice a Hibernate el tipo real en BD.
     */
    @Column(name = "horario_inicio", columnDefinition = "TIME")
    private String horarioInicio;

    @Column(name = "horario_fin", columnDefinition = "TIME")
    private String horarioFin;

    // ── Campos que existen en TiDB y DEBEN estar aquí ─────────
    /**
     * HU06: contador acumulado de reprocesos del maquinista.
     * La columna en TiDB es NOT NULL DEFAULT 0.
     * Si no está en la entidad, Hibernate omite la columna en INSERT/UPDATE
     * y TiDB lanza error de columna NOT NULL sin valor.
     */
    @Column(name = "reprocesos_acum")
    private Integer reprocesosAcum = 0;

    /**
     * Timestamp de creación — gestionado por la BD (DEFAULT CURRENT_TIMESTAMP).
     * insertable=false, updatable=false: Hibernate NUNCA lo escribe.
     */
    @Column(name = "fecha_crea", insertable = false, updatable = false)
    private LocalDateTime fechaCrea;

    /**
     * Timestamp de última modificación — gestionado por la BD (ON UPDATE).
     * insertable=false, updatable=false: Hibernate NUNCA lo escribe.
     */
    @Column(name = "fecha_mod", insertable = false, updatable = false)
    private LocalDateTime fechaMod;

    // Dentro de Usuario.java
    @Column(name = "intentos_fallidos")
    private Integer intentosFallidos = 0;

    @Column(name = "bloqueado_hasta")
    private java.time.LocalDateTime bloqueadoHasta;
    // ── Campo transitorio (no está en BD) ─────────────────────
    /**
     * Se llena manualmente desde la tabla roles mediante JOIN en el Service.
     * No es columna de la tabla usuarios — @Transient lo excluye de JPA.
     */
    @Transient
    private String nombreRol;

    // ── Método de utilidad ─────────────────────────────────────
    public String getNombreCompleto() {
        String n = (this.nombre != null) ? this.nombre : "";
        String a = (this.apellido != null) ? this.apellido : "";
        return (n + " " + a).trim();
    }
}

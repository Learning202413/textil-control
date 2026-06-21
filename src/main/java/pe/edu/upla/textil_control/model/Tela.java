package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "telas")
@Data
public class Tela {
    public enum Origen { CLIENTE, TALLER }
    public enum EstadoCalidad { ACEPTADO, OBSERVADO, RECHAZADO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tela")
    private Integer idTela;

    @Column(name = "id_ot")
    private Integer idOt;

    @Column(name = "id_registrador")
    private Integer idRegistrador;

    @Column(name = "codigo_tela")
    private String codigoTela;

    @Enumerated(EnumType.STRING)
    private Origen origen;

    private String proveedor;

    @Column(name = "peso_guia")
    private BigDecimal pesoGuia;

    @Column(name = "peso_real")
    private BigDecimal pesoReal;

    // REGLA PRESERVADA: La base de datos calcula este valor (STORED), Java no lo inserta.
    @Column(name = "diferencia_peso", insertable = false, updatable = false)
    private BigDecimal diferenciaPeso;

    @Column(name = "tipo_tejido")
    private String tipoTejido;

    private String color;

    @Column(name = "num_rollos")
    private Integer numRollos = 1;

    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_calidad")
    private EstadoCalidad estadoCalidad = EstadoCalidad.OBSERVADO;

    @Column(name = "requiere_reposo")
    private Boolean requiereReposo;

    @Column(name = "id_catalogo_tela")
    private Integer idCatalogoTela;

    @Transient // No se guarda en la BD de telas, se usa para vistas
    private Integer tiempoReposoCatalogo;

    @CreationTimestamp
    @Column(name = "fecha_ingreso", updatable = false)
    private LocalDateTime fechaIngreso;

    // REGLA DE NEGOCIO PRESERVADA (HU01 - CA1)
    public boolean hayDiscrepanciaPeso() {
        if (pesoGuia == null || pesoReal == null) return false;
        BigDecimal umbral = pesoGuia.multiply(new BigDecimal("0.01"));
        if (diferenciaPeso == null) {
            diferenciaPeso = pesoReal.subtract(pesoGuia);
        }
        return diferenciaPeso.abs().compareTo(umbral) > 0;
    }
}
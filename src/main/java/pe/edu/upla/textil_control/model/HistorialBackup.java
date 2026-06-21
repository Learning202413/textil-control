package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime; // <-- NUEVO IMPORT

@Entity
@Table(name = "historial_backups")
@Data
public class HistorialBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_backup")
    private Integer idBackup;

    @CreationTimestamp
    @Column(name = "fecha_solicitud", updatable = false)
    private LocalDateTime fechaSolicitud; // <-- CAMBIADO

    @Column(name = "usuario_solicitante")
    private Integer usuarioSolicitante;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    @Column(name = "tamanio_bytes")
    private Long tamanioBytes;

    private String estado;

    private String observaciones;
}
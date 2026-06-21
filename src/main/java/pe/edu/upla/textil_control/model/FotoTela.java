package pe.edu.upla.textil_control.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore; // <-- NUEVO IMPORT
import java.time.LocalDateTime;

@Entity
@Table(name = "fotos_tela")
@Data
public class FotoTela {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto")
    private Integer idFoto;

    @Column(name = "id_tela")
    private Integer idTela;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    @Column(name = "ruta_relativa")
    private String rutaRelativa;

    @JsonIgnore // <-- SOLUCIÓN: Jackson ignorará este campo al armar el JSON para JavaScript
    @CreationTimestamp
    @Column(name = "fecha_subida", updatable = false)
    private LocalDateTime fechaSubida;
}
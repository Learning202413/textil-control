package pe.edu.upla.textil_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upla.textil_control.model.Tela;
import java.util.List;

@Repository
public interface TelaRepository extends JpaRepository<Tela, Integer> {

    @Query(value = "SELECT codigo_tela FROM telas WHERE codigo_tela LIKE :prefijo ORDER BY id_tela DESC LIMIT 1", nativeQuery = true)
    String findMaxCodigoByPrefijo(@Param("prefijo") String prefijo);

    @Query(value = """
        SELECT t.id_tela AS idTela, t.id_ot AS idOt, t.codigo_tela AS codigoTela, t.origen, t.proveedor, 
               t.peso_guia AS pesoGuia, t.peso_real AS pesoReal, t.diferencia_peso AS diferenciaPeso, 
               t.tipo_tejido AS tipoTejido, t.color, t.num_rollos AS numRollos, t.estado_calidad AS estadoCalidad, 
               t.requiere_reposo AS requiereReposo, t.observaciones, t.fecha_ingreso AS fechaIngreso, 
               ot.codigo_ot AS codigoOt, ot.cliente AS clienteOt, CONCAT(u.nombre,' ',u.apellido) AS nombreRegistrador, 
               ct.nombre AS nombreCatalogoTela, ct.composicion AS composicionCatalogo, t.id_catalogo_tela AS idCatalogoTela, ct.tiempo_reposo AS tiempoReposo
        FROM telas t
        LEFT JOIN orden_trabajo ot ON t.id_ot = ot.id_ot
        LEFT JOIN usuarios u ON t.id_registrador = u.id_usuario
        LEFT JOIN catalogo_telas ct ON t.id_catalogo_tela = ct.id_catalogo
        WHERE (:codigo IS NULL OR t.codigo_tela LIKE CONCAT('%', :codigo, '%'))
          AND (:proveedor IS NULL OR t.proveedor LIKE CONCAT('%', :proveedor, '%'))
          AND (:fechaIni IS NULL OR DATE(t.fecha_ingreso) >= :fechaIni)
          AND (:fechaFin IS NULL OR DATE(t.fecha_ingreso) <= :fechaFin)
        ORDER BY t.fecha_ingreso DESC
    """, nativeQuery = true)
    List<TelaResumenDTO> listarConFiltros(@Param("codigo") String codigo, @Param("proveedor") String proveedor,
                                          @Param("fechaIni") String fechaIni, @Param("fechaFin") String fechaFin);
}
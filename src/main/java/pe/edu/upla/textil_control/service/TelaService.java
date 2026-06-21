package pe.edu.upla.textil_control.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upla.textil_control.model.FotoTela;
import pe.edu.upla.textil_control.model.Tela;
import pe.edu.upla.textil_control.repository.FotoTelaRepository;
import pe.edu.upla.textil_control.repository.TelaRepository;
import pe.edu.upla.textil_control.repository.TelaResumenDTO;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
public class TelaService {

    @Autowired private TelaRepository telaRepo;
    @Autowired private FotoTelaRepository fotoRepo;
    private final RestTemplate restTemplate = new RestTemplate();

    public List<TelaResumenDTO> listarConFiltros(String codigo, String proveedor, String fIni, String fFin) {
        // Conversión de vacíos a null para respetar la lógica SQL original
        return telaRepo.listarConFiltros(
                (codigo != null && !codigo.isBlank()) ? codigo : null,
                (proveedor != null && !proveedor.isBlank()) ? proveedor : null,
                (fIni != null && !fIni.isBlank()) ? fIni : null,
                (fFin != null && !fFin.isBlank()) ? fFin : null
        );
    }

    public List<FotoTela> obtenerFotosPorTela(Integer idTela) {
        return fotoRepo.findByIdTelaOrderByFechaSubidaAsc(idTela);
    }

    // Regla de Negocio: Generar TELA-YYYY-NNNN preservada de TelaDAO
    public String generarCodigoTela() {
        int anio = Year.now().getValue();
        String prefijo = "TELA-" + anio + "-";
        String maxCodigo = telaRepo.findMaxCodigoByPrefijo(prefijo + "%");
        if (maxCodigo != null && maxCodigo.length() >= 14) {
            try {
                int siguiente = Integer.parseInt(maxCodigo.substring(10)) + 1;
                return prefijo + String.format("%04d", siguiente);
            } catch (Exception e) { return prefijo + "0001"; }
        }
        return prefijo + "0001";
    }

    @Transactional
    public Tela guardarTela(Tela tela, MultipartFile[] fotos) throws Exception {
        Tela entidadGuardar;

        // REGLA: Si el checkbox de HTML llega vacío (null), forzamos a false para evitar error en MySQL
        boolean reposo = (tela.getRequiereReposo() != null) ? tela.getRequiereReposo() : false;

        if (tela.getIdTela() == null) {
            // NUEVO INGRESO
            tela.setCodigoTela(generarCodigoTela());
            tela.setRequiereReposo(reposo);
            entidadGuardar = tela;
        } else {
            // EDICIÓN: Cargamos la original para proteger campos fijos (registrador, fecha, código)
            entidadGuardar = telaRepo.findById(tela.getIdTela()).orElseThrow();
            entidadGuardar.setIdOt(tela.getIdOt());
            entidadGuardar.setOrigen(tela.getOrigen());
            entidadGuardar.setProveedor(tela.getProveedor());
            entidadGuardar.setIdCatalogoTela(tela.getIdCatalogoTela());
            entidadGuardar.setTipoTejido(tela.getTipoTejido());
            entidadGuardar.setColor(tela.getColor());
            entidadGuardar.setNumRollos(tela.getNumRollos());
            entidadGuardar.setPesoGuia(tela.getPesoGuia());
            entidadGuardar.setPesoReal(tela.getPesoReal());
            entidadGuardar.setEstadoCalidad(tela.getEstadoCalidad());
            entidadGuardar.setObservaciones(tela.getObservaciones());
            entidadGuardar.setRequiereReposo(reposo); // <-- Aquí prevenimos el error
        }

        Tela telaGuardada = telaRepo.save(entidadGuardar);
        procesarFotos(telaGuardada.getIdTela(), fotos);
        return telaGuardada;
    }

    // Lógica original preservada del InventarioServlet.java (líneas 110-130)
    private void procesarFotos(Integer idTela, MultipartFile[] fotos) throws Exception {
        if (fotos == null || fotos.length == 0) return;
        Path carpeta = Paths.get("uploads", "telas", String.valueOf(idTela));
        Files.createDirectories(carpeta);

        for (MultipartFile file : fotos) {
            if (file.isEmpty()) continue;
            String originalName = file.getOriginalFilename();
            String extension = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
            String nombreUnico = UUID.randomUUID().toString() + extension;
            Path destino = carpeta.resolve(nombreUnico);

            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            FotoTela foto = new FotoTela();
            foto.setIdTela(idTela);
            foto.setNombreArchivo(nombreUnico);
            foto.setRutaRelativa("uploads/telas/" + idTela + "/" + nombreUnico);
            fotoRepo.save(foto);
        }
    }

    public String consultarProveedorApi(String doc) {
        try {
            String url = (doc.length() == 8)
                    ? "https://api.apis.net.pe/v1/dni?numero=" + doc
                    : "https://api.apis.net.pe/v1/ruc?numero=" + doc;
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) { return "{}"; }
    }
}
package pe.edu.upla.textil_control.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.upla.textil_control.model.Tela;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.repository.TelaResumenDTO;
import pe.edu.upla.textil_control.service.CatalogoTelaService;
import pe.edu.upla.textil_control.service.OrdenTrabajoService;
import pe.edu.upla.textil_control.service.TelaService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    @Autowired private TelaService telaService;
    @Autowired private OrdenTrabajoService otService;
    @Autowired private CatalogoTelaService catalogoService;

    // Validador de permisos original
    private boolean tienePermiso(HttpSession session, String permiso) {
        Set<String> permisos = (Set<String>) session.getAttribute("permisosUsuario");
        return permisos != null && permisos.contains(permiso);
    }

    @GetMapping
    public String listar(Model model, HttpSession session,
                         @RequestParam(required = false) String fCodigo,
                         @RequestParam(required = false) String fProveedor,
                         @RequestParam(required = false) String fFechaIni,
                         @RequestParam(required = false) String fFechaFin) {

        // Regla: Solo si tiene permiso ALM_TELA_VER
        if (!tienePermiso(session, "ALM_TELA_VER")) return "redirect:/dashboard?error=sinPermiso";

        List<TelaResumenDTO> telas = telaService.listarConFiltros(fCodigo, fProveedor, fFechaIni, fFechaFin);

        // Mapeo original de fotos por tela para mandarlo a la vista
        Map<Integer, Object> fotosMap = new HashMap<>();
        for (TelaResumenDTO t : telas) {
            fotosMap.put(t.getIdTela(), telaService.obtenerFotosPorTela(t.getIdTela()));
        }

        model.addAttribute("listaTelas", telas);
        model.addAttribute("fotosMap", fotosMap);
        model.addAttribute("otsActivas", otService.listarTodas());
        model.addAttribute("catalogoTelas", catalogoService.listarTodos());

        // Mantener valores de los filtros
        model.addAttribute("fCodigo", fCodigo);
        model.addAttribute("fProveedor", fProveedor);
        model.addAttribute("fFechaIni", fFechaIni);
        model.addAttribute("fFechaFin", fFechaFin);

        return "inventario";
    }

    @GetMapping(value = "/buscarProveedor", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> buscarProveedor(@RequestParam String doc) {
        return ResponseEntity.ok(telaService.consultarProveedorApi(doc));
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Tela tela,
                          @RequestParam(value = "fotos", required = false) MultipartFile[] fotos,
                          HttpSession session, RedirectAttributes redirectAttrs) {

        if (!tienePermiso(session, "ALM_TELA_REGISTRAR")) return "redirect:/dashboard?error=sinPermiso";

        try {
            Usuario usr = (Usuario) session.getAttribute("usuarioSesion");
            if (usr != null && tela.getIdTela() == null) {
                tela.setIdRegistrador(usr.getIdUsuario());
            }

            Tela guardada = telaService.guardarTela(tela, fotos);

            // Regla de Negocio Preservada: Generación del mensaje de Alerta Dinámico
            String msj = "✅ Tela " + guardada.getCodigoTela() + " registrada correctamente.";
            if (guardada.hayDiscrepanciaPeso()) {
                msj += " ⚠ ALERTA: La diferencia de peso supera el 1% de tolerancia.";
            }
            redirectAttrs.addAttribute("exito", msj);

        } catch (Exception e) {
            redirectAttrs.addAttribute("error", "Error interno al procesar el inventario.");
        }
        return "redirect:/inventario";
    }
}
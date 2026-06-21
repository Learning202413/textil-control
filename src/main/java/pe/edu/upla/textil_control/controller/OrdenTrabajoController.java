package pe.edu.upla.textil_control.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.upla.textil_control.model.OrdenTrabajo;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.service.ModeloPrendaService;
import pe.edu.upla.textil_control.service.OrdenTrabajoService;
// Importa tu ModeloPrendaService y UsuarioService aquí
import java.util.Set;

@Controller
@RequestMapping("/ordenes-trabajo")
public class OrdenTrabajoController {

    @Autowired private OrdenTrabajoService otService;
    @Autowired
    private ModeloPrendaService modeloService; // <-- Asegúrate de tener esto
    private boolean tienePermiso(HttpSession session, String permiso) {
        @SuppressWarnings("unchecked")
        Set<String> permisos = (Set<String>) session.getAttribute("permisosUsuario");
        return permisos != null && permisos.contains(permiso);
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        if (!tienePermiso(session, "PROD_OT_VER")) return "redirect:/dashboard?error=sinPermiso";

        model.addAttribute("ordenes", otService.listarTodas());
        model.addAttribute("codigoPreview", otService.generarSiguienteCodigo());

        // 🔥 ESTA ES LA LÍNEA EXACTA PARA TU CÓDIGO 🔥
        model.addAttribute("modelosPrenda", modeloService.listarResumen());

        return "ordenes_trabajo";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute OrdenTrabajo ot, @RequestParam(required = false) String accion, HttpSession session, RedirectAttributes ra) {
        if (!tienePermiso(session, "PROD_OT_CREAR")) return "redirect:/dashboard?error=sinPermiso";
        Usuario u = (Usuario) session.getAttribute("usuarioSesion");
        ot.setIdResponsable(u.getIdUsuario());
        try {
            otService.guardarOrden(ot, accion);
            ra.addFlashAttribute("exito", "Orden de Trabajo procesada exitosamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/ordenes-trabajo";
    }

    @PostMapping("/estado")
    public String estado(@RequestParam Integer idOt, @RequestParam String estado, HttpSession session, RedirectAttributes ra) {
        if (!tienePermiso(session, "PROD_OT_CREAR")) return "redirect:/dashboard?error=sinPermiso";
        try {
            otService.cambiarEstado(idOt, estado);
            ra.addFlashAttribute("exito", "Estado actualizado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ordenes-trabajo";
    }

    @PostMapping("/anular")
    public String anular(@RequestParam Integer idOt, HttpSession session, RedirectAttributes ra) {
        if (!tienePermiso(session, "PROD_OT_CREAR")) return "redirect:/dashboard?error=sinPermiso";
        if (otService.anularOrden(idOt)) ra.addFlashAttribute("exito", "OT anulada.");
        else ra.addFlashAttribute("error", "No se puede eliminar: Tiene telas asignadas.");
        return "redirect:/ordenes-trabajo";
    }

    // ── ENDPOINT PARA LA API RENIEC / SUNAT ──
    @GetMapping(value = "/buscarCliente", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public org.springframework.http.ResponseEntity<String> buscarClienteAPI(@RequestParam String doc, HttpSession session) {
        if (!tienePermiso(session, "PROD_OT_CREAR")) {
            return org.springframework.http.ResponseEntity.status(401).body("{}");
        }
        String json = otService.buscarClienteApi(doc);
        return org.springframework.http.ResponseEntity.ok(json);
    }
}
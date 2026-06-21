package pe.edu.upla.textil_control.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.service.ConciliacionDespachoService;
import java.util.Set;

@Controller
@RequestMapping("/despacho")
public class DespachoController {

    @Autowired private ConciliacionDespachoService despachoService;

    private boolean tienePermiso(HttpSession session, String permiso) {
        @SuppressWarnings("unchecked")
        Set<String> permisos = (Set<String>) session.getAttribute("permisosUsuario");
        return permisos != null && permisos.contains(permiso);
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String accion, @RequestParam(required = false) Integer idOt, Model model, HttpSession session) {
        if (!tienePermiso(session, "DES_CONCIL_REG")) return "redirect:/dashboard?error=sinPermiso";

        if ("nota".equals(accion) && idOt != null) {
            // Reutilizamos el DTO que ya trae todos los cruces (Cliente, Modelo, etc.)
            pe.edu.upla.textil_control.repository.ConciliacionDespachoResumenDTO notaCompleta =
                    despachoService.listarLotes().stream()
                            .filter(l -> l.getIdOt().equals(idOt))
                            .findFirst()
                            .orElse(null);

            model.addAttribute("conciliacion", notaCompleta);
            return "nota_despacho";
        }

        model.addAttribute("lotes", despachoService.listarLotes());
        return "despacho";
    }

    @PostMapping("/conciliar")
    public String conciliar(@RequestParam Integer idOt, @RequestParam Integer cantidadFinal, @RequestParam String observaciones, HttpSession session, RedirectAttributes ra) {
        if (!tienePermiso(session, "DES_CONCIL_REG")) return "redirect:/dashboard?error=sinPermiso";
        Usuario u = (Usuario) session.getAttribute("usuarioSesion");
        try {
            despachoService.registrarConciliacion(idOt, cantidadFinal, u.getIdUsuario(), observaciones);
            ra.addFlashAttribute("exito", "Conciliación registrada.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error BD: " + e.getMessage());
        }
        return "redirect:/despacho";
    }

    @PostMapping("/despachar")
    public String despachar(@RequestParam Integer idConciliacion, HttpSession session, RedirectAttributes ra) {
        if (!tienePermiso(session, "DES_CONCIL_REG")) return "redirect:/dashboard?error=sinPermiso";
        try {
            if (despachoService.confirmarDespacho(idConciliacion)) {
                ra.addFlashAttribute("exito", "Despacho confirmado y nota generada.");
            } else {
                ra.addFlashAttribute("error", "No se pudo confirmar el despacho.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error BD: " + e.getMessage());
        }
        return "redirect:/despacho";
    }
}
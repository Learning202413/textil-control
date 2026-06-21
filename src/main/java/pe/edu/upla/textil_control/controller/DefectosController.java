package pe.edu.upla.textil_control.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.upla.textil_control.model.DefectoReproceso;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.repository.DefectoResumenDTO;
import pe.edu.upla.textil_control.service.DefectoReprocesoService;
// IMPORTANTE: Asegúrate de tener este servicio inyectado si necesitas crear la tarea
// import pe.edu.upla.textil_control.service.AsignacionCargaService;
import java.util.Set;

@Controller
@RequestMapping("/defectos")
public class DefectosController {

    @Autowired private DefectoReprocesoService defectoService;
    // @Autowired private AsignacionCargaService asignacionService;

    private boolean tienePermiso(HttpSession session, String permiso) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario != null && usuario.getIdRol() == 6) return true;
        @SuppressWarnings("unchecked")
        Set<String> permisos = (Set<String>) session.getAttribute("permisosUsuario");
        return permisos != null && permisos.contains(permiso);
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        if (!tienePermiso(session, "CAL_DEFECTOS_REG")) return "redirect:/dashboard?error=sinPermiso";
        model.addAttribute("defectos", defectoService.listarTodos());
        model.addAttribute("resumenReprocesos", defectoService.resumenReprocesos());
        return "defectos";
    }

    // 🔥 LA SOLUCIÓN: Un solo POST que atrape todas las acciones
    @PostMapping
    public String procesarAcciones(
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) Integer idDefecto,
            @RequestParam(required = false) String observaciones,
            @RequestParam(required = false) String tipoFalla,
            HttpSession session,
            RedirectAttributes ra) {

        if (!tienePermiso(session, "CAL_DEFECTOS_REG")) return "redirect:/dashboard?error=sinPermiso";

        // Validación defensiva
        if (accion == null || idDefecto == null) {
            ra.addFlashAttribute("error", "Error: No se recibió la acción o el ID del defecto.");
            return "redirect:/defectos";
        }

        try {
            switch (accion) {
                case "revertir":
                    DefectoResumenDTO def = defectoService.listarTodos().stream()
                            .filter(d -> d.getIdDefecto().equals(idDefecto))
                            .findFirst()
                            .orElseThrow(() -> new Exception("Defecto no encontrado"));

                    defectoService.corregirOriginal(idDefecto, def.getIdAsignacion(), def.getCantidadFaltante());
                    ra.addFlashAttribute("exito", "Error revertido. La carga de trabajo fue restaurada.");
                    break;

                case "reponer":
                    if (tipoFalla == null || tipoFalla.isEmpty()) {
                        ra.addFlashAttribute("error", "El tipo de falla es obligatorio.");
                        return "redirect:/defectos";
                    }
                    defectoService.marcarRepuesto(idDefecto, observaciones);
                    defectoService.actualizarTipoFalla(idDefecto, tipoFalla);
                    ra.addFlashAttribute("exito", "Falla registrada y tarea de reposición generada.");
                    break;

                case "completar":
                    if (tipoFalla == null || tipoFalla.isEmpty()) {
                        ra.addFlashAttribute("error", "El tipo de falla es obligatorio.");
                        return "redirect:/defectos";
                    }
                    // 🔥 Corrección: Usamos completarDefecto para no generar tareas extra
                    defectoService.completarDefecto(idDefecto, tipoFalla, observaciones);
                    ra.addFlashAttribute("exito", "Defecto clasificado y registrado exitosamente.");
                    break;
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al procesar: " + e.getMessage());
        }

        return "redirect:/defectos";
    }
}
package pe.edu.upla.textil_control.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.repository.FasePendienteDTO; // 🔥 Importante
import pe.edu.upla.textil_control.service.AsignacionCargaService;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cargas-trabajo")
public class CargasTrabajoController {

    @Autowired
    private AsignacionCargaService cargaService;
    @Autowired
    private pe.edu.upla.textil_control.repository.UsuarioRepository usuarioRepo;
    // Helper original
    private boolean tienePermiso(HttpSession session, String permiso) {
        @SuppressWarnings("unchecked")
        Set<String> permisos = (Set<String>) session.getAttribute("permisosUsuario");
        return permisos != null && permisos.contains(permiso);
    }

    @GetMapping
    public String index(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "redirect:/login";

        if (usuario.getIdRol() == 6) {
            model.addAttribute("tareas", cargaService.listarTareasPorMaquinista(usuario.getIdUsuario()));
            model.addAttribute("tareasCompletadas", cargaService.listarTareasCompletadasPorMaquinista(usuario.getIdUsuario()));
            return "mis_tareas";
        }

        if (!tienePermiso(session, "PROD_CARGAS_ASIG")) {
            return "redirect:/dashboard?error=sinPermiso";
        }

        // 1. Obtenemos la lista de la BD
        List<FasePendienteDTO> pendientes = cargaService.listarFasesPendientes();
        model.addAttribute("fasesPendientes", pendientes);

        // 2. 🔥 AGRUPACIÓN TRASLADADA DEL JSP AL CONTROLADOR (Java Seguro)
        Map<String, List<FasePendienteDTO>> porOT = pendientes.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getCodigoOt() != null ? f.getCodigoOt() : "Sin OT",
                        LinkedHashMap::new, // Mantiene el orden original
                        Collectors.toList()
                ));
        model.addAttribute("porOT", porOT);

        model.addAttribute("resumenCarga", cargaService.resumenCargaPorMaquinista());

        // 🔥 AÑADE ESTAS LÍNEAS 🔥
        // Obtenemos todos los usuarios con ID de Rol 6 (Maquinistas)
        List<Usuario> maquinistas = usuarioRepo.findByIdRol(6).stream()
                .filter(u -> u.getActivo() != null && u.getActivo()) // Solo maquinistas activos
                .collect(Collectors.toList());

        model.addAttribute("maquinistas", maquinistas);

        return "cargas_trabajo";
    }

    @PostMapping("/asignar")
    public String asignar(@RequestParam Integer idAsignacion, @RequestParam Integer idMaquinista, HttpSession session, RedirectAttributes ra) {
        if (!tienePermiso(session, "PROD_CARGAS_ASIG")) return "redirect:/dashboard?error=sinPermiso";
        try {
            cargaService.asignarMaquinista(idAsignacion, idMaquinista);
            ra.addFlashAttribute("exito", "Tarea asignada correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cargas-trabajo";
    }

    @PostMapping("/reasignar")
    public String reasignar(@RequestParam Integer idAsignacion, @RequestParam Integer idMaquinista, HttpSession session, RedirectAttributes ra) {
        if (!tienePermiso(session, "PROD_CARGAS_ASIG")) return "redirect:/dashboard?error=sinPermiso";
        try {
            cargaService.reasignarMaquinista(idAsignacion, idMaquinista);
            ra.addFlashAttribute("exito", "Tarea reasignada correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cargas-trabajo";
    }

    @PostMapping("/completar")
    public String completar(@RequestParam Integer idAsignacion, @RequestParam Integer piezasCompletadas, HttpSession session, RedirectAttributes ra) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "redirect:/login";

        if (usuario.getIdRol() != 6 && !tienePermiso(session, "PROD_CARGAS_ASIG")) {
            return "redirect:/dashboard?error=sinPermiso";
        }

        try {
            cargaService.completarFase(idAsignacion, piezasCompletadas);
            ra.addFlashAttribute("exito", "Tarea completada exitosamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cargas-trabajo";
    }
}
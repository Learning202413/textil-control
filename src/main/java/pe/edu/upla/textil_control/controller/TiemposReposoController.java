package pe.edu.upla.textil_control.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.repository.TiempoReposoResumenDTO;
import pe.edu.upla.textil_control.service.TiempoReposoService;

import java.util.*;

/**
 * Controller: /tiempos-reposo
 * HU03: Gestión de Tiempos de Reposo y Corte
 *
 * Migrado desde TiemposReposoServlet — LÓGICA PRESERVADA ÍNTEGRAMENTE.
 *
 * Permisos:
 *   PROD_REPOSO_VER      → listar / ver
 *   PROD_REPOSO_GESTION  → registrar inicio, marcar apto, cancelar
 */
@Controller
@RequestMapping("/tiempos-reposo")
public class TiemposReposoController {

    @Autowired
    private TiempoReposoService reposoService;

    // ─── GET: Listar ─────────────────────────────────────────
    @GetMapping
    public String listar(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "redirect:/login";

        if (!tienePermiso(session, "PROD_REPOSO_VER")) {
            return "redirect:/dashboard?error=sinPermiso";
        }

        try {
            var lista = reposoService.listarSegunRol(usuario);
            var telasDisponibles = reposoService.listarTelasDisponibles();

            // Separar activos e historial — PRESERVADO del JSP original
            var activos = new java.util.ArrayList<TiempoReposoResumenDTO>();
            var historial = new java.util.ArrayList<TiempoReposoResumenDTO>();
            for (var tr : lista) {
                if ("EN_REPOSO".equals(tr.getEstado())) {
                    activos.add(tr);
                } else {
                    historial.add(tr);
                }
            }

            model.addAttribute("reposoList", lista);
            model.addAttribute("activos", activos);
            model.addAttribute("historial", historial);
            model.addAttribute("telasDisponibles", telasDisponibles);
            model.addAttribute("hayTelasParaReposo", !telasDisponibles.isEmpty());

        } catch (Exception e) {
            model.addAttribute("errorBD", "Error al cargar los tiempos de reposo: " + e.getMessage());
        }

        return "tiempos_reposo";
    }

    // ─── GET: Polling AJAX ───────────────────────────────────
    @GetMapping("/polling")
    @ResponseBody
    public String polling(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "{\"error\":\"noSession\"}";

        try {
            // PRESERVADO: primero verificar vencimientos, luego devolver lista
            reposoService.verificarYNotificar();
            var activos = reposoService.listarSegunRol(usuario);
            return reposoService.construirJsonActivos(activos);
        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
        }
    }

    // ─── POST: Iniciar Reposo ────────────────────────────────
    @PostMapping("/iniciar")
    public String iniciar(@RequestParam int idTela,
                          @RequestParam int duracionMinutos,
                          @RequestParam(required = false) String observaciones,
                          HttpSession session, RedirectAttributes ra) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "redirect:/login";
        if (!tienePermiso(session, "PROD_REPOSO_GESTION")) return "redirect:/dashboard?error=sinPermiso";

        try {
            reposoService.registrarInicio(idTela, usuario.getIdUsuario(),
                                           duracionMinutos, observaciones);
            ra.addAttribute("exito", "iniciado");
        } catch (IllegalStateException e) {
            ra.addAttribute("error", e.getMessage());
        } catch (NumberFormatException e) {
            ra.addAttribute("error", "datosInvalidos");
        } catch (Exception e) {
            ra.addAttribute("error", "falloInsercion");
        }

        return "redirect:/tiempos-reposo";
    }

    // ─── POST: Marcar Apto para Corte ────────────────────────
    @PostMapping("/apto")
    public String marcarApto(@RequestParam int idReposo,
                             HttpSession session, RedirectAttributes ra) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "redirect:/login";
        if (!tienePermiso(session, "PROD_REPOSO_GESTION")) return "redirect:/dashboard?error=sinPermiso";

        boolean ok = reposoService.marcarAptoCorte(idReposo);
        ra.addAttribute(ok ? "exito" : "error", ok ? "apto" : "noActualizado");
        return "redirect:/tiempos-reposo";
    }

    // ─── POST: Cancelar ──────────────────────────────────────
    @PostMapping("/cancelar")
    public String cancelar(@RequestParam int idReposo,
                           HttpSession session, RedirectAttributes ra) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "redirect:/login";
        if (!tienePermiso(session, "PROD_REPOSO_GESTION")) return "redirect:/dashboard?error=sinPermiso";

        boolean ok = reposoService.cancelar(idReposo);
        ra.addAttribute(ok ? "exito" : "error", ok ? "cancelado" : "noActualizado");
        return "redirect:/tiempos-reposo";
    }

    // ─── POST: Eliminar ──────────────────────────────────────
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam int idReposo,
                           HttpSession session, RedirectAttributes ra) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "redirect:/login";
        if (!tienePermiso(session, "PROD_REPOSO_GESTION")) return "redirect:/dashboard?error=sinPermiso";

        try {
            reposoService.eliminar(idReposo);
            ra.addFlashAttribute("exito", "Registro de tiempo de reposo eliminado correctamente.");
        } catch (IllegalStateException e) {
            // 🔥 AQUÍ SE ATRAPA EL BLOQUEO Y SE ENVÍA EL MENSAJE A LA VISTA
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error al intentar eliminar el registro.");
        }

        return "redirect:/tiempos-reposo";
    }

    // ─── Helper ──────────────────────────────────────────────
    private boolean tienePermiso(HttpSession session, String permiso) {
        @SuppressWarnings("unchecked")
        Set<String> permisos = (Set<String>) session.getAttribute("permisosUsuario");
        if (permisos != null && permisos.contains(permiso)) return true;

        Usuario u = (Usuario) session.getAttribute("usuarioSesion");
        return u != null && "ADMINISTRADOR".equalsIgnoreCase(u.getNombreRol());
    }
}

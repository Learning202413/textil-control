package pe.edu.upla.textil_control.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.service.ReportesService;

import java.util.*;

/**
 * Controller: /reportes
 *
 * Migración del ReportesServlet.java (Java EE) a Spring Boot.
 * Lógica de selección de datos y permisos PRESERVADA ÍNTEGRAMENTE.
 *
 * GET /reportes → valida sesión, detecta rol, llama al Service,
 *                 pasa datos al Model, retorna vista "reportes"
 */
@Controller
@RequestMapping("/reportes")
public class ReportesController {

    @Autowired
    private ReportesService reportesService;

    @GetMapping
    public String mostrarReportes(HttpSession session, Model model) {

        // ── Validación de sesión ─────────────────────────────────────────────
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) {
            return "redirect:/login";
        }

        @SuppressWarnings("unchecked")
        Set<String> permisos = (Set<String>) session.getAttribute("permisosUsuario");
        if (permisos == null) permisos = new HashSet<>();

        String rol = usuario.getNombreRol();
        boolean esAdmin     = "ADMINISTRADOR".equalsIgnoreCase(rol);

        // Verificar permiso de acceso a reportes — PRESERVADO del ReportesServlet
        boolean tieneAcceso = permisos.contains("RPT_MERMAS_CALIDAD")
                || permisos.contains("RPT_DASHBOARD")
                || esAdmin
                || reportesService.esRolGerencial(rol)
                || "MAQUINISTA".equalsIgnoreCase(rol)
                || "TIZADOR".equalsIgnoreCase(rol);

        if (!tieneAcceso) {
            return "redirect:/dashboard?error=sinPermiso";
        }

        try {
            // ── Datos siempre disponibles ────────────────────────────────────
            model.addAttribute("mermaPorOT",         reportesService.obtenerMermaPorOT(usuario));
            model.addAttribute("tiemposMaquinistas",  reportesService.obtenerTiemposMaquinistas(usuario));
            model.addAttribute("fallasPorTela",       reportesService.obtenerFallasPorTela(usuario));
            model.addAttribute("eficienciaGlobal",    reportesService.obtenerEficienciaGlobal());
            model.addAttribute("inventarioTelas",     reportesService.obtenerInventarioTelas());

            // ── Reportes estratégicos — solo roles gerenciales ────────────────
            // PRESERVADO del ReportesServlet: bloque if (r.contains("ADMIN") || ...)
            if (reportesService.esRolGerencial(rol)) {
                model.addAttribute("calidadVsProductividad", reportesService.obtenerCalidadVsProductividad());
                model.addAttribute("otsProblematicas",       reportesService.obtenerOTsProblematicas());
                model.addAttribute("desviacionDespacho",     reportesService.obtenerDesviacionesDespacho());
            } else {
                model.addAttribute("calidadVsProductividad", null);
                model.addAttribute("otsProblematicas",       null);
                model.addAttribute("desviacionDespacho",     null);
            }

            // ── Rendimiento personal — solo MAQUINISTA ────────────────────────
            // PRESERVADO del ReportesServlet: bloque if (r.contains("MAQUINISTA"))
            if ("MAQUINISTA".equalsIgnoreCase(rol)) {
                model.addAttribute("rendimientoMaquinista", reportesService.obtenerRendimientoMaquinista(usuario.getIdUsuario()));
            } else {
                model.addAttribute("rendimientoMaquinista", null);
            }

            // ── Flags de rol para la vista ────────────────────────────────────
            // Identicos a las banderas del reportes.jsp original
            String r = rol.toUpperCase();
            model.addAttribute("esAdmin",      esAdmin);
            model.addAttribute("esAlmacen",    r.contains("JEFE_ALMACEN") || esAdmin);
            model.addAttribute("esProduccion", r.contains("JEFE_PRODUCCION") || esAdmin);
            model.addAttribute("esTizador",    r.contains("TIZADOR") || r.contains("JEFE_PRODUCCION") || esAdmin);
            model.addAttribute("esSupervisor", r.contains("SUPERVISOR") || r.contains("JEFE_PRODUCCION") || esAdmin);
            model.addAttribute("esMaquinista", r.contains("MAQUINISTA"));
            model.addAttribute("rolUsuario",   rol);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al generar los reportes: " + e.getMessage());
        }

        return "reportes";
    }
}

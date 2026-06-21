package pe.edu.upla.textil_control.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.upla.textil_control.model.FallaTela;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.repository.FallaTelaResumenDTO;
import pe.edu.upla.textil_control.service.FallaTelaService;

import java.math.BigDecimal;
import java.util.*;

/**
 * Controller: /fallas-tela
 * HU02: Mapeo Digital de Imperfecciones y Fallas en la Tela
 *
 * Migrado desde FallasTelaServlet — LÓGICA PRESERVADA ÍNTEGRAMENTE.
 *
 * Permisos:
 *   PROD_FALLAS_VER  → listar / ver
 *   PROD_FALLAS_REG  → registrar / actualizar / eliminar
 */
@Controller
@RequestMapping("/fallas-tela")
public class FallasTelaController {

    @Autowired
    private FallaTelaService fallaTelaService;

    // ─── GET ─────────────────────────────────────────────────
    @GetMapping
    public String listar(@RequestParam(required = false) String accion,
                         @RequestParam(required = false) String idTela,
                         HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "redirect:/login";

        @SuppressWarnings("unchecked")
        Set<String> permisos = (Set<String>) session.getAttribute("permisosUsuario");
        if (permisos == null) permisos = new HashSet<>();

        boolean esAdmin = "ADMINISTRADOR".equalsIgnoreCase(usuario.getNombreRol());

        if (!permisos.contains("PROD_FALLAS_VER") && !esAdmin) {
            return "redirect:/dashboard?error=sinPermiso";
        }

        try {
            List<FallaTelaResumenDTO> lista;

            // LÓGICA PRESERVADA: filtro por tela o listado por rol
            if ("porTela".equals(accion) && idTela != null && !idTela.isBlank()) {
                lista = fallaTelaService.listarPorTela(Integer.parseInt(idTela));
                model.addAttribute("idTelaFiltro", idTela);
            } else {
                lista = fallaTelaService.listarSegunRol(usuario);
            }

            boolean puedeReg = permisos.contains("PROD_FALLAS_REG") || esAdmin;

            model.addAttribute("fallasList", lista);
            model.addAttribute("telasParaMapeo", puedeReg
                    ? fallaTelaService.listarTelasParaMapeo()
                    : new ArrayList<>());
            model.addAttribute("telasConFallas", fallaTelaService.listarTelasConFallas());
            model.addAttribute("puedeRegistrar", puedeReg);

        } catch (Exception e) {
            model.addAttribute("errorBD", "Error al cargar fallas: " + e.getMessage());
        }

        return "fallas_tela";
    }

    // ─── POST: Registrar ─────────────────────────────────────
    @PostMapping("/registrar")
    public String registrar(@RequestParam int idTela,
                            @RequestParam String tipoFalla,
                            @RequestParam int posicionRollo,
                            @RequestParam BigDecimal posicionMetro,
                            @RequestParam(required = false) BigDecimal anchoCm,
                            @RequestParam(required = false) BigDecimal largoCm,
                            @RequestParam(required = false) String descripcion,
                            @RequestParam(required = false, defaultValue = "true") String esAreaNoApta,
                            HttpSession session, RedirectAttributes ra) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "redirect:/login";
        if (!tienePermiso(session, "PROD_FALLAS_REG")) return "redirect:/dashboard?error=sinPermiso";

        try {
            FallaTela f = new FallaTela();
            f.setIdTela(idTela);
            f.setIdTizador(usuario.getIdUsuario());
            f.setTipoFalla(FallaTela.TipoFalla.valueOf(tipoFalla));
            f.setPosicionRollo(posicionRollo);
            f.setPosicionMetro(posicionMetro);
            f.setAnchoCm(anchoCm);
            f.setLargoCm(largoCm);
            f.setDescripcion(descripcion);
            // CUS 2.3: área no apta se activa siempre por defecto — PRESERVADO
            f.setEsAreaNoApta(!"false".equals(esAreaNoApta));

            fallaTelaService.registrar(f);
            ra.addAttribute("exito", "registrado");
        } catch (NumberFormatException e) {
            ra.addAttribute("error", "datosInvalidos");
        } catch (Exception e) {
            ra.addAttribute("error", "bd");
        }

        return "redirect:/fallas-tela";
    }

    // ─── POST: Actualizar ────────────────────────────────────
    @PostMapping("/actualizar")
    public String actualizar(@RequestParam int idFalla,
                             @RequestParam int idTela,
                             @RequestParam String tipoFalla,
                             @RequestParam int posicionRollo,
                             @RequestParam BigDecimal posicionMetro,
                             @RequestParam(required = false) BigDecimal anchoCm,
                             @RequestParam(required = false) BigDecimal largoCm,
                             @RequestParam(required = false) String descripcion,
                             @RequestParam(required = false, defaultValue = "false") String esAreaNoApta,
                             HttpSession session, RedirectAttributes ra) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "redirect:/login";
        if (!tienePermiso(session, "PROD_FALLAS_REG")) return "redirect:/dashboard?error=sinPermiso";

        try {
            FallaTela f = new FallaTela();
            f.setIdFalla(idFalla);
            f.setIdTela(idTela);
            f.setIdTizador(usuario.getIdUsuario());
            f.setTipoFalla(FallaTela.TipoFalla.valueOf(tipoFalla));
            f.setPosicionRollo(posicionRollo);
            f.setPosicionMetro(posicionMetro);
            f.setAnchoCm(anchoCm);
            f.setLargoCm(largoCm);
            // Soporta tanto envíos de checkbox HTML ("on") como envíos AJAX/JSON ("true")
            f.setEsAreaNoApta("true".equals(esAreaNoApta) || "on".equals(esAreaNoApta));
            f.setDescripcion(descripcion);

            fallaTelaService.actualizar(f);
            ra.addFlashAttribute("exito", "Falla actualizada correctamente.");

        } catch (IllegalStateException e) {
            // 🔥 AQUÍ SE ATRAPA LA REGLA DE NEGOCIO: Bloquea si la OT está FINALIZADA o ANULADA
            ra.addFlashAttribute("error", e.getMessage());

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error al intentar actualizar el registro.");
        }

        return "redirect:/fallas-tela";
    }

    // ─── POST: Eliminar ──────────────────────────────────────
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam int idFalla,
                           HttpSession session, RedirectAttributes ra) {

        if (!tienePermiso(session, "PROD_FALLAS_REG")) return "redirect:/dashboard?error=sinPermiso";

        try {
            fallaTelaService.eliminar(idFalla);
            ra.addFlashAttribute("exito", "Falla eliminada correctamente.");
        } catch (IllegalStateException e) {
            // Atrapa la validación de OT bloqueada (FINALIZADA / ANULADA)
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error al intentar eliminar el registro.");
        }

        return "redirect:/fallas-tela";
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

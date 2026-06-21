package pe.edu.upla.textil_control.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.upla.textil_control.model.Merma;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.service.MermaService;

import java.math.BigDecimal;
import java.util.*;

/**
 * Controller: /mermas
 * HU04: Registro de Merma por Tipo de Tejido
 *
 * Migrado desde MermasServlet — LÓGICA PRESERVADA ÍNTEGRAMENTE.
 *
 * Permisos:
 *   PROD_MERMA_VER → listar (Admin, Jefe Producción, Tizador)
 *   PROD_MERMA_REG → registrar / eliminar (Admin, Tizador)
 */
@Controller
@RequestMapping("/mermas")
public class MermasController {

    @Autowired
    private MermaService mermaService;

    // ─── GET ─────────────────────────────────────────────────
    @GetMapping
    public String listar(@RequestParam(required = false) String accion,
                         @RequestParam(required = false) String idOt,
                         HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "redirect:/login";

        @SuppressWarnings("unchecked")
        Set<String> permisos = (Set<String>) session.getAttribute("permisosUsuario");
        if (permisos == null) permisos = new HashSet<>();

        boolean esAdmin = "ADMINISTRADOR".equalsIgnoreCase(usuario.getNombreRol());

        if (!permisos.contains("PROD_MERMA_VER") && !esAdmin) {
            return "redirect:/dashboard?error=sinPermiso";
        }

        try {
            // LÓGICA PRESERVADA: parseo de idOt (ignorar "null", vacío, no numérico)
            Integer idOtInt = null;
            if (idOt != null && !idOt.isEmpty() && !"null".equalsIgnoreCase(idOt)) {
                try {
                    idOtInt = Integer.parseInt(idOt);
                } catch (NumberFormatException e) {
                    // No es número válido, ignorar — PRESERVADO
                }
            }

            var lista = ("porOt".equals(accion) && idOtInt != null)
                    ? mermaService.listarPorOt(idOtInt)
                    : mermaService.listarSegunRol(usuario);

            if ("porOt".equals(accion) && idOtInt != null) {
                model.addAttribute("idOtFiltro", idOt);
            }

            // Calcular % acumulado por OT si hay filtro activo — PRESERVADO
            BigDecimal pctOt = null;
            if (idOtInt != null) {
                pctOt = mermaService.calcularPorcentajePorOt(idOtInt);
            }

            boolean puedeReg = permisos.contains("PROD_MERMA_REG") || esAdmin;

            model.addAttribute("mermasList", lista);
            model.addAttribute("telasParaMerma", puedeReg
                    ? mermaService.listarTelasParaMerma()
                    : new ArrayList<>());
            model.addAttribute("otsConMermas", mermaService.listarOtsConMermas());
            model.addAttribute("puedeRegistrar", puedeReg);
            model.addAttribute("pctOtActivo", pctOt);

        } catch (Exception e) {
            model.addAttribute("errorBD", "Error al cargar mermas: " + e.getMessage());
        }

        return "mermas";
    }

    // ─── POST: Registrar ─────────────────────────────────────
    @PostMapping("/registrar")
    public String registrar(@RequestParam int idTela,
                            @RequestParam int idOt,
                            @RequestParam String fase,
                            @RequestParam BigDecimal pesoUtilizadoKg,
                            @RequestParam BigDecimal pesomermaKg,
                            @RequestParam(required = false) String observaciones,
                            HttpSession session, RedirectAttributes ra) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "redirect:/login";
        if (!tienePermiso(session, "PROD_MERMA_REG")) return "redirect:/dashboard?error=sinPermiso";

        try {
            Merma m = new Merma();
            m.setIdTela(idTela);
            m.setIdOt(idOt);
            m.setIdTizador(usuario.getIdUsuario());
            m.setFase(Merma.Fase.valueOf(fase));
            m.setPesoUtilizadoKg(pesoUtilizadoKg);
            m.setPesomermaKg(pesomermaKg);
            m.setObservaciones(observaciones != null ? observaciones : "");

            Merma guardada = mermaService.registrar(m);
            // PRESERVADO: redirigir con idOt para mostrar % acumulado
            ra.addAttribute("exito", "registrado");
            ra.addAttribute("idOt", idOt);
        } catch (IllegalStateException e) {
            ra.addAttribute("error", e.getMessage());
        } catch (NumberFormatException e) {
            ra.addAttribute("error", "datosInvalidos");
        } catch (Exception e) {
            ra.addAttribute("error", "bd");
        }

        return "redirect:/mermas";
    }

    // ─── POST: Actualizar ────────────────────────────────────
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Merma merma,
                             HttpSession session, RedirectAttributes ra) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "redirect:/login";

        if (!"ADMINISTRADOR".equalsIgnoreCase(usuario.getNombreRol())) {
            ra.addFlashAttribute("error", "No tienes permisos para editar este registro.");
            return "redirect:/mermas";
        }

        try {
            mermaService.actualizar(merma);
            ra.addFlashAttribute("exito", "Registro de merma actualizado correctamente.");

        } catch (IllegalStateException e) {
            // Atrapa la validación de OT bloqueada (FINALIZADA / ANULADA) o peso excedido
            ra.addFlashAttribute("error", e.getMessage());

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error al intentar actualizar el registro.");
        }

        return "redirect:/mermas";
    }

    // ─── POST: Eliminar ──────────────────────────────────────
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam int idMerma,
                           HttpSession session, RedirectAttributes ra) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return "redirect:/login";

        if (!"ADMINISTRADOR".equalsIgnoreCase(usuario.getNombreRol())) {
            ra.addFlashAttribute("error", "No tienes permisos para eliminar este registro.");
            return "redirect:/mermas";
        }

        try {
            mermaService.eliminar(idMerma);
            ra.addFlashAttribute("exito", "Registro de merma eliminado.");

        } catch (IllegalStateException e) {
            // Atrapa la validación de OT bloqueada (FINALIZADA / ANULADA)
            ra.addFlashAttribute("error", e.getMessage());

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error al intentar eliminar el registro.");
        }

        return "redirect:/mermas";
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

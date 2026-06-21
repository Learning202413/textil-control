package pe.edu.upla.textil_control.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.repository.RolRepository;
import pe.edu.upla.textil_control.service.GestionUsuariosService;

/**
 * GestionUsuariosController — CORREGIDO.
 *
 * Cambio principal:
 *  - desactivar() y activar() ahora llaman a usuarioService.cambiarEstado()
 *    que hace un UPDATE solo del campo 'activo', igual que el original.
 *    Antes llamaban a buscarPorId() + actualizar() completo, lo que causaba
 *    el error 500 porque Hibernate intentaba escribir campos TIME y NULL.
 */
@Controller
@RequestMapping("/gestion-usuarios")
public class GestionUsuariosController {

    @Autowired
    private GestionUsuariosService usuarioService;

    @Autowired
    private RolRepository rolRepository;

    // ── Helper ────────────────────────────────────────────────

    private boolean esAdministrador(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioSesion");
        if (u == null) return false;

        String nombreRol = (String) session.getAttribute("nombreRol");
        if (nombreRol != null) {
            return "ADMINISTRADOR".equalsIgnoreCase(nombreRol);
        }
        return u.getNombreRol() != null && "ADMINISTRADOR".equalsIgnoreCase(u.getNombreRol());
    }

    // ── GET: listar ───────────────────────────────────────────

    @GetMapping
    public String listar(HttpSession session, Model model) {
        if (!esAdministrador(session)) {
            return "redirect:/catalogo-telas?error=acceso";
        }
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("roles",    rolRepository.findAll());
        return "gestion_usuarios";
    }

    // ── POST: guardar nuevo usuario ───────────────────────────

    @PostMapping(params = "accion=guardar")
    public String guardar(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String nombre,
                          @RequestParam String apellido,
                          @RequestParam String email,
                          @RequestParam String idRol,
                          @RequestParam(value = "horarioRestringidoHidden", defaultValue = "false") String horarioRestringido,
                          @RequestParam(value = "horarioDias",   required = false) String horarioDias,
                          @RequestParam(value = "horarioInicio", required = false) String horarioInicio,
                          @RequestParam(value = "horarioFin",    required = false) String horarioFin,
                          HttpSession session,
                          RedirectAttributes ra) {

        if (!esAdministrador(session)) return "redirect:/catalogo-telas?error=acceso";

        String err = validarNuevo(username, password, nombre, apellido, email, idRol);
        if (err != null) {
            ra.addFlashAttribute("msgError", err);
            return "redirect:/gestion-usuarios";
        }

        Usuario u = new Usuario();
        u.setUsername(username.trim());
        u.setPassword(password);
        u.setNombre(nombre.trim());
        u.setApellido(apellido.trim());
        u.setEmail(email.trim());
        u.setIdRol(Integer.parseInt(idRol));
        u.setActivo(true);
        u.setHorarioRestringido("true".equalsIgnoreCase(horarioRestringido));
        u.setHorarioDias(horarioDias);
        u.setHorarioInicio(horarioInicio);
        u.setHorarioFin(horarioFin);

        try {
            usuarioService.insertar(u);
            ra.addAttribute("exito", "Usuario '" + username + "' creado exitosamente.");
        } catch (Exception e) {
            String msg = (e.getMessage() != null && e.getMessage().contains("Duplicate"))
                    ? "El username o email ya está registrado."
                    : "Error al crear usuario: " + e.getMessage();
            ra.addAttribute("error", msg);
        }
        return "redirect:/gestion-usuarios";
    }

    // ── POST: actualizar usuario existente ────────────────────

    @PostMapping(params = "accion=actualizar")
    public String actualizar(@RequestParam Integer idUsuario,
                             @RequestParam String nombre,
                             @RequestParam String apellido,
                             @RequestParam String email,
                             @RequestParam String idRol,
                             @RequestParam(value = "password",   defaultValue = "") String password,
                             @RequestParam(value = "activo",     defaultValue = "true") String activo,
                             @RequestParam(value = "horarioRestringidoHidden", defaultValue = "false") String horarioRestringido,
                             @RequestParam(value = "horarioDias",   required = false) String horarioDias,
                             @RequestParam(value = "horarioInicio", required = false) String horarioInicio,
                             @RequestParam(value = "horarioFin",    required = false) String horarioFin,
                             HttpSession session,
                             RedirectAttributes ra) {

        if (!esAdministrador(session)) return "redirect:/catalogo-telas?error=acceso";

        if (nombre.isBlank() || apellido.isBlank() || email.isBlank() || idRol.isBlank()) {
            ra.addAttribute("error", "Todos los campos son obligatorios.");
            return "redirect:/gestion-usuarios";
        }
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$")) {
            ra.addAttribute("error", "Email inválido.");
            return "redirect:/gestion-usuarios";
        }
        if (!password.isBlank() && password.length() < 6) {
            ra.addAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres.");
            return "redirect:/gestion-usuarios";
        }

        Usuario u = new Usuario();
        u.setIdUsuario(idUsuario);
        u.setNombre(nombre.trim());
        u.setApellido(apellido.trim());
        u.setEmail(email.trim());
        u.setIdRol(Integer.parseInt(idRol));
        u.setActivo("1".equals(activo) || "true".equalsIgnoreCase(activo));
        u.setPassword(password.isBlank() ? "" : password);
        u.setHorarioRestringido("true".equalsIgnoreCase(horarioRestringido));
        u.setHorarioDias(horarioDias);
        u.setHorarioInicio(horarioInicio);
        u.setHorarioFin(horarioFin);

        boolean ok = usuarioService.actualizar(u);
        if (ok) ra.addAttribute("exito", "Usuario actualizado correctamente.");
        else    ra.addAttribute("error", "No se pudo actualizar el usuario.");
        return "redirect:/gestion-usuarios";
    }

    // ── POST: desactivar ──────────────────────────────────────
    /**
     * CORRECCIÓN: ahora llama a cambiarEstado(id, false) que hace
     * UPDATE usuarios SET activo=0 WHERE id_usuario=?
     * Sin cargar ni reescribir todos los campos. No puede fallar.
     */
    @PostMapping(params = "accion=desactivar")
    public String desactivar(@RequestParam Integer id,
                             HttpSession session,
                             RedirectAttributes ra) {
        if (!esAdministrador(session)) return "redirect:/catalogo-telas?error=acceso";

        Usuario sesion = (Usuario) session.getAttribute("usuarioSesion");
        if (sesion != null && sesion.getIdUsuario().equals(id)) {
            ra.addAttribute("error", "No puedes desactivar tu propia cuenta.");
            return "redirect:/gestion-usuarios";
        }

        boolean ok = usuarioService.cambiarEstado(id, false);
        if (ok) ra.addAttribute("exito", "Cuenta desactivada.");
        else    ra.addAttribute("error", "No se pudo cambiar el estado.");
        return "redirect:/gestion-usuarios";
    }

    // ── POST: activar ─────────────────────────────────────────
    /**
     * CORRECCIÓN: igual que desactivar() — solo actualiza el campo 'activo'.
     */
    @PostMapping(params = "accion=activar")
    public String activar(@RequestParam Integer id,
                          HttpSession session,
                          RedirectAttributes ra) {
        if (!esAdministrador(session)) return "redirect:/catalogo-telas?error=acceso";

        boolean ok = usuarioService.cambiarEstado(id, true);
        if (ok) ra.addAttribute("exito", "Cuenta activada.");
        else    ra.addAttribute("error", "No se pudo cambiar el estado.");
        return "redirect:/gestion-usuarios";
    }

    // ── POST: eliminar ────────────────────────────────────────

    @PostMapping(params = "accion=eliminar")
    public String eliminar(@RequestParam Integer id,
                           HttpSession session,
                           RedirectAttributes ra) {
        if (!esAdministrador(session)) return "redirect:/catalogo-telas?error=acceso";

        Usuario sesion = (Usuario) session.getAttribute("usuarioSesion");
        if (sesion != null && sesion.getIdUsuario().equals(id)) {
            ra.addAttribute("error", "No puedes eliminar tu propia cuenta.");
            return "redirect:/gestion-usuarios";
        }

        try {
            // 1. Verificación inicial
            if (usuarioService.tieneActividades(id)) {
                ra.addAttribute("error", "El usuario tiene actividades registradas y no puede ser eliminado.");
                return "redirect:/gestion-usuarios";
            }

            // 2. Intento de borrado
            boolean ok = usuarioService.eliminar(id);
            if (ok) ra.addAttribute("exito", "Usuario eliminado definitivamente.");
            else    ra.addAttribute("error", "No se pudo eliminar el usuario.");

        } catch (Exception e) {
            // 🔥 AQUÍ ATRAPAMOS EL ERROR DE LA BASE DE DATOS
            // Si hay registros en notificaciones, asignaciones o defectos, saltará aquí
            ra.addAttribute("error", "No se puede eliminar: El usuario tiene historial de trabajo en el sistema.");
        }

        return "redirect:/gestion-usuarios";
    }

    // ── Validaciones ──────────────────────────────────────────

    private String validarNuevo(String username, String password, String nombre,
                                String apellido, String email, String idRolStr) {
        if (username == null || username.isBlank())  return "El username es obligatorio.";
        if (username.length() < 4)                   return "El username debe tener al menos 4 caracteres.";
        if (password == null || password.isBlank())  return "La contraseña es obligatoria.";
        if (password.length() < 6)                   return "La contraseña debe tener al menos 6 caracteres.";
        if (nombre == null   || nombre.isBlank())    return "El nombre es obligatorio.";
        if (apellido == null || apellido.isBlank())  return "El apellido es obligatorio.";
        if (email == null    || email.isBlank())     return "El email es obligatorio.";
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$")) return "El email no tiene formato válido.";
        if (idRolStr == null || idRolStr.isBlank())  return "Debes seleccionar un rol.";
        try { Integer.parseInt(idRolStr); } catch (NumberFormatException e) { return "Rol inválido."; }
        return null;
    }
}

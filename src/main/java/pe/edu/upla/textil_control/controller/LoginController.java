package pe.edu.upla.textil_control.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.upla.textil_control.model.Rol;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.repository.RolRepository;
import pe.edu.upla.textil_control.repository.UsuarioRepository;
import pe.edu.upla.textil_control.service.PasswordService;
import pe.edu.upla.textil_control.repository.PermisoRepository;

import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * LoginController — CORREGIDO.
 *
 * Agrega la validación de horario personalizado que existía en LoginServlet.java
 * del proyecto original pero faltaba aquí:
 *  - Si el usuario tiene horario_restringido = true y NO es ADMINISTRADOR,
 *    se verifica que el día actual esté en horario_dias y que la hora esté
 *    entre horario_inicio y horario_fin.
 *  - Si está fuera de horario → no puede ingresar, muestra mensaje.
 *  - ADMINISTRADOR: entra siempre sin restricción.
 */
@Controller
public class LoginController {

    private static final ZoneId ZONA = ZoneId.of("America/Lima");

    // Mapa de días DayOfWeek → abreviatura en español (igual que el original)
    private static final Map<String, String> MAPA_DIAS = Map.of(
            "MONDAY",    "LUN",
            "TUESDAY",   "MAR",
            "WEDNESDAY", "MIE",
            "THURSDAY",  "JUE",
            "FRIDAY",    "VIE",
            "SATURDAY",  "SAB",
            "SUNDAY",    "DOM"
    );

    /** Redirección por rol tras login exitoso (igual que el original) */
    private static final Map<String, String> RUTA_POR_ROL = new HashMap<>();
    static {
        RUTA_POR_ROL.put("ADMINISTRADOR",   "/gestion-usuarios");
        RUTA_POR_ROL.put("JEFE_ALMACEN",    "/catalogo-telas");
        RUTA_POR_ROL.put("JEFE_PRODUCCION", "/catalogo-telas");
        RUTA_POR_ROL.put("TIZADOR",         "/catalogo-telas");
        RUTA_POR_ROL.put("SUPERVISOR",      "/catalogo-telas");
        RUTA_POR_ROL.put("MAQUINISTA",      "/ordenes-trabajo");
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private RolRepository rolRepository;

    // ── GET /login ────────────────────────────────────────────

    @GetMapping("/login")
    public String mostrarLogin(HttpSession session, Model model) {
        if (session.getAttribute("usuarioSesion") != null) {
            String nombreRol = (String) session.getAttribute("nombreRol");
            if (nombreRol == null) nombreRol = "";
            return "redirect:" + RUTA_POR_ROL.getOrDefault(nombreRol.toUpperCase(), "/catalogo-telas");
        }

        // Mostrar mensaje de error de horario si viene desde AuthInterceptor
        // (cuando la sesión expira por estar fuera de horario)
        if ("1".equals(session.getAttribute("errorHorario"))) {
            model.addAttribute("errorHorarioLogin", "Sesión cerrada: estás fuera de tu horario laboral.");
            session.removeAttribute("errorHorario");
        }
        return "login";
    }

    // ── POST /login ───────────────────────────────────────────

    // ── POST /login ───────────────────────────────────────────

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {

        // Buscar usuario activo
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameAndActivoTrue(username);

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Usuario o contraseña incorrectos.");
            return "login";
        }

        Usuario usuario = usuarioOpt.get();
        LocalDateTime ahora = LocalDateTime.now(ZONA);

        // 1. VERIFICAR SI LA CUENTA ESTÁ BLOQUEADA
        if (usuario.getBloqueadoHasta() != null) {
            if (ahora.isBefore(usuario.getBloqueadoHasta())) {
                // Sigue bloqueado: calculamos cuánto tiempo falta
                long minutosRestantes = java.time.Duration.between(ahora, usuario.getBloqueadoHasta()).toMinutes();
                model.addAttribute("error", "Cuenta bloqueada por múltiples intentos fallidos. Intenta de nuevo en " + (minutosRestantes + 1) + " minutos.");
                return "login";
            } else {
                // El tiempo de castigo ya expiró, reseteamos la cuenta silenciosamente
                usuario.setIntentosFallidos(0);
                usuario.setBloqueadoHasta(null);
                usuarioRepository.resetearIntentos(usuario.getIdUsuario());
            }
        }

        // 2. VERIFICAR CONTRASEÑA
        if (!passwordService.verificar(password, usuario.getPassword())) {
            int intentosActuales = (usuario.getIntentosFallidos() == null) ? 0 : usuario.getIntentosFallidos();
            int nuevosIntentos = intentosActuales + 1;
            int maxIntentos = 5;

            if (nuevosIntentos >= maxIntentos) {
                // Castigo: Bloquear cuenta por 1 hora
                LocalDateTime tiempoBloqueo = ahora.plusHours(1);
                usuarioRepository.actualizarIntentosYBloqueo(usuario.getIdUsuario(), nuevosIntentos, tiempoBloqueo);
                model.addAttribute("error", "Has superado los 5 intentos fallidos. Tu cuenta ha sido bloqueada por 1 hora por seguridad.");
            } else {
                // Registrar intento fallido y advertir al usuario
                usuarioRepository.actualizarIntentosYBloqueo(usuario.getIdUsuario(), nuevosIntentos, null);
                int restantes = maxIntentos - nuevosIntentos;
                model.addAttribute("error", "Contraseña incorrecta. Te quedan " + restantes + " intentos antes de bloquear tu cuenta.");
            }
            return "login";
        }

        // 3. LOGIN EXITOSO -> RESETEAR INTENTOS
        // Si logró entrar y tenía intentos fallidos previos, limpiamos su historial
        if (usuario.getIntentosFallidos() != null && usuario.getIntentosFallidos() > 0) {
            usuarioRepository.resetearIntentos(usuario.getIdUsuario());
        }

        // Cargar nombre del rol
        String nombreRol = rolRepository.findById(usuario.getIdRol())
                .map(Rol::getNombreRol)
                .orElse("");
        usuario.setNombreRol(nombreRol);

        // ── Validación de horario personalizado ───────────────
        if (Boolean.TRUE.equals(usuario.getHorarioRestringido())
                && !"ADMINISTRADOR".equalsIgnoreCase(nombreRol)) {

            ZonedDateTime ahoraZoned  = ZonedDateTime.now(ZONA);
            DayOfWeek     diaActual = ahoraZoned.getDayOfWeek();

            // -- Validar día --
            String diasPermitidos = (usuario.getHorarioDias() != null)
                    ? usuario.getHorarioDias().toUpperCase() : "";
            boolean diaOk = false;
            if (!diasPermitidos.isBlank()) {
                String diaCorto = MAPA_DIAS.getOrDefault(diaActual.name(), "");
                diaOk = diasPermitidos.contains(diaCorto);
            }

            if (!diaOk) {
                String msg = "Tu cuenta no está habilitada los "
                        + (diasPermitidos.isBlank() ? "días configurados." : diasPermitidos + ".");
                model.addAttribute("errorHorarioLogin", msg);
                return "login";
            }

            // -- Validar hora --
            LocalTime horaInicio = null;
            LocalTime horaFin    = null;
            try {
                if (usuario.getHorarioInicio() != null && !usuario.getHorarioInicio().isBlank())
                    horaInicio = LocalTime.parse(usuario.getHorarioInicio());
                if (usuario.getHorarioFin() != null && !usuario.getHorarioFin().isBlank())
                    horaFin = LocalTime.parse(usuario.getHorarioFin());
            } catch (Exception ignored) {}

            if (horaInicio != null && horaFin != null) {
                LocalTime ahoraHora = ahoraZoned.toLocalTime();
                if (ahoraHora.isBefore(horaInicio) || ahoraHora.isAfter(horaFin)) {
                    String msg = String.format(
                            "⛔ Acceso fuera de horario. Tu horario permitido es %s a %s los días %s.",
                            horaInicio, horaFin,
                            diasPermitidos.isBlank() ? "establecidos" : diasPermitidos);
                    model.addAttribute("errorHorarioLogin", msg);
                    return "login";
                }
            }
        }
        // ── Fin validación horario ─────────────────────────────

        // Guardar sesión
        Set<String> permisos = permisoRepository.findCodigosByIdRol(usuario.getIdRol());
        session.setAttribute("permisosUsuario", permisos);
        session.setAttribute("nombreRol",       nombreRol);
        session.setAttribute("usuarioSesion",   usuario);
        session.setMaxInactiveInterval(60 * 30); // 30 minutos

        String destino = RUTA_POR_ROL.getOrDefault(nombreRol.toUpperCase(), "/catalogo-telas");
        return "redirect:" + destino;
    }

    // ── GET /logout ───────────────────────────────────────────

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

package pe.edu.upla.textil_control.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pe.edu.upla.textil_control.model.Usuario;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Lima");

    private static final Map<String, String> RUTAS_PERMISOS = Map.ofEntries(
            Map.entry("/gestion-usuarios", "SEG_USUARIOS_VER"),
            Map.entry("/inventario",        "ALM_TELA_VER"),
            Map.entry("/registro-tela",     "ALM_TELA_REGISTRAR"),
            Map.entry("/catalogo-telas",    "CAT_TELAS_VER"),
            Map.entry("/catalogo-modelos",  "CAT_MODELOS_VER"),
            Map.entry("/ordenes-trabajo",   "PROD_OT_VER"),
            Map.entry("/tiempos-reposo",    "PROD_REPOSO_VER"),
            Map.entry("/defectos",          "CAL_DEFECTOS_VER"),
            Map.entry("/mermas",            "PROD_MERMA_VER"),
            Map.entry("/cargas-trabajo",    "PROD_CARGAS_VER"),
            Map.entry("/maquinistas",       "PROD_MAQUINISTAS_VER"),
            Map.entry("/despacho",          "DES_CONCIL_VER"),
            Map.entry("/reportes",          "RPT_MERMAS_CALIDAD"),
            Map.entry("/backup",            "RPT_MERMAS_CALIDAD")
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioSesion") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        ZonedDateTime ahoraLima = ZonedDateTime.now(ZONA_HORARIA);

        // ── CONTROL DE HORARIO DINÁMICO ANTIBALAS ──
        if (Boolean.TRUE.equals(usuario.getHorarioRestringido())) {

            int diaSemana = ahoraLima.getDayOfWeek().getValue();
            LocalTime horaActual = ahoraLima.toLocalTime();

            boolean diaValido = isDiaValido(usuario.getHorarioDias(), diaSemana);
            LocalTime horaInicio = parseHoraSegura(usuario.getHorarioInicio(), LocalTime.of(7, 0));
            LocalTime horaFin = parseHoraSegura(usuario.getHorarioFin(), LocalTime.of(17, 0));

            if (!diaValido || horaActual.isBefore(horaInicio) || horaActual.isAfter(horaFin)) {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login?errorHorario=1");
                return false;
            }
        }

        // ── CONTROL DE PERMISOS POR RUTA ──
        String uri = request.getRequestURI();

        for (Map.Entry<String, String> entry : RUTAS_PERMISOS.entrySet()) {
            if (uri.startsWith(request.getContextPath() + entry.getKey()) || uri.startsWith(entry.getKey())) {

                @SuppressWarnings("unchecked")
                Set<String> permisosUsuario = (Set<String>) session.getAttribute("permisosUsuario");

                if ((uri.contains("/cargas-trabajo") || uri.contains("/defectos"))
                        && usuario.getIdRol() != null && usuario.getIdRol() == 6) {
                    return true;
                }

                if (permisosUsuario == null || !permisosUsuario.contains(entry.getValue())) {
                    response.sendRedirect(request.getContextPath() + "/dashboard?error=acceso");
                    return false;
                }
                return true;
            }
        }

        return true;
    }

    // ── MÉTODOS DE FILTRADO ANTIBALAS ──

    private boolean isDiaValido(String diasStr, int dayOfWeek) {
        if (diasStr == null || diasStr.isBlank()) return false;
        String d = diasStr.toLowerCase();
        switch (dayOfWeek) {
            case 1: return d.contains("1") || d.contains("lun");
            case 2: return d.contains("2") || d.contains("mar");
            case 3: return d.contains("3") || d.contains("mie") || d.contains("mié");
            case 4: return d.contains("4") || d.contains("jue");
            case 5: return d.contains("5") || d.contains("vie");
            case 6: return d.contains("6") || d.contains("sab") || d.contains("sáb");
            case 7: return d.contains("7") || d.contains("0") || d.contains("dom");
            default: return false;
        }
    }

    private LocalTime parseHoraSegura(String horaStr, LocalTime defaultTime) {
        if (horaStr == null || horaStr.trim().isBlank()) return defaultTime;
        try {
            String h = horaStr.trim();
            if (h.length() >= 4 && h.charAt(1) == ':') h = "0" + h;
            if (h.length() > 5) h = h.substring(0, 5);
            return LocalTime.parse(h);
        } catch (Exception e) {
            return defaultTime;
        }
    }
}
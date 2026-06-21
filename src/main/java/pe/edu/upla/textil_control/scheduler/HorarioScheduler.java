package pe.edu.upla.textil_control.scheduler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@EnableScheduling
public class HorarioScheduler {

    private static final Logger log = LoggerFactory.getLogger(HorarioScheduler.class);

    @Autowired
    private JdbcTemplate jdbc;

    @PostConstruct
    public void iniciar() {
        log.info("[HorarioScheduler] Iniciando control de horarios personalizados antibalas...");
        ejecutarRevisionHorarios();
    }

    @Scheduled(cron = "0 * * * * *", zone = "America/Lima")
    public void ejecutarRevisionHorarios() {
        ZonedDateTime ahora = ZonedDateTime.now(ZoneId.of("America/Lima"));
        int diaSemana = ahora.getDayOfWeek().getValue(); // Lunes=1, Domingo=7
        LocalTime horaActual = ahora.toLocalTime();

        String sqlSelect = "SELECT id_usuario, horario_dias, horario_inicio, horario_fin, activo FROM usuarios WHERE horario_restringido = 1";

        jdbc.query(sqlSelect, rs -> {
            int idUsuario = rs.getInt("id_usuario");
            boolean activoActual = rs.getBoolean("activo");
            boolean deberiaEstarActivo = false;

            try {
                String diasStr = rs.getString("horario_dias");

                if (isDiaValido(diasStr, diaSemana)) {
                    LocalTime inicio = parseHoraSegura(rs.getString("horario_inicio"), LocalTime.of(7, 0));
                    LocalTime fin = parseHoraSegura(rs.getString("horario_fin"), LocalTime.of(17, 0));

                    // 🔥 LÓGICA MEJORADA: Soporta turnos normales y nocturnos de amanecida
                    if (inicio.isBefore(fin)) {
                        // Turno normal (Ej: 07:00 a 17:00)
                        if (!horaActual.isBefore(inicio) && !horaActual.isAfter(fin)) deberiaEstarActivo = true;
                    } else {
                        // Turno nocturno (Ej: 22:00 a 06:00)
                        if (!horaActual.isBefore(inicio) || !horaActual.isAfter(fin)) deberiaEstarActivo = true;
                    }
                }
            } catch (Exception e) {
                log.error("Error evaluando horario para usuario {}: {}", idUsuario, e.getMessage());
            }

            // Muestra en consola lo que Spring Boot intenta hacer
            log.info("🔍 Revisando Usuario {}: Debe estar activo? {} | Estado actual BD: {}", idUsuario, deberiaEstarActivo, activoActual);

            if (activoActual != deberiaEstarActivo) {
                jdbc.update("UPDATE usuarios SET activo = ? WHERE id_usuario = ?", deberiaEstarActivo ? 1 : 0, idUsuario);
                log.info("✅ Usuario ID {} fue actualizado por Spring Boot a: {}", idUsuario, deberiaEstarActivo);
            }
        });
    }

    @PreDestroy
    public void detener() {
        log.info("[HorarioScheduler] Servicio detenido.");
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
            case 7: return d.contains("7") || d.contains("0") || d.contains("dom"); // Soporta domingos como 7 o 0
            default: return false;
        }
    }

    private LocalTime parseHoraSegura(String horaStr, LocalTime defaultTime) {
        if (horaStr == null || horaStr.trim().isBlank()) return defaultTime;
        try {
            String h = horaStr.trim();
            if (h.length() >= 4 && h.charAt(1) == ':') h = "0" + h; // Arregla "7:00" a "07:00"
            if (h.length() > 5) h = h.substring(0, 5); // Arregla "07:00:00" a "07:00"
            return LocalTime.parse(h);
        } catch (Exception e) {
            return defaultTime;
        }
    }
}
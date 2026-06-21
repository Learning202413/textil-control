package pe.edu.upla.textil_control.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.service.NotificacionService;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    @Autowired private NotificacionService service;

    @GetMapping
    // 🔥 CAMBIO AQUÍ: Agregamos required = false y un valor por defecto vacío
    public ResponseEntity<?> obtenerNotificaciones(@RequestParam(required = false, defaultValue = "") String accion, @RequestParam(required = false, defaultValue = "20") int limite, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSesion");
        if (usuario == null) return ResponseEntity.status(401).build();

        if ("listarNoLeidas".equals(accion)) {
            return ResponseEntity.ok(service.listarNoLeidasPorRol(usuario.getNombreRol()));
        } else {
            return ResponseEntity.ok(service.listarTodasPorRol(usuario.getNombreRol(), limite));
        }
    }

    @GetMapping("/marcarLeida")
    public ResponseEntity<?> marcarLeida(@RequestParam Integer id, HttpSession session) {
        if (session.getAttribute("usuarioSesion") == null) return ResponseEntity.status(401).build();
        service.marcarComoLeida(id);
        return ResponseEntity.ok().build();
    }
}
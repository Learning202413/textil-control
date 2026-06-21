package pe.edu.upla.textil_control.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.repository.UsuarioRepository;
import pe.edu.upla.textil_control.service.PasswordService;

import java.util.Optional;

// Usamos RestController porque solo queremos devolver texto/html, no una vista de Thymeleaf
@RestController
public class SetupController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordService passwordService;

    @GetMapping("/setup")
    public String inicializarContrasenas() {
        String[] usuariosDemo = {"admin", "almacen1", "jefe_prod", "tizador1", "supervisor1", "maquinista1"};
        int actualizados = 0;
        StringBuilder log = new StringBuilder("<h3>⚙️ Setup - Actualización de contraseñas</h3><ul>");

        for (String username : usuariosDemo) {
            // Buscamos al usuario en la BD (asegúrate de tener este método en tu UsuarioRepository)
            Optional<Usuario> userOpt = usuarioRepository.findByUsernameAndActivoTrue(username);

            if (userOpt.isPresent()) {
                Usuario u = userOpt.get();
                // Hasheamos el mismo username como contraseña
                u.setPassword(passwordService.encriptar(username));
                usuarioRepository.save(u); // Hace el UPDATE automático

                log.append("<li>✅ Usuario <b>").append(username).append("</b> actualizado.</li>");
                actualizados++;
            } else {
                log.append("<li>⚠️ Usuario <b>").append(username).append("</b> no encontrado.</li>");
            }
        }

        log.append("</ul><br><b>Total actualizados: ").append(actualizados).append("</b>");
        log.append("<br><br><a href='/login'>Ir al Login</a>");

        return log.toString();
    }
}
package pe.edu.upla.textil_control.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.service.BackupService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/backup")
public class BackupController {

    @Autowired private BackupService backupService;

    private boolean tienePermiso(HttpSession session, String permiso) {
        Set<String> permisos = (Set<String>) session.getAttribute("permisosUsuario");
        return permisos != null && permisos.contains(permiso);
    }

    // 1. Vista Principal
    @GetMapping
    public String index(Model model, @RequestParam(required = false) String jobId, HttpSession session) {
        if (!tienePermiso(session, "RPT_MERMAS_CALIDAD")) return "redirect:/dashboard?error=sinPermiso";
        model.addAttribute("jobId", jobId);
        return "backup";
    }

    // 2. Fragmento AJAX (Reemplaza a tabla_backups.jsp)
    @GetMapping("/listar")
    public String listarAjax(Model model) {
        model.addAttribute("historial", backupService.listarTodos());
        return "backup :: tabla_backups"; // Retorna solo el bloque de la tabla de backup.html
    }

    // 3. Generar Asíncrono
    @PostMapping("/generar")
    public String generar(HttpSession session) {
        Usuario u = (Usuario) session.getAttribute("usuarioSesion");
        if (u == null) return "redirect:/login";
        String jobId = backupService.generarBackupAsincrono(u.getIdUsuario());
        return "redirect:/backup?jobId=" + jobId;
    }

    // 4. Polling JSON (Estado del Job)
    @GetMapping(value = "/estado", produces = "application/json")
    @ResponseBody
    public Map<String, Object> estado(@RequestParam String jobId) {
        Map<String, Object> res = new HashMap<>();
        BackupService.BackupJob job = backupService.getEstadoJob(jobId);
        if (job != null) {
            res.put("estado", job.estado);
            res.put("mensaje", job.mensaje);
            res.put("archivo", job.nombreArchivo);
        } else {
            res.put("estado", "NOT_FOUND");
        }
        return res;
    }

    // 5. Eliminar (AJAX JSON)
    @PostMapping(value = "/eliminar", produces = "application/json")
    @ResponseBody
    public Map<String, Object> eliminar(@RequestParam Integer id, @RequestParam String file) {
        Map<String, Object> res = new HashMap<>();
        try {
            backupService.eliminarBackup(id, file);
            res.put("success", true);
        } catch (Exception e) {
            res.put("success", false);
            res.put("error", e.getMessage());
        }
        return res;
    }

    // 6. Descargar archivo
    @GetMapping("/descargar")
    public ResponseEntity<Resource> descargar(@RequestParam String file) {
        try {
            Path path = backupService.obtenerRutaArchivo(file);
            if (!Files.exists(path)) return ResponseEntity.notFound().build();

            Resource resource = new FileSystemResource(path.toFile());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    // 7. Restaurar Base de Datos (AJAX JSON)
    @PostMapping(value = "/restaurar", produces = "application/json")
    @ResponseBody
    public Map<String, Object> restaurar(@RequestParam String file, HttpSession session) {
        Map<String, Object> res = new HashMap<>();

        // Medida de seguridad: Solo un Administrador debería poder restaurar
        if (!tienePermiso(session, "RPT_MERMAS_CALIDAD")) {
            res.put("success", false);
            res.put("error", "No tienes permisos para restaurar la base de datos.");
            return res;
        }

        try {
            backupService.restaurarBackup(file);
            res.put("success", true);
        } catch (Exception e) {
            res.put("success", false);
            res.put("error", e.getMessage());
        }
        return res;
    }
}
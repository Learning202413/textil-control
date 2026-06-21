package pe.edu.upla.textil_control.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.upla.textil_control.model.Especialidad;
import pe.edu.upla.textil_control.model.MaquinistaDTO;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.service.EspecialidadService;
import pe.edu.upla.textil_control.service.MaquinistaService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/maquinistas")
public class MaquinistaController {

    @Autowired private MaquinistaService maquinistaService;
    @Autowired private EspecialidadService especialidadService;
    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping
    public String listar(Model model) throws JsonProcessingException {
        List<MaquinistaDTO> maquinistas = maquinistaService.listarMaquinistas();
        List<Especialidad> especialidades = especialidadService.listarTodos();

        // Preparamos datos en JSON seguro para los modales de JS
        Map<Integer, Object> maqDataMap = new HashMap<>();
        for (MaquinistaDTO m : maquinistas) {
            Map<String, Object> data = new HashMap<>();
            data.put("username", m.getUsuario().getUsername());
            data.put("nombre", m.getUsuario().getNombre());
            data.put("apellido", m.getUsuario().getApellido());
            data.put("email", m.getUsuario().getEmail() != null ? m.getUsuario().getEmail() : "");
            data.put("especialidades", m.getEspecialidades().stream().map(Especialidad::getIdEspecialidad).collect(Collectors.toList()));
            maqDataMap.put(m.getUsuario().getIdUsuario(), data);
        }

        model.addAttribute("maquinistas", maquinistas);
        model.addAttribute("especialidadesDisponiblesJson", mapper.writeValueAsString(especialidades));
        model.addAttribute("maquinistasJson", mapper.writeValueAsString(maqDataMap));

        return "maquinistas"; // Renderiza maquinistas.html
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario,
                          @RequestParam(required = false) String accion,
                          @RequestParam(value = "especialidades", required = false) List<Integer> especialidades,
                          RedirectAttributes redirectAttrs) {

        try {
            if ("actualizar".equals(accion)) {
                maquinistaService.actualizarMaquinista(usuario, especialidades);
                redirectAttrs.addAttribute("exito", "Maquinista actualizado");
            } else {
                maquinistaService.guardarMaquinista(usuario, especialidades);
                redirectAttrs.addAttribute("exito", "Maquinista creado");
            }
        } catch (Exception e) {
            redirectAttrs.addAttribute("error", "Error en la operación: verifique los datos");
        }
        return "redirect:/maquinistas";
    }

    @PostMapping("/estado")
    public String cambiarEstado(@RequestParam Integer id, @RequestParam String accion, RedirectAttributes redirectAttrs) {
        maquinistaService.cambiarEstado(id, "activar".equals(accion));
        redirectAttrs.addAttribute("exito", "Estado actualizado");
        return "redirect:/maquinistas";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer id, RedirectAttributes redirectAttrs) {
        try {
            if (maquinistaService.eliminarMaquinista(id)) {
                redirectAttrs.addAttribute("exito", "Maquinista eliminado");
            } else {
                redirectAttrs.addAttribute("error", "El maquinista tiene historial de trabajo, no se puede eliminar");
            }
        } catch (Exception e) {
            // 🔥 AQUÍ ATRAPAMOS EL ERROR DE LA BASE DE DATOS
            redirectAttrs.addAttribute("error", "No se puede eliminar: El maquinista ya tiene tareas o defectos asignados en el sistema.");
        }
        return "redirect:/maquinistas";
    }

    // --- REEMPLAZO DE TU ANTIGUO EspecialidadServlet (AJAX) ---
    @PostMapping("/ajax/especialidad")
    @ResponseBody
    public ResponseEntity<?> guardarEspecialidadAjax(@RequestParam String nombre, @RequestParam(required = false) String descripcion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre es obligatorio"));
        }
        Especialidad nueva = new Especialidad();
        nueva.setNombre(nombre.trim());
        nueva.setDescripcion(descripcion != null ? descripcion.trim() : "");
        especialidadService.guardar(nueva); // Service inyecta ID automático

        return ResponseEntity.ok(nueva);
    }
}
package pe.edu.upla.textil_control.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.upla.textil_control.model.Especialidad;
import pe.edu.upla.textil_control.service.EspecialidadService;

@Controller
@RequestMapping("/gestion-especialidades")
public class GestionEspecialidadesController {

    @Autowired
    private EspecialidadService service;

    // --- GET: Listar y preparar edición ---
    @GetMapping
    public String listar(Model model,
                         @RequestParam(required = false) String accion,
                         @RequestParam(required = false) Integer id) {

        if ("editar".equals(accion) && id != null) {
            model.addAttribute("especialidadEditar", service.buscarPorId(id));
        }

        model.addAttribute("especialidades", service.listarTodos());
        return "gestion_especialidades"; // Retorna la vista gestion_especialidades.html
    }

    // --- POST: Guardar o Actualizar ---
    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Integer id_especialidad,
                          @RequestParam String nombre,
                          @RequestParam(required = false) String descripcion,
                          RedirectAttributes redirectAttrs) {

        Especialidad esp = new Especialidad();
        esp.setIdEspecialidad(id_especialidad);
        esp.setNombre(nombre);
        esp.setDescripcion(descripcion);

        try {
            service.guardar(esp);
            redirectAttrs.addAttribute("exito", "Especialidad guardada correctamente");
        } catch (Exception e) {
            redirectAttrs.addAttribute("error", "Error al guardar la especialidad");
        }

        return "redirect:/gestion-especialidades";
    }

    // --- POST: Eliminar ---
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer id_especialidad, RedirectAttributes redirectAttrs) {
        try {
            service.eliminar(id_especialidad);
            redirectAttrs.addAttribute("exito", "Especialidad eliminada");
        } catch (Exception e) {
            redirectAttrs.addAttribute("error", "No se puede eliminar la especialidad (Puede estar asignada a un usuario).");
        }
        return "redirect:/gestion-especialidades";
    }
}
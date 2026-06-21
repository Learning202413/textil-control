package pe.edu.upla.textil_control.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.upla.textil_control.model.Permiso;
import pe.edu.upla.textil_control.service.PermisoService;

import java.util.List;

@Controller
@RequestMapping("/roles")
public class RolController {

    @Autowired
    private PermisoService permisoService;

    // --- MOSTRAR LA MATRIZ (GET) ---
    @GetMapping("/permisos/{idRol}")
    public String mostrarMatrizPermisos(@PathVariable Integer idRol, Model model) {

        List<Permiso> listaPermisos = permisoService.listarPermisosParaMatriz(idRol);

        model.addAttribute("listaPermisos", listaPermisos);
        model.addAttribute("idRolActual", idRol); // Para saber a quién le estamos editando

        return "gestion_permisos"; // Renderiza templates/gestion_permisos.html
    }

    // --- GUARDAR LOS CHECKBOXES (POST) ---
    @PostMapping("/permisos/{idRol}")
    public String guardarPermisos(@PathVariable Integer idRol,
                                  @RequestParam(value = "permisosMarcados", required = false) List<Integer> permisosMarcados) {

        // Llamamos al servicio para que haga el Delete + Insert
        permisoService.actualizarPermisosDeRol(idRol, permisosMarcados);

        // Redirigimos a la misma página mostrando un mensaje de éxito
        return "redirect:/roles/permisos/" + idRol + "?exito=true";
    }
}
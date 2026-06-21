package pe.edu.upla.textil_control.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.upla.textil_control.model.CatalogoTela;
import pe.edu.upla.textil_control.service.CatalogoTelaService;

@Controller
@RequestMapping("/catalogo-telas")
public class CatalogoTelaController {

    @Autowired
    private CatalogoTelaService catalogoTelaService;

    // --- GET: Mostrar Lista y Manejar Edición ---
    @GetMapping
    public String listar(Model model,
                         @RequestParam(required = false) String accion,
                         @RequestParam(required = false) Integer id) {

        if ("editar".equals(accion) && id != null) {
            model.addAttribute("telaEditar", catalogoTelaService.buscarPorId(id));
        }

        model.addAttribute("telas", catalogoTelaService.listarTodos());
        return "catalogo_telas"; // Retorna catalogo_telas.html
    }

    // --- POST: Guardar o Actualizar ---
    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(required = false) Integer id_catalogo,
            @RequestParam String nombre,
            @RequestParam String composicion,
            @RequestParam(required = false) String proveedor,
            @RequestParam(required = false, defaultValue = "false") boolean reposo,
            @RequestParam(required = false) Integer tiempo_reposo,
            @RequestParam(required = false) String accion,
            RedirectAttributes redirectAttrs) {

        CatalogoTela tela = new CatalogoTela();
        tela.setIdCatalogo(id_catalogo);
        tela.setNombre(nombre);
        tela.setComposicion(composicion);
        tela.setProveedorBase(proveedor);
        tela.setRequiereReposo(reposo);
        tela.setTiempoReposo(tiempo_reposo != null ? tiempo_reposo : 0);

        if ("actualizar".equals(accion)) {
            catalogoTelaService.actualizar(tela);
            redirectAttrs.addAttribute("exito", "Material actualizado y telas sincronizadas");
        } else {
            catalogoTelaService.guardar(tela);
            redirectAttrs.addAttribute("exito", "Material guardado correctamente");
        }
        return "redirect:/catalogo-telas";
    }

    // --- POST: Eliminar ---
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer id_catalogo, RedirectAttributes redirectAttrs) {
        if (catalogoTelaService.eliminar(id_catalogo)) {
            redirectAttrs.addAttribute("exito", "Material eliminado correctamente");
        } else {
            redirectAttrs.addAttribute("error", "Tela en uso en el almacén, no se puede eliminar");
        }
        return "redirect:/catalogo-telas";
    }
}
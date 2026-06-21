package pe.edu.upla.textil_control.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.upla.textil_control.model.FaseProduccion;
import pe.edu.upla.textil_control.model.ModeloPrenda;
import pe.edu.upla.textil_control.model.PiezaModelo;
import pe.edu.upla.textil_control.service.ModeloPrendaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/catalogo-modelos")
public class CatalogoModelosController {

    @Autowired private ModeloPrendaService service;
    private final ObjectMapper mapper = new ObjectMapper();


    @GetMapping
    public String listar(Model model, @RequestParam(required = false) String accion, @RequestParam(required = false) Integer id) throws JsonProcessingException {

        List<FaseProduccion> fases = service.listarFases();

        // Filtramos las fases que se mostrarán en los checkboxes (excluyendo ENSAMBLAJE ID: 6)
        List<FaseProduccion> fasesUi = fases.stream()
                .filter(f -> f.getIdFase() != 6 && !"ENSAMBLAJE".equalsIgnoreCase(f.getNombre()))
                .collect(Collectors.toList());

        if ("editar".equals(accion) && id != null) {
            ModeloPrenda m = service.buscarConPiezas(id);
            model.addAttribute("modeloEditarJson", mapper.writeValueAsString(m));
        }

        model.addAttribute("modelos", service.listarResumen());
        model.addAttribute("fasesJson", mapper.writeValueAsString(fasesUi));

        return "catalogo_modelos"; // catalogo_modelos.html
    }

    @GetMapping("/piezas/{id}")
    @ResponseBody // Retorna JSON directo
    public ResponseEntity<?> verPiezas(@PathVariable Integer id) {
        ModeloPrenda m = service.buscarConPiezas(id);
        if (m == null) return ResponseEntity.ok(new ArrayList<>());

        Map<Integer, String> mapaFases = service.listarFases().stream()
                .collect(Collectors.toMap(FaseProduccion::getIdFase, FaseProduccion::getNombre));

        // Formatear respuesta igual a la antigua
        List<Map<String, Object>> respuesta = new ArrayList<>();
        for (PiezaModelo p : m.getPiezas()) {
            List<Map<String, Object>> fasesInfo = p.getIdFasesAsignadas().stream()
                    .map(idF -> Map.<String, Object>of("id", idF, "nombre", mapaFases.getOrDefault(idF, "Desconocida")))
                    .collect(Collectors.toList());

            respuesta.add(Map.of(
                    "nombre", p.getNombrePieza(),
                    "cantidad", p.getCantidad(),
                    "fases", fasesInfo
            ));
        }
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/guardar")
    public String guardar(HttpServletRequest req, RedirectAttributes redirectAttrs) {
        String accion = req.getParameter("accion");
        ModeloPrenda m = new ModeloPrenda();

        String idStr = req.getParameter("id_modelo");
        if (idStr != null && !idStr.isEmpty()) m.setIdModelo(Integer.parseInt(idStr));
        m.setNombre(req.getParameter("nombre"));
        m.setTemporada(req.getParameter("temporada"));

        String[] nombresPiezas = req.getParameterValues("nombrePieza[]");
        String[] cantidadesPiezas = req.getParameterValues("cantidadPieza[]");

        if (nombresPiezas != null) {
            for (int i = 0; i < nombresPiezas.length; i++) {
                if (!nombresPiezas[i].trim().isEmpty()) {
                    PiezaModelo p = new PiezaModelo();
                    p.setNombrePieza(nombresPiezas[i].trim());
                    p.setCantidad(Integer.parseInt(cantidadesPiezas[i]));

                    String[] fasesMarcadas = req.getParameterValues("fasesPieza_" + i);
                    if (fasesMarcadas != null) {
                        List<Integer> ids = new ArrayList<>();
                        for (String fm : fasesMarcadas) ids.add(Integer.parseInt(fm));
                        p.setIdFasesAsignadas(ids);
                    }
                    m.getPiezas().add(p);
                }
            }
        }

        if ("actualizar".equals(accion)) {
            if (service.estaEnUso(m.getIdModelo())) {
                redirectAttrs.addAttribute("error", "No se puede editar: El modelo ya tiene órdenes de trabajo.");
            } else {
                service.actualizarTransaccional(m);
                redirectAttrs.addAttribute("exito", "Modelo actualizado");
            }
        } else {
            service.guardarTransaccional(m);
            redirectAttrs.addAttribute("exito", "Modelo guardado");
        }
        return "redirect:/catalogo-modelos";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer id_modelo, RedirectAttributes redirectAttrs) {
        if (service.estaEnUso(id_modelo)) {
            redirectAttrs.addAttribute("error", "No se puede eliminar: En producción.");
        } else {
            service.eliminar(id_modelo);
            redirectAttrs.addAttribute("exito", "Eliminado correctamente");
        }
        return "redirect:/catalogo-modelos";
    }

    @PostMapping("/fase")
    @ResponseBody
    public ResponseEntity<?> agregarFase(@RequestParam String nombre, @RequestParam Integer orden, @RequestParam String descripcion) {
        try {
            service.agregarFase(nombre, orden, descripcion);
            List<FaseProduccion> fasesUi = service.listarFases().stream()
                    .filter(f -> f.getIdFase() != 6 && !"ENSAMBLAJE".equalsIgnoreCase(f.getNombre()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(fasesUi);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
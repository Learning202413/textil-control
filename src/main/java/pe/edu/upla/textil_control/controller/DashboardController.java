package pe.edu.upla.textil_control.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.edu.upla.textil_control.service.DashboardService;

import java.util.Map;

/**
 * Controller: Dashboard
 * Migración del DashboardServlet.java original (proyecto Java EE) a Spring Boot.
 *
 * Todos los datos provienen EN VIVO de la base de datos real (vía DashboardService
 * + repositorios JPA), las mismas tablas que ya usan los demás módulos del sistema.
 *
 * GET /dashboard                -> renderiza dashboard.html (HTML)
 * GET /dashboard?accion=json    -> devuelve el JSON con KPIs, gráficos, alertas, etc.
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public String dashboard(@RequestParam(value = "accion", required = false) String accion,
                            Model model,
                            HttpSession session) {

        model.addAttribute("otFilas", dashboardService.calcularProgresoOTs());
        return "dashboard";
    }

    @GetMapping(params = "accion=json", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Map<String, Object> dashboardJson() {
        return dashboardService.construirDatosJson();
    }
}
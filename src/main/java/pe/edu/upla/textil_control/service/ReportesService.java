package pe.edu.upla.textil_control.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Service: Reportes y Analíticas
 *
 * Migración del ReportesServlet.java + ReporteDAO.java (Java EE) a Spring Boot.
 *
 * LÓGICA DE NEGOCIO PRESERVADA ÍNTEGRAMENTE:
 *  - Filtrado por rol: Admin/Supervisor/Gerente/Jefe → ven todo
 *  - Tizador → solo sus propios datos de merma y fallas
 *  - Maquinista → solo su rendimiento personal
 *  - Calidad/Almacén → acceso a fallas e inventario global
 *  - Cálculos derivados en Service: % merma, tiempo formateado, tasa responsabilidad, etc.
 */
@Service
public class ReportesService {

    @Autowired
    private ReportesRepository reportesRepo;

    // ── MÉTODOS PÚBLICOS POR TIPO DE REPORTE ────────────────────────────────

    /**
     * Eficiencia global de OTs (estados) — solo roles supervisión+
     */
    public List<Map<String, Object>> obtenerEficienciaGlobal() {
        List<Map<String, Object>> resultado = new ArrayList<>();
        try {
            for (ReporteEficienciaDTO dto : reportesRepo.obtenerEficienciaGlobal()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("estado", dto.getEstado());
                fila.put("total", dto.getTotal());
                resultado.add(fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    /**
     * Merma por OT — filtrada por rol (PRESERVADO del ReportesServlet)
     */
    public List<Map<String, Object>> obtenerMermaPorOT(Usuario usuario) {
        boolean verTodo = esRolGlobal(usuario.getNombreRol());
        List<ReporteMermaDTO> raw = verTodo
                ? reportesRepo.obtenerMermaPorOTGlobal()
                : reportesRepo.obtenerMermaPorOTPorTizador(usuario.getIdUsuario());

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (ReporteMermaDTO dto : raw) {
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("codigoOt", dto.getCodigoOt());
            fila.put("pesoUtilizado", dto.getPesoUtilizado());
            fila.put("pesoMerma", dto.getPesoMerma());

            // Cálculo de % merma — PRESERVADO del ReporteDAO
            BigDecimal utilizado = dto.getPesoUtilizado();
            BigDecimal merma = dto.getPesoMerma();
            double pct = 0.0;
            if (utilizado != null && utilizado.compareTo(BigDecimal.ZERO) > 0 && merma != null) {
                pct = merma.doubleValue() / utilizado.doubleValue() * 100.0;
                pct = Math.round(pct * 100.0) / 100.0;
            }
            fila.put("porcentajeMerma", pct);
            resultado.add(fila);
        }
        return resultado;
    }

    /**
     * Tiempos de maquinistas por OT — filtrado por rol (PRESERVADO)
     */
    public List<Map<String, Object>> obtenerTiemposMaquinistas(Usuario usuario) {
        boolean esMaquinista = "MAQUINISTA".equalsIgnoreCase(usuario.getNombreRol());
        List<ReporteTiempoMaquinistaDTO> raw = esMaquinista
                ? reportesRepo.obtenerTiemposMaquinistaPorId(usuario.getIdUsuario())
                : reportesRepo.obtenerTiemposMaquinistasGlobal();

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (ReporteTiempoMaquinistaDTO dto : raw) {
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("codigoOt", dto.getCodigoOt());
            fila.put("maquinista", dto.getMaquinista());
            fila.put("inicioReal", dto.getInicioReal());
            fila.put("finReal", dto.getFinReal());

            // Cálculo de tiempo formateado — PRESERVADO del ReporteDAO
            int minAbs = dto.getMinutosAbsolutos() != null ? dto.getMinutosAbsolutos() : 0;
            fila.put("tiempoAbsoluto", (minAbs / 60) + "h " + (minAbs % 60) + "m");

            int minTrab = dto.getMinutosTrabajados() != null ? dto.getMinutosTrabajados() : 0;
            fila.put("minutosTrabajados", minTrab);
            fila.put("tiempoFormateado", (minTrab / 60) + "h " + (minTrab % 60) + "m");
            resultado.add(fila);
        }
        return resultado;
    }

    /**
     * Fallas por tipo de tela — filtrado por rol (PRESERVADO)
     */
    public List<Map<String, Object>> obtenerFallasPorTela(Usuario usuario) {
        String rol = usuario.getNombreRol();
        boolean verTodo = esRolGlobal(rol) || rol.toUpperCase().contains("CALIDAD");
        List<ReporteFallaDTO> raw = verTodo
                ? reportesRepo.obtenerFallasPorTelaGlobal()
                : reportesRepo.obtenerFallasPorTelaPorTizador(usuario.getIdUsuario());

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (ReporteFallaDTO dto : raw) {
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("codigoTela", dto.getCodigoTela());
            fila.put("fallasTotales", dto.getFallasTotales());
            resultado.add(fila);
        }
        return resultado;
    }

    /**
     * Inventario de telas por estado de calidad
     */
    public List<Map<String, Object>> obtenerInventarioTelas() {
        List<Map<String, Object>> resultado = new ArrayList<>();
        try {
            for (ReporteInventarioDTO dto : reportesRepo.obtenerInventarioTelas()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("estado", dto.getEstado());
                fila.put("cantidad", dto.getCantidad());
                fila.put("pesoTotal", dto.getPesoTotal());
                fila.put("pesoPromedio", dto.getPesoPromedio() != null
                        ? dto.getPesoPromedio().setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO);
                resultado.add(fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    /**
     * Cruce calidad vs productividad por maquinista — solo roles gerenciales (PRESERVADO)
     */
    public List<Map<String, Object>> obtenerCalidadVsProductividad() {
        List<Map<String, Object>> resultado = new ArrayList<>();
        try {
            for (ReporteCalidadProductividadDTO dto : reportesRepo.obtenerCalidadVsProductividad()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("maquinista", dto.getMaquinista());
                fila.put("minutos", dto.getMinutos() != null ? dto.getMinutos() : 0);
                fila.put("defectos", dto.getDefectos() != null ? dto.getDefectos() : 0);
                resultado.add(fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    /**
     * OTs problemáticas (top 15) — solo roles gerenciales (PRESERVADO)
     */
    public List<Map<String, Object>> obtenerOTsProblematicas() {
        List<Map<String, Object>> resultado = new ArrayList<>();
        try {
            for (ReporteOTProblematicaDTO dto : reportesRepo.obtenerOTsProblematicas()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("codigoOt", dto.getCodigoOt());
                fila.put("cantidadEst", dto.getCantidadEst() != null ? dto.getCantidadEst() : 0);
                fila.put("merma", dto.getMerma() != null ? dto.getMerma() : BigDecimal.ZERO);
                int defectos = dto.getDefectos() != null ? dto.getDefectos() : 0;
                int cantEst = dto.getCantidadEst() != null ? dto.getCantidadEst() : 0;
                fila.put("defectos", defectos);
                // Cálculo de tasa de falla — PRESERVADO del ReporteDAO
                double tasaFalla = cantEst > 0 ? ((double) defectos / cantEst) * 100 : 0.0;
                fila.put("tasaFalla", Math.round(tasaFalla * 100.0) / 100.0);
                resultado.add(fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    /**
     * Desviaciones de despacho (planeado vs final) — solo roles gerenciales (PRESERVADO)
     */
    public List<Map<String, Object>> obtenerDesviacionesDespacho() {
        List<Map<String, Object>> resultado = new ArrayList<>();
        try {
            for (ReporteDespachoDTO dto : reportesRepo.obtenerDesviacionesDespacho()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("codigoOt", dto.getCodigoOt());
                fila.put("estimada", dto.getEstimada() != null ? dto.getEstimada() : 0);
                fila.put("finalPrendas", dto.getFinal() != null ? dto.getFinal() : 0);
                fila.put("diferencia", dto.getDiferencia() != null ? dto.getDiferencia() : 0);
                resultado.add(fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    /**
     * Rendimiento personal de un maquinista — solo rol MAQUINISTA (PRESERVADO)
     */
    public List<Map<String, Object>> obtenerRendimientoMaquinista(int idUsuario) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        try {
            for (ReporteRendimientoDTO dto : reportesRepo.obtenerRendimientoMaquinista(idUsuario)) {
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("codigoOt", dto.getCodigoOt());
                int cantEst = dto.getCantidadEst() != null ? dto.getCantidadEst() : 0;
                fila.put("cantidadEst", cantEst);
                fila.put("fechaAsignada", dto.getFechaAsignada());
                int minTrab = dto.getMinutosTrabajados() != null ? dto.getMinutosTrabajados() : 0;
                fila.put("minutosTrabajados", minTrab);
                int diasRetraso = dto.getDiasRetraso() != null ? dto.getDiasRetraso() : 0;
                fila.put("diasRetraso", diasRetraso);
                int defPropios = dto.getDefectosPropios() != null ? dto.getDefectosPropios() : 0;
                fila.put("defectosPropios", defPropios);

                // Cálculos derivados — PRESERVADOS del ReporteDAO.obtenerRendimientoMaquinista()
                double vel = cantEst > 0 ? (double) minTrab / cantEst : minTrab;
                fila.put("velocidadMinPrenda", Math.round(vel * 100.0) / 100.0);

                String efiEnt = diasRetraso <= 0 ? "A Tiempo" : "Retraso (" + diasRetraso + "d)";
                fila.put("eficienciaEntrega", efiEnt);

                double tasaResp = cantEst > 0 ? ((double) defPropios / cantEst) * 100 : 0;
                fila.put("tasaResponsabilidad", Math.round(tasaResp * 100.0) / 100.0);

                resultado.add(fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    // ── HELPER: detección de rol global ─────────────────────────────────────

    /**
     * Determina si el rol tiene acceso global (ve todos los datos) — PRESERVADO del ReporteDAO.esRolGlobal()
     */
    public boolean esRolGlobal(String rol) {
        if (rol == null) return false;
        String r = rol.toUpperCase();
        return r.contains("ADMINISTRADOR") || r.contains("SUPERVISOR")
                || r.contains("GERENTE") || r.contains("JEFE");
    }

    /**
     * Determina si el rol tiene acceso gerencial (reportes estratégicos) — PRESERVADO del ReportesServlet.doGet()
     */
    public boolean esRolGerencial(String rol) {
        if (rol == null) return false;
        String r = rol.toUpperCase();
        return r.contains("ADMIN") || r.contains("GERENTE")
                || r.contains("SUPERVISOR") || r.contains("JEFE");
    }
}

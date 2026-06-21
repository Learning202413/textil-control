package pe.edu.upla.textil_control.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upla.textil_control.repository.AsignacionCargaRepository;
import pe.edu.upla.textil_control.repository.OrdenTrabajoRepository;
import pe.edu.upla.textil_control.repository.OrdenTrabajoResumenDTO;
import pe.edu.upla.textil_control.repository.ResumenCargaMaquinistaDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service: Dashboard
 * Migración del DashboardServlet.java original (proyecto Java EE) a Spring Boot.
 *
 * Todos los datos provienen de ESTE proyecto Spring (repositorios JPA +
 * queries nativas vía EntityManager), apuntando a la misma base de datos
 * TiDB Cloud configurada en application.properties.
 *
 * Cada bloque de datos está aislado en su propio try/catch, igual que en
 * el servlet original, para que un fallo puntual no rompa el resto del JSON.
 */
@Service
public class DashboardService {

    @Autowired
    private OrdenTrabajoRepository otRepo;

    @Autowired
    private AsignacionCargaRepository cargaRepo;

    @PersistenceContext
    private EntityManager em;

    // ════════════════════════════════════════════════════════════
    // 1. PROGRESO DE OTs (para la tabla renderizada en el HTML)
    //    Equivalente al bloque doGet() del servlet original.
    // ════════════════════════════════════════════════════════════
    public List<Map<String, Object>> calcularProgresoOTs() {
        List<Map<String, Object>> filas = new ArrayList<>();
        try {
            List<OrdenTrabajoResumenDTO> ordenes = otRepo.listarResumen();
            Map<Integer, int[]> prog = obtenerProgresoPorOt();

            for (OrdenTrabajoResumenDTO ot : ordenes) {
                int[] p = prog.get(ot.getIdOt());
                int total = p != null ? p[0] : 0;
                int comp  = p != null ? p[1] : 0;
                int proc  = p != null ? p[2] : 0;
                int pct   = total > 0 ? (comp * 100 / total) : 0;
                if ("FINALIZADA".equals(ot.getEstado())) pct = 100;

                String fecha = "";
                if (ot.getFechaCrea() != null) {
                    fecha = ot.getFechaCrea().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                }

                Map<String, Object> fila = new HashMap<>();
                fila.put("codigo", ot.getCodigoOt() != null ? ot.getCodigoOt() : "");
                fila.put("cliente", ot.getCliente() != null ? ot.getCliente() : "");
                fila.put("responsable", ot.getNombreResponsable() != null ? ot.getNombreResponsable() : "");
                fila.put("estado", ot.getEstado() != null ? ot.getEstado() : "");
                fila.put("fecha", fecha);
                fila.put("progreso", pct);
                fila.put("fasesComp", comp);
                fila.put("fasesTotal", total);
                fila.put("enProc", proc);
                filas.add(fila);
            }
        } catch (Exception ex) {
            System.err.println("[Dashboard] Error cargando OTs: " + ex.getMessage());
        }
        return filas;
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, int[]> obtenerProgresoPorOt() {
        Map<Integer, int[]> prog = new HashMap<>();
        try {
            Query q = em.createNativeQuery(
                    "SELECT id_ot, COUNT(*) AS total, " +
                            "SUM(CASE WHEN estado_fase='COMPLETADA' THEN 1 ELSE 0 END) AS comp, " +
                            "SUM(CASE WHEN estado_fase='EN_PROCESO' THEN 1 ELSE 0 END) AS proc " +
                            "FROM asignaciones_carga GROUP BY id_ot");
            List<Object[]> rows = q.getResultList();
            for (Object[] r : rows) {
                int idOt = ((Number) r[0]).intValue();
                int total = ((Number) r[1]).intValue();
                int comp  = ((Number) r[2]).intValue();
                int proc  = ((Number) r[3]).intValue();
                prog.put(idOt, new int[]{total, comp, proc});
            }
        } catch (Exception ex) {
            System.err.println("[Dashboard] asignaciones_carga: " + ex.getMessage());
        }
        return prog;
    }

    // ════════════════════════════════════════════════════════════
    // JSON COMPLETO (equivalente a servirJSON() del servlet original)
    // ════════════════════════════════════════════════════════════
    public Map<String, Object> construirDatosJson() {
        Map<String, Object> json = new LinkedHashMap<>();

        // ── 1. OTs con progreso ──────────────────────────────────
        List<OrdenTrabajoResumenDTO> ordenes = new ArrayList<>();
        List<Map<String, Object>> ordenesJson = new ArrayList<>();
        try {
            ordenes = otRepo.listarResumen();
            Map<Integer, int[]> prog = obtenerProgresoPorOt();
            for (OrdenTrabajoResumenDTO ot : ordenes) {
                int[] p = prog.get(ot.getIdOt());
                int total = p != null ? p[0] : 0;
                int comp  = p != null ? p[1] : 0;
                int proc  = p != null ? p[2] : 0;
                int pct = total > 0 ? (comp * 100 / total) : 0;
                if ("FINALIZADA".equals(ot.getEstado())) pct = 100;
                String fecha = ot.getFechaCrea() != null
                        ? ot.getFechaCrea().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";

                Map<String, Object> o = new LinkedHashMap<>();
                o.put("codigo", nz(ot.getCodigoOt()));
                o.put("cliente", nz(ot.getCliente()));
                o.put("responsable", nz(ot.getNombreResponsable()));
                o.put("estado", nz(ot.getEstado()));
                o.put("fecha", fecha);
                o.put("progreso", pct);
                o.put("fasesComp", comp);
                o.put("fasesTotal", total);
                o.put("enProc", proc);
                ordenesJson.add(o);
            }
        } catch (Exception e) {
            System.err.println("[Dashboard JSON] ordenes: " + e.getMessage());
        }
        json.put("ordenes", ordenesJson);

        // ── 2. KPIs ───────────────────────────────────────────────
        Map<String, Object> kpis = new LinkedHashMap<>();
        try {
            int otActivas = (int) ordenes.stream()
                    .filter(o -> "CREADA".equals(o.getEstado()) || "EN_PROCESO".equals(o.getEstado()))
                    .count();
            int otFinalizadas = (int) ordenes.stream()
                    .filter(o -> "FINALIZADA".equals(o.getEstado()))
                    .count();

            double mermaPromedio = 0;
            try {
                Object r = em.createNativeQuery("SELECT AVG(porcentaje_merma) FROM mermas").getSingleResult();
                if (r != null) mermaPromedio = ((Number) r).doubleValue();
            } catch (Exception e) { System.err.println("[Dashboard JSON] merma avg: " + e.getMessage()); }

            int alertasCalidad = 0;
            try {
                Object r = em.createNativeQuery(
                        "SELECT COUNT(*) FROM defectos_reproceso WHERE estado='PENDIENTE'").getSingleResult();
                alertasCalidad = ((Number) r).intValue();
            } catch (Exception e) { System.err.println("[Dashboard JSON] alertas: " + e.getMessage()); }

            int telasListaCorte = 0;
            try {
                Object r = em.createNativeQuery(
                        "SELECT COUNT(*) FROM tiempos_reposo WHERE estado='APTO_CORTE'").getSingleResult();
                telasListaCorte = ((Number) r).intValue();
            } catch (Exception e) { System.err.println("[Dashboard JSON] telas corte: " + e.getMessage()); }

            double eficiencia = 0;
            try {
                Object[] r = (Object[]) em.createNativeQuery(
                        "SELECT COUNT(*) AS total, SUM(CASE WHEN estado_fase='COMPLETADA' THEN 1 ELSE 0 END) AS comp " +
                                "FROM asignaciones_carga").getSingleResult();
                int t = ((Number) r[0]).intValue();
                int c = r[1] != null ? ((Number) r[1]).intValue() : 0;
                if (t > 0) eficiencia = Math.round((c * 100.0 / t) * 10) / 10.0;
            } catch (Exception e) { System.err.println("[Dashboard JSON] eficiencia: " + e.getMessage()); }

            kpis.put("otActivas", otActivas);
            kpis.put("prendas", otFinalizadas);
            kpis.put("eficiencia", eficiencia);
            kpis.put("alertas", alertasCalidad);
            kpis.put("mermaPromedio", Math.round(mermaPromedio * 10) / 10.0);
            kpis.put("telasListaCorte", telasListaCorte);
        } catch (Exception e) {
            System.err.println("[Dashboard JSON] kpis: " + e.getMessage());
        }
        json.put("kpis", kpis);

        // ── 3. Maquinistas con cargas reales ─────────────────────
        List<Map<String, Object>> maquinistasJson = new ArrayList<>();
        try {
            List<ResumenCargaMaquinistaDTO> maq = cargaRepo.resumenCargaPorMaquinista();

            Map<Integer, Integer> completadas = new HashMap<>();
            try {
                List<Object[]> rows = em.createNativeQuery(
                        "SELECT id_maquinista, COUNT(*) AS cnt FROM asignaciones_carga " +
                                "WHERE estado_fase='COMPLETADA' GROUP BY id_maquinista").getResultList();
                for (Object[] r : rows) {
                    completadas.put(((Number) r[0]).intValue(), ((Number) r[1]).intValue());
                }
            } catch (Exception e) { /* se deja vacío si falla */ }

            for (ResumenCargaMaquinistaDTO m : maq) {
                int comp2 = completadas.getOrDefault(m.getIdMaquinista(), 0);
                Map<String, Object> mm = new LinkedHashMap<>();
                mm.put("nombre", nz(m.getNombreMaquinista()));
                mm.put("completadas", comp2);
                mm.put("pendientes", m.getTotalActivas() != null ? m.getTotalActivas() : 0);
                maquinistasJson.add(mm);
            }
        } catch (Exception e) {
            System.err.println("[Dashboard JSON] maquinistas: " + e.getMessage());
        }
        json.put("maquinistas", maquinistasJson);

        // ── 4. Mermas por fase (tabla mermas) ────────────────────
        List<Map<String, Object>> mermasJson = new ArrayList<>();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT fase AS label, SUM(peso_merma_kg) AS valor FROM mermas " +
                            "GROUP BY fase ORDER BY valor DESC").getResultList();
            for (Object[] r : rows) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("label", nz((String) r[0]));
                m.put("valor", r[1] != null ? ((Number) r[1]).doubleValue() : 0.0);
                mermasJson.add(m);
            }
        } catch (Exception e) {
            System.err.println("[Dashboard JSON] mermas: " + e.getMessage());
        }
        json.put("mermas", mermasJson);

        // ── 5. Merma % por OT ─────────────────────────────────────
        List<Map<String, Object>> mermaOtJson = new ArrayList<>();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT ot.codigo_ot AS ot, " +
                            "ROUND(SUM(m.peso_merma_kg)/NULLIF(SUM(m.peso_utilizado_kg),0)*100,2) AS pct " +
                            "FROM mermas m JOIN orden_trabajo ot ON m.id_ot=ot.id_ot " +
                            "GROUP BY m.id_ot, ot.codigo_ot ORDER BY pct DESC LIMIT 10").getResultList();
            for (Object[] r : rows) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("ot", nz((String) r[0]));
                m.put("pct", r[1] != null ? ((Number) r[1]).doubleValue() : 0.0);
                mermaOtJson.add(m);
            }
        } catch (Exception e) {
            System.err.println("[Dashboard JSON] mermaOT: " + e.getMessage());
        }
        json.put("mermaOT", mermaOtJson);

        // ── 6. Defectos por tipo/estado ───────────────────────────
        List<Map<String, Object>> defectosJson = new ArrayList<>();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT tipo_falla AS tipo, estado, COUNT(*) AS cantidad " +
                            "FROM defectos_reproceso GROUP BY tipo_falla, estado ORDER BY cantidad DESC").getResultList();
            for (Object[] r : rows) {
                Map<String, Object> d = new LinkedHashMap<>();
                d.put("tipo", nz((String) r[0]));
                d.put("estado", nz((String) r[1]));
                d.put("cantidad", ((Number) r[2]).intValue());
                defectosJson.add(d);
            }
        } catch (Exception e) {
            System.err.println("[Dashboard JSON] defectos: " + e.getMessage());
        }
        json.put("defectos", defectosJson);

        // ── 7. Telas por estado de calidad ────────────────────────
        List<Map<String, Object>> telasJson = new ArrayList<>();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT estado_calidad AS estado, origen, tipo_tejido AS tejido, COUNT(*) AS cantidad " +
                            "FROM telas GROUP BY estado_calidad, origen, tipo_tejido").getResultList();
            for (Object[] r : rows) {
                Map<String, Object> t = new LinkedHashMap<>();
                t.put("estado", nz((String) r[0]));
                t.put("origen", nz((String) r[1]));
                t.put("tejido", nz((String) r[2]));
                t.put("cantidad", ((Number) r[3]).intValue());
                telasJson.add(t);
            }
        } catch (Exception e) {
            System.err.println("[Dashboard JSON] telas: " + e.getMessage());
        }
        json.put("telas", telasJson);

        // ── 8. Tiempos de reposo ───────────────────────────────────
        List<Map<String, Object>> repososJson = new ArrayList<>();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT t.codigo_tela AS tela, tr.estado, " +
                            "TIMESTAMPDIFF(HOUR, tr.fecha_inicio, IFNULL(tr.fecha_fin_real, tr.fecha_fin_estimada)) AS horas " +
                            "FROM tiempos_reposo tr JOIN telas t ON tr.id_tela=t.id_tela " +
                            "ORDER BY tr.fecha_inicio DESC LIMIT 10").getResultList();
            for (Object[] r : rows) {
                Map<String, Object> rep = new LinkedHashMap<>();
                rep.put("tela", nz((String) r[0]));
                rep.put("estado", nz((String) r[1]));
                rep.put("horas", r[2] != null ? ((Number) r[2]).intValue() : 0);
                repososJson.add(rep);
            }
        } catch (Exception e) {
            System.err.println("[Dashboard JSON] reposos: " + e.getMessage());
        }
        json.put("reposos", repososJson);

        // ── 9. Cargas por OT (progreso real de cada OT activa) ────
        List<Map<String, Object>> cargasJson = new ArrayList<>();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT " +
                            "  ot.codigo_ot AS maquinista, " +
                            "  IFNULL((" +
                            "    SELECT CONCAT(u2.nombre,' ',u2.apellido) " +
                            "    FROM asignaciones_carga ac2 " +
                            "    LEFT JOIN usuarios u2 ON ac2.id_maquinista=u2.id_usuario " +
                            "    WHERE ac2.id_ot=ot.id_ot AND ac2.id_maquinista IS NOT NULL LIMIT 1" +
                            "  ),'Sin asignar') AS fase, " +
                            "  SUM(CASE WHEN ac.estado_fase='COMPLETADA' THEN 1 ELSE 0 END) AS completadas, " +
                            "  SUM(CASE WHEN ac.estado_fase IN('PENDIENTE','EN_PROCESO') THEN 1 ELSE 0 END) AS pendientes " +
                            "FROM orden_trabajo ot " +
                            "JOIN asignaciones_carga ac ON ac.id_ot = ot.id_ot " +
                            "WHERE ot.estado NOT IN('ANULADA','FINALIZADA') " +
                            "GROUP BY ot.id_ot, ot.codigo_ot " +
                            "ORDER BY pendientes DESC, completadas DESC LIMIT 20").getResultList();
            for (Object[] r : rows) {
                Map<String, Object> c = new LinkedHashMap<>();
                c.put("maquinista", nz((String) r[0]));
                c.put("fase", nz((String) r[1]));
                c.put("completadas", ((Number) r[2]).intValue());
                c.put("pendientes", ((Number) r[3]).intValue());
                cargasJson.add(c);
            }
        } catch (Exception e) {
            System.err.println("[Dashboard JSON] cargas: " + e.getMessage());
        }
        json.put("cargas", cargasJson);

        // ── 10. Alertas reales: defectos PENDIENTE por OT ─────────
        List<Map<String, Object>> alertasJson = new ArrayList<>();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT ot.codigo_ot AS codigo, COUNT(*) AS cant, " +
                            "CONCAT(COUNT(*),' defecto(s) pendiente(s)') AS mensaje " +
                            "FROM defectos_reproceso dr JOIN orden_trabajo ot ON dr.id_ot=ot.id_ot " +
                            "WHERE dr.estado='PENDIENTE' " +
                            "GROUP BY dr.id_ot, ot.codigo_ot ORDER BY cant DESC LIMIT 10").getResultList();
            for (Object[] r : rows) {
                Map<String, Object> a = new LinkedHashMap<>();
                a.put("codigo", nz((String) r[0]));
                a.put("mensaje", nz((String) r[2]));
                alertasJson.add(a);
            }
        } catch (Exception e) {
            System.err.println("[Dashboard JSON] alertas: " + e.getMessage());
        }
        json.put("alertas", alertasJson);

        // ── 11. Inventario telas críticas (peso_real ASC) ─────────
        List<Map<String, Object>> inventarioJson = new ArrayList<>();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT codigo_tela AS codigo, tipo_tejido AS tipo, " +
                            "CONCAT(ROUND(peso_real,1),' kg') AS restante " +
                            "FROM telas WHERE estado_calidad != 'RECHAZADO' " +
                            "ORDER BY peso_real ASC LIMIT 8").getResultList();
            for (Object[] r : rows) {
                Map<String, Object> inv = new LinkedHashMap<>();
                inv.put("codigo", nz((String) r[0]));
                inv.put("tipo", nz((String) r[1]));
                inv.put("restante", nz((String) r[2]));
                inventarioJson.add(inv);
            }
        } catch (Exception e) {
            System.err.println("[Dashboard JSON] inventario: " + e.getMessage());
        }
        json.put("inventarioCritico", inventarioJson);

        // ── 12. Eficiencia semanal real ───────────────────────────
        List<Map<String, Object>> eficienciaSemanalJson = new ArrayList<>();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT DATE_FORMAT(MIN(fecha_ref), '%d/%m') AS semana, " +
                            "ROUND(SUM(es_completada)*100.0/COUNT(*), 1) AS pct " +
                            "FROM (" +
                            "  SELECT " +
                            "    COALESCE(ac.fecha_asignacion, ot.fecha_crea) AS fecha_ref, " +
                            "    YEARWEEK(COALESCE(ac.fecha_asignacion, ot.fecha_crea), 1) AS semana_key, " +
                            "    CASE WHEN ac.estado_fase='COMPLETADA' THEN 1 ELSE 0 END AS es_completada " +
                            "  FROM asignaciones_carga ac " +
                            "  JOIN orden_trabajo ot ON ac.id_ot = ot.id_ot " +
                            "  WHERE COALESCE(ac.fecha_asignacion, ot.fecha_crea) >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
                            "    AND ot.estado NOT IN('ANULADA') " +
                            ") sub " +
                            "GROUP BY semana_key " +
                            "ORDER BY semana_key ASC LIMIT 12").getResultList();
            for (Object[] r : rows) {
                Map<String, Object> ef = new LinkedHashMap<>();
                ef.put("semana", nz((String) r[0]));
                ef.put("pct", r[1] != null ? ((Number) r[1]).doubleValue() : 0.0);
                eficienciaSemanalJson.add(ef);
            }
        } catch (Exception e) {
            System.err.println("[Dashboard JSON] eficienciaSemanal: " + e.getMessage());
        }
        json.put("eficienciaSemanal", eficienciaSemanalJson);

        return json;
    }

    private String nz(String s) {
        return s != null ? s : "";
    }
}
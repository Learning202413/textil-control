package pe.edu.upla.textil_control.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upla.textil_control.model.Merma;
import pe.edu.upla.textil_control.model.OrdenTrabajo;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.repository.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service: Merma
 * HU04: Registro de Merma por Tipo de Tejido
 *
 * LÓGICA DE NEGOCIO PRESERVADA ÍNTEGRAMENTE del MermasServlet:
 *  - VALIDACIÓN: pesoMerma > pesoUtilizado → bloquear
 *  - VALIDACIÓN: OT debe estar en estado EN_PROCESO
 *  - Listado: Admin→todas, Tizador→propias, JefeProducción→todas
 *  - actualizar/eliminar: solo Admin
 *  - calcularPorcentajePorOt(): SUM(merma)/SUM(utilizado)*100
 */
@Service
public class MermaService {

    @Autowired
    private MermaRepository mermaRepo;

    @Autowired
    private OrdenTrabajoRepository otRepo;

    // ── LECTURA ───────────────────────────────────────────────

    /**
     * Lista mermas según rol — PRESERVADO del MermasServlet.doGet()
     */
    public List<MermaResumenDTO> listarSegunRol(Usuario usuario) {
        boolean esAdmin   = "ADMINISTRADOR".equalsIgnoreCase(usuario.getNombreRol());
        boolean esTizador = "TIZADOR".equalsIgnoreCase(usuario.getNombreRol());

        if (esAdmin) {
            return mermaRepo.listarTodas();
        } else if (esTizador) {
            return mermaRepo.listarPorTizador(usuario.getIdUsuario());
        } else {
            return mermaRepo.listarTodas(); // Jefe producción: ve todo, sin registrar
        }
    }

    /** Filtrar mermas por OT */
    public List<MermaResumenDTO> listarPorOt(int idOt) {
        return mermaRepo.listarPorOt(idOt);
    }

    /** CUS 4.2: Porcentaje acumulado por OT */
    public BigDecimal calcularPorcentajePorOt(int idOt) {
        BigDecimal pct = mermaRepo.calcularPorcentajePorOt(idOt);
        return pct != null ? pct : BigDecimal.ZERO;
    }

    /** Telas disponibles para registrar merma */
    public List<TelaMermaDTO> listarTelasParaMerma() {
        return mermaRepo.listarTelasParaMerma();
    }

    /** OTs con mermas para filtro */
    public List<OtResumenDTO> listarOtsConMermas() {
        return mermaRepo.listarOtsConMermas();
    }

    // ── ESCRITURA ─────────────────────────────────────────────

    /**
     * CUS 4.1 + 4.2: Registrar merma — PRESERVADO del MermasServlet.doPost("registrar")
     * Validaciones:
     *  1. OT debe estar en estado EN_PROCESO
     *  2. pesoMerma <= pesoUtilizado
     *
     * @return la merma guardada
     * @throws IllegalStateException con código de error si falla validación
     */
    @Transactional
    public Merma registrar(Merma merma) {
        // VALIDACIÓN PRESERVADA: OT debe estar en estado EN_PROCESO
        OrdenTrabajo ot = otRepo.findById(merma.getIdOt()).orElse(null);
        if (ot == null || !"EN_PROCESO".equals(ot.getEstado())) {
            throw new IllegalStateException("La OT no está en estado EN_PROCESO");
        }

        // VALIDACIÓN PRESERVADA: peso_merma <= peso_utilizado
        if (merma.getPesomermaKg().compareTo(merma.getPesoUtilizadoKg()) > 0) {
            throw new IllegalStateException("mermaExcede");
        }

        return mermaRepo.save(merma);
    }

    /**
     * Actualizar merma — PRESERVADO del MermasServlet.doPost("actualizar")
     * Solo Admin. Validación: pesoMerma <= pesoUtilizado
     */
    @Transactional
    public Merma actualizar(Merma mermaActualizada) {
        // 🔥 VALIDACIÓN 1: Verificar el estado de la OT antes de permitir la edición
        String estadoOt = mermaRepo.obtenerEstadoOtPorMerma(mermaActualizada.getIdMerma());
        if ("FINALIZADA".equals(estadoOt) || "ANULADA".equals(estadoOt)) {
            throw new IllegalStateException("No se puede editar: La Orden de Trabajo ya se encuentra en estado " + estadoOt + ".");
        }

        // 🔥 VALIDACIÓN 2: peso_merma <= peso_utilizado
        if (mermaActualizada.getPesomermaKg().compareTo(mermaActualizada.getPesoUtilizadoKg()) > 0) {
            throw new IllegalStateException("El peso de merma no puede ser mayor al peso utilizado.");
        }

        // Evitar nulos: Recuperamos la merma original y solo actualizamos los campos permitidos
        Merma existente = mermaRepo.findById(mermaActualizada.getIdMerma())
                .orElseThrow(() -> new RuntimeException("Merma no encontrada"));

        existente.setFase(mermaActualizada.getFase());
        existente.setPesoUtilizadoKg(mermaActualizada.getPesoUtilizadoKg());
        existente.setPesomermaKg(mermaActualizada.getPesomermaKg());
        existente.setObservaciones(mermaActualizada.getObservaciones());

        return mermaRepo.save(existente);
    }

    @Transactional
    public void eliminar(int idMerma) {
        // 🔥 VALIDACIÓN: Verificar el estado de la OT antes de permitir la eliminación
        String estadoOt = mermaRepo.obtenerEstadoOtPorMerma(idMerma);
        if ("FINALIZADA".equals(estadoOt) || "ANULADA".equals(estadoOt)) {
            throw new IllegalStateException("No se puede eliminar: La Orden de Trabajo ya se encuentra en estado " + estadoOt + ".");
        }

        mermaRepo.deleteById(idMerma);
    }
}

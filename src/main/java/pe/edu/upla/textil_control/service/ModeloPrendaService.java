package pe.edu.upla.textil_control.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upla.textil_control.model.FaseProduccion;
import pe.edu.upla.textil_control.model.ModeloPrenda;
import pe.edu.upla.textil_control.model.PiezaModelo;
import pe.edu.upla.textil_control.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModeloPrendaService {

    @Autowired private ModeloPrendaRepository modeloRepo;
    @Autowired private PiezaModeloRepository piezaRepo;
    @Autowired private FaseProduccionRepository faseRepo;
    @Autowired private EntityManager entityManager;

    public List<ModeloResumenDTO> listarResumen() { return modeloRepo.listarResumen(); }
    public List<FaseProduccion> listarFases() { return faseRepo.findAll(org.springframework.data.domain.Sort.by("orden")); }
    public boolean estaEnUso(Integer idModelo) {
        return modeloRepo.countOrdenesByModelo(idModelo) > 0;
    }

    public ModeloPrenda buscarConPiezas(Integer id) {
        ModeloPrenda m = modeloRepo.findById(id).orElse(null);
        if (m != null) {
            List<PiezaModelo> piezas = piezaRepo.findByIdModelo(id);
            for (PiezaModelo p : piezas) {
                // Obtenemos las fases asignadas usando consulta nativa con EntityManager
                List<Integer> fases = entityManager.createNativeQuery("SELECT id_fase FROM pieza_ruta_fase WHERE id_pieza = " + p.getIdPieza()).getResultList();
                p.setIdFasesAsignadas(fases);
            }
            m.setPiezas(piezas);
        }
        return m;
    }

    @Transactional
    public void guardarTransaccional(ModeloPrenda m) {
        // 1. Guardar modelo
        modeloRepo.save(m);
        // 2. Procesar Piezas y Rutas
        procesarPiezasYRutas(m);
    }

    @Transactional
    public void actualizarTransaccional(ModeloPrenda m) {
        // 1. Limpiar datos antiguos
        modeloRepo.deleteRutasByModelo(m.getIdModelo());
        piezaRepo.deleteByIdModelo(m.getIdModelo());
        // 2. Guardar modelo actualizado
        modeloRepo.save(m);
        // 3. Procesar nuevas Piezas y Rutas
        procesarPiezasYRutas(m);
    }

    private void procesarPiezasYRutas(ModeloPrenda m) {
        for (PiezaModelo p : m.getPiezas()) {
            p.setIdModelo(m.getIdModelo());
            piezaRepo.save(p); // Guarda la pieza y genera su ID

            if (p.getIdFasesAsignadas() != null) {
                for (Integer idFase : p.getIdFasesAsignadas()) {
                    modeloRepo.insertRutaPieza(p.getIdPieza(), idFase);
                }
            }
        }
        // FASE GLOBAL (Ensamblaje = ID 6)
        modeloRepo.insertRutaGlobal(m.getIdModelo(), 6);
    }

    @Transactional
    public void eliminar(Integer id) { modeloRepo.deleteById(id); }

    @Transactional
    public FaseProduccion agregarFase(String nombre, int orden, String descripcion) {
        try {
            faseRepo.desplazarOrdenes(orden);
            FaseProduccion f = new FaseProduccion();
            f.setNombre(nombre); f.setOrden(orden); f.setDescripcion(descripcion);
            return faseRepo.save(f);
        } catch (Exception e) { // Manejo de duplicados de orden
            int max = faseRepo.getMaxOrden();
            FaseProduccion f = new FaseProduccion();
            f.setNombre(nombre); f.setOrden(max + 1); f.setDescripcion(descripcion);
            return faseRepo.save(f);
        }
    }
}
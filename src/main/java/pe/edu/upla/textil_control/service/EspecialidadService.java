package pe.edu.upla.textil_control.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upla.textil_control.model.Especialidad;
import pe.edu.upla.textil_control.repository.EspecialidadRepository;

import java.util.List;

@Service
public class EspecialidadService {

    @Autowired
    private EspecialidadRepository repository;

    public List<Especialidad> listarTodos() {
        return repository.findAllByOrderByNombreAsc();
    }

    public Especialidad buscarPorId(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public void guardar(Especialidad especialidad) {
        repository.save(especialidad); // Spring detecta si es INSERT (id nulo) o UPDATE (con id)
    }

    public void eliminar(Integer id) {
        repository.deleteById(id);
    }
}
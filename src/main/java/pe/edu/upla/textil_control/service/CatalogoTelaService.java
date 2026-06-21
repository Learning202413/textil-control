package pe.edu.upla.textil_control.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upla.textil_control.model.CatalogoTela;
import pe.edu.upla.textil_control.repository.CatalogoTelaRepository;

import java.util.List;

@Service
public class CatalogoTelaService {

    @Autowired
    private CatalogoTelaRepository repository;

    public List<CatalogoTela> listarTodos() {
        return repository.findAll();
    }

    public CatalogoTela buscarPorId(Integer id) {
        return repository.findById(id).orElse(null); // Spring Boot hace el SELECT por id automático
    }

    public void guardar(CatalogoTela tela) {
        repository.save(tela); // Si no tiene ID, hace INSERT
    }

    public void actualizar(CatalogoTela tela) {
        repository.save(tela); // Si tiene ID, hace UPDATE
        // Sincronizamos las telas hijas en el inventario
        repository.actualizarReposoEnTelas(tela.getIdCatalogo(), tela.isRequiereReposo());
    }

    public boolean eliminar(Integer idCatalogo) {
        // Validación: ¿Está en uso?
        if (repository.countTelasByCatalogoId(idCatalogo) > 0) {
            return false; // No se puede eliminar
        }
        repository.deleteById(idCatalogo);
        return true; // Eliminado con éxito
    }
}
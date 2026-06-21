package pe.edu.upla.textil_control.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upla.textil_control.model.Permiso;
import pe.edu.upla.textil_control.repository.PermisoRepository;

import java.util.List;
import java.util.Set;

@Service
public class PermisoService {

    @Autowired
    private PermisoRepository permisoRepository;

    // 1. Método para pintar la vista con los checkboxes marcados/desmarcados
    public List<Permiso> listarPermisosParaMatriz(Integer idRol) {
        List<Permiso> todosLosPermisos = permisoRepository.findAll();
        Set<Integer> permisosDelRol = permisoRepository.findIdsPermisosByIdRol(idRol);

        for (Permiso p : todosLosPermisos) {
            p.setAsignado(permisosDelRol.contains(p.getIdPermiso()));
        }
        return todosLosPermisos;
    }

    // 2. Método para guardar los cambios del formulario
    @Transactional
    public void actualizarPermisosDeRol(Integer idRol, List<Integer> idPermisosSeleccionados) {
        // A. Primero borramos todos los permisos actuales de ese rol
        permisoRepository.deletePermisosByRol(idRol);

        // B. Luego insertamos los que vinieron marcados en el formulario
        if (idPermisosSeleccionados != null && !idPermisosSeleccionados.isEmpty()) {
            for (Integer idPermiso : idPermisosSeleccionados) {
                permisoRepository.insertRolPermiso(idRol, idPermiso);
            }
        }
    }
}
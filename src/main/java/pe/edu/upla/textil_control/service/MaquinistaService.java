package pe.edu.upla.textil_control.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upla.textil_control.model.Especialidad;
import pe.edu.upla.textil_control.model.MaquinistaDTO;
import pe.edu.upla.textil_control.model.Usuario;
import pe.edu.upla.textil_control.repository.EspecialidadRepository;
import pe.edu.upla.textil_control.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class MaquinistaService {

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private EspecialidadRepository especialidadRepo;
    @Autowired private PasswordService passwordService;

    private static final int ID_ROL_MAQUINISTA = 6;

    public List<MaquinistaDTO> listarMaquinistas() {
        List<Usuario> usuarios = usuarioRepo.findByIdRol(ID_ROL_MAQUINISTA);
        List<MaquinistaDTO> lista = new ArrayList<>();
        for (Usuario u : usuarios) {
            List<Especialidad> especialidades = especialidadRepo.findByUsuarioId(u.getIdUsuario());
            lista.add(new MaquinistaDTO(u, especialidades));
        }
        return lista;
    }

    @Transactional
    public void guardarMaquinista(Usuario u, List<Integer> idEspecialidades) {
        // Configuraciones por defecto
        u.setIdRol(ID_ROL_MAQUINISTA);
        u.setActivo(true);
        u.setPassword(passwordService.encriptar(u.getPassword()));
        u.setHorarioRestringido(true);
        u.setHorarioDias("LUN,MAR,MIE,JUE,VIE,SAB");
        u.setHorarioInicio("07:00:00");
        u.setHorarioFin("17:00:00");

        usuarioRepo.save(u); // Guarda y genera ID

        if (idEspecialidades != null) {
            for (Integer idEsp : idEspecialidades) {
                especialidadRepo.insertUsuarioEspecialidad(u.getIdUsuario(), idEsp);
            }
        }
    }

    @Transactional
    public void actualizarMaquinista(Usuario uDatos, List<Integer> idEspecialidades) {
        Usuario uDB = usuarioRepo.findById(uDatos.getIdUsuario()).orElseThrow();

        uDB.setNombre(uDatos.getNombre());
        uDB.setApellido(uDatos.getApellido());
        uDB.setEmail(uDatos.getEmail());

        // Si escribió una contraseña nueva, la actualizamos
        if (uDatos.getPassword() != null && !uDatos.getPassword().isBlank()) {
            uDB.setPassword(passwordService.encriptar(uDatos.getPassword()));
        }

        usuarioRepo.save(uDB);

        // Actualizamos especialidades (Borrar y volver a insertar)
        especialidadRepo.deleteEspecialidadesByUsuario(uDB.getIdUsuario());
        if (idEspecialidades != null) {
            for (Integer idEsp : idEspecialidades) {
                especialidadRepo.insertUsuarioEspecialidad(uDB.getIdUsuario(), idEsp);
            }
        }
    }

    public void cambiarEstado(Integer idUsuario, boolean activo) {
        Usuario u = usuarioRepo.findById(idUsuario).orElseThrow();
        u.setActivo(activo);
        usuarioRepo.save(u);
    }

    public boolean eliminarMaquinista(Integer idUsuario) {
        if (usuarioRepo.countActividadesByUsuario(idUsuario) > 0) {
            return false; // Está en uso
        }
        especialidadRepo.deleteEspecialidadesByUsuario(idUsuario);
        usuarioRepo.deleteById(idUsuario);
        return true;
    }
}
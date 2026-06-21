package pe.edu.upla.textil_control.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upla.textil_control.model.Notificacion;
import pe.edu.upla.textil_control.repository.NotificacionRepository;
import java.util.List;

@Service
public class NotificacionService {
    @Autowired private NotificacionRepository repository;

    public void registrarNotificacion(String titulo, String mensaje, String tipo, Integer idReferencia, String paraRol) {
        Notificacion n = new Notificacion();
        n.setTitulo(titulo);
        n.setMensaje(mensaje);
        n.setTipo(tipo);
        n.setIdReferencia(idReferencia);
        n.setParaRol(paraRol);
        n.setLeida(false);
        repository.save(n);
    }

    public List<Notificacion> listarNoLeidasPorRol(String rol) {
        return repository.listarNoLeidasPorRol(rol);
    }

    public List<Notificacion> listarTodasPorRol(String rol, int limite) {
        return repository.listarTodasPorRol(rol, limite);
    }

    public void marcarComoLeida(Integer idNotificacion) {
        repository.marcarComoLeida(idNotificacion);
    }
}
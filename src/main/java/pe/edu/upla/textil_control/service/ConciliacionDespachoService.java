package pe.edu.upla.textil_control.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upla.textil_control.model.ConciliacionDespacho;
import pe.edu.upla.textil_control.repository.ConciliacionDespachoRepository;
import pe.edu.upla.textil_control.repository.ConciliacionDespachoResumenDTO;
import java.util.List;

@Service
public class ConciliacionDespachoService {
    @Autowired private ConciliacionDespachoRepository repository;

    public List<ConciliacionDespachoResumenDTO> listarLotes() {
        return repository.listarLotesParaDespacho();
    }

    public ConciliacionDespacho obtenerPorOt(Integer idOt) {
        return repository.findByIdOt(idOt);
    }

    @Transactional
    public void registrarConciliacion(int idOt, int cantidadFinal, int idResponsable, String obs) {
        ConciliacionDespachoResumenDTO dto = repository.listarLotesParaDespacho().stream()
                .filter(l -> l.getIdOt() == idOt).findFirst()
                .orElseThrow(() -> new IllegalStateException("OT no encontrada"));

        int diferencia = cantidadFinal - dto.getCantidadEstimada();
        String estado = (diferencia == 0) ? "CONCILIADO_OK" : "MERMA_DETECTADA";

        repository.insertarConciliacion(idOt, cantidadFinal, diferencia, estado, idResponsable, obs);
    }

    @Transactional
    public boolean confirmarDespacho(int idConciliacion) {
        return repository.confirmarDespacho(idConciliacion) > 0;
    }
}
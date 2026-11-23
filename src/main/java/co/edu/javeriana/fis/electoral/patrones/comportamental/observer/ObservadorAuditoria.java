package co.edu.javeriana.fis.electoral.patrones.comportamental.observer;

import java.util.ArrayList;
import java.util.List;

/** Observador que conserva la bitacora completa de eventos para auditoria. */
public class ObservadorAuditoria implements ObservadorElectoral {

    private final List<EventoElectoral> eventos = new ArrayList<>();

    @Override
    public void actualizar(EventoElectoral evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento es obligatorio");
        }
        eventos.add(evento);
    }

    public List<EventoElectoral> getEventos() {
        return List.copyOf(eventos);
    }

    public int totalEventos() {
        return eventos.size();
    }
}

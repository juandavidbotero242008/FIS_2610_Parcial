package co.edu.javeriana.fis.electoral.patrones.comportamental.observer;

import java.util.EnumMap;
import java.util.Map;

/** Observador que lleva el conteo de eventos por tipo para analitica. */
public class ObservadorAnalitica implements ObservadorElectoral {

    private final Map<TipoEvento, Integer> conteos = new EnumMap<>(TipoEvento.class);

    @Override
    public void actualizar(EventoElectoral evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento es obligatorio");
        }
        conteos.merge(evento.tipo(), 1, Integer::sum);
    }

    /** Conteo de eventos del tipo indicado; 0 si no ha ocurrido ninguno. */
    public int conteo(TipoEvento tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de evento es obligatorio");
        }
        return conteos.getOrDefault(tipo, 0);
    }

    public int totalEventos() {
        return conteos.values().stream().mapToInt(Integer::intValue).sum();
    }
}

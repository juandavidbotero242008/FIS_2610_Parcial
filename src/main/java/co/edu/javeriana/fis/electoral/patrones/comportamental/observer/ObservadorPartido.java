package co.edu.javeriana.fis.electoral.patrones.comportamental.observer;

import co.edu.javeriana.fis.electoral.dominio.Partido;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Observador de un partido politico.
 *
 * <p>Recibe unicamente los eventos cuyo campo {@code partidoNombre} coincide
 * con el partido interesado, de modo que cada tolda puede seguir las
 * reclamaciones que le competen sin recibir ruido de otros partidos.</p>
 */
public class ObservadorPartido implements ObservadorElectoral {

    private final Partido partido;
    private final List<EventoElectoral> eventos = new ArrayList<>();

    public ObservadorPartido(Partido partido) {
        if (partido == null) {
            throw new IllegalArgumentException("El partido es obligatorio");
        }
        this.partido = partido;
    }

    @Override
    public void actualizar(EventoElectoral evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento es obligatorio");
        }
        if (Objects.equals(partido.getNombre(), evento.partidoNombre())) {
            eventos.add(evento);
        }
    }

    public List<EventoElectoral> getEventos() {
        return List.copyOf(eventos);
    }

    public int totalRecibidos() {
        return eventos.size();
    }
}

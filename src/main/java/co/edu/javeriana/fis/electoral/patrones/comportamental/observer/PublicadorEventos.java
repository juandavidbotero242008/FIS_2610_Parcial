package co.edu.javeriana.fis.electoral.patrones.comportamental.observer;

import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

import java.util.ArrayList;
import java.util.List;

/**
 * Sujeto observable (patron <b>Observer</b>).
 *
 * <p>Mantiene la lista de observadores suscritos y les notifica los eventos
 * del proceso electoral. Los observadores concretos (auditoria, analitica y
 * partidos) se desacoplan del servicio que origina el evento.</p>
 */
public class PublicadorEventos {

    private final List<ObservadorElectoral> observadores = new ArrayList<>();

    /** Suscribe un observador; no se permite duplicados. */
    public void suscribir(ObservadorElectoral observador) {
        if (observador == null) {
            throw new IllegalArgumentException("El observador es obligatorio");
        }
        if (observadores.contains(observador)) {
            throw new ValidacionElectoralException("El observador ya esta suscrito");
        }
        observadores.add(observador);
    }

    /** Desuscribe un observador previamente registrado. */
    public void desuscribir(ObservadorElectoral observador) {
        if (observador == null) {
            throw new IllegalArgumentException("El observador es obligatorio");
        }
        if (!observadores.remove(observador)) {
            throw new ValidacionElectoralException("El observador no esta suscrito");
        }
    }

    /** Notifica el evento a todos los observadores suscritos. */
    public void publicar(EventoElectoral evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento es obligatorio");
        }
        // Se copia la lista para permitir que un observador se desuscriba al recibir.
        for (ObservadorElectoral observador : List.copyOf(observadores)) {
            observador.actualizar(evento);
        }
    }

    /** Cantidad de observadores suscritos. */
    public int totalObservadores() {
        return observadores.size();
    }
}

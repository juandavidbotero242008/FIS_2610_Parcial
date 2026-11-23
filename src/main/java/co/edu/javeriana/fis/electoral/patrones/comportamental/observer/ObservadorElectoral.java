package co.edu.javeriana.fis.electoral.patrones.comportamental.observer;

/** Observador que reacciona a los eventos del proceso electoral. */
public interface ObservadorElectoral {

    /** Recibe una notificacion del publicador de eventos. */
    void actualizar(EventoElectoral evento);
}

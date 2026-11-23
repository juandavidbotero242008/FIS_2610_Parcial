package co.edu.javeriana.fis.electoral.excepcion;

/**
 * Se lanza cuando se intenta una transicion no permitida en el ciclo de vida
 * de un proceso electoral o de una mesa (patron State).
 */
public class TransicionEstadoInvalidaException extends ElectoralException {

    public TransicionEstadoInvalidaException(String mensaje) {
        super(mensaje);
    }
}

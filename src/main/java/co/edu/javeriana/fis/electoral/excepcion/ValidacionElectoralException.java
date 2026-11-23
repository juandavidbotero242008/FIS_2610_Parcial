package co.edu.javeriana.fis.electoral.excepcion;

/**
 * Se lanza cuando una regla de negocio electoral se incumple.
 *
 * <p>Ejemplos: registrar dos veces el mismo candidato en un partido, asignar
 * un jurado dos veces a la misma mesa o consignar en un acta mas votos que el
 * censo de la mesa.</p>
 */
public class ValidacionElectoralException extends ElectoralException {

    public ValidacionElectoralException(String mensaje) {
        super(mensaje);
    }
}

package co.edu.javeriana.fis.electoral.patrones.creacional.factory;

import co.edu.javeriana.fis.electoral.dominio.Acta;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

/**
 * Validaciones comunes a todos los tipos de eleccion.
 *
 * <p>Independientemente del tipo, el acta debe existir y no puede consignar
 * mas votos que el censo de la mesa. Cada subclase agrega su regla propia.</p>
 */
public abstract class ValidadorActaBase implements ValidadorActa {

    @Override
    public final void validar(Acta acta) {
        if (acta == null) {
            throw new IllegalArgumentException("El acta es obligatoria");
        }
        if (acta.getMesa() == null) {
            throw new ValidacionElectoralException("El acta no tiene mesa asociada");
        }
        int total = acta.totalVotos();
        if (total < 0) {
            throw new ValidacionElectoralException("El total de votos no puede ser negativo");
        }
        if (total > acta.getMesa().getCenso()) {
            throw new ValidacionElectoralException(
                    "El acta registra " + total + " votos y la mesa solo tiene censo "
                            + acta.getMesa().getCenso());
        }
        validarReglaEspecifica(acta);
    }

    /** Regla que varia segun el tipo de eleccion. */
    protected abstract void validarReglaEspecifica(Acta acta);
}

package co.edu.javeriana.fis.electoral.patrones.comportamental.state;

import co.edu.javeriana.fis.electoral.dominio.ProcesoElectoral;
import co.edu.javeriana.fis.electoral.excepcion.TransicionEstadoInvalidaException;

/**
 * Implementacion base de los estados.
 *
 * <p>Por defecto toda transicion se rechaza; cada estado concreto habilita
 * unicamente las que corresponden a su etapa del calendario electoral.</p>
 */
public abstract class EstadoProcesoBase implements EstadoProceso {

    protected TransicionEstadoInvalidaException transicionInvalida(String accion, ProcesoElectoral proceso) {
        return new TransicionEstadoInvalidaException(
                "No se puede " + accion + " un proceso en estado " + proceso.estadoActual());
    }

    @Override
    public void iniciar(ProcesoElectoral proceso) {
        throw transicionInvalida("iniciar", proceso);
    }

    @Override
    public void pasarAEscrutinio(ProcesoElectoral proceso) {
        throw transicionInvalida("pasar a escrutinio", proceso);
    }

    @Override
    public void cerrar(ProcesoElectoral proceso) {
        throw transicionInvalida("cerrar", proceso);
    }

    @Override
    public void cancelar(ProcesoElectoral proceso) {
        throw transicionInvalida("cancelar", proceso);
    }
}

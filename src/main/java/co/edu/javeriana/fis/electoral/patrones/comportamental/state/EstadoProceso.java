package co.edu.javeriana.fis.electoral.patrones.comportamental.state;

import co.edu.javeriana.fis.electoral.dominio.ProcesoElectoral;

/**
 * Estado del ciclo de vida de un proceso electoral (patron <b>State</b>).
 *
 * <p>Cada estado concreto decide que transiciones son validas. Asi, el
 * ProcesoElectoral no acumula condicionales y las reglas del calendario
 * electoral quedan encapsuladas en objetos especializados.</p>
 */
public interface EstadoProceso {

    /** Nombre del estado para reportes y trazabilidad. */
    String nombre();

    /** Transicion PROGRAMADO -> EN_CURSO. */
    void iniciar(ProcesoElectoral proceso);

    /** Transicion EN_CURSO -> ESCRUTINIO. */
    void pasarAEscrutinio(ProcesoElectoral proceso);

    /** Transicion ESCRUTINIO -> CERRADO. */
    void cerrar(ProcesoElectoral proceso);

    /** Cancelacion del proceso desde un estado que lo permita. */
    void cancelar(ProcesoElectoral proceso);
}

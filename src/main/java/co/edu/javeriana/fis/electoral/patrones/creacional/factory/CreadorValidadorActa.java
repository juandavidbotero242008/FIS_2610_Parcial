package co.edu.javeriana.fis.electoral.patrones.creacional.factory;

import co.edu.javeriana.fis.electoral.dominio.Acta;

/**
 * Creador (patron <b>Factory Method</b>).
 *
 * <p>Define el metodo fabrica {@link #crearValidador()} y un metodo de
 * plantilla {@link #validar(Acta)} que lo utiliza. Las subclases deciden que
 * validador concreto se instancia segun el tipo de eleccion, de modo que el
 * servicio de registro de actas nunca depende de clases concretas.</p>
 */
public abstract class CreadorValidadorActa {

    /** Metodo fabrica: cada subclase produce su validador. */
    public abstract ValidadorActa crearValidador();

    /** Valida el acta usando el validador producido por el metodo fabrica. */
    public void validar(Acta acta) {
        crearValidador().validar(acta);
    }
}

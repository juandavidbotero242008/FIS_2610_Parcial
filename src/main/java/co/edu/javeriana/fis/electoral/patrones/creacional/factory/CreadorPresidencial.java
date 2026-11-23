package co.edu.javeriana.fis.electoral.patrones.creacional.factory;

/** Creador de validadores para elecciones presidenciales. */
public class CreadorPresidencial extends CreadorValidadorActa {

    @Override
    public ValidadorActa crearValidador() {
        return new ValidadorPresidencial();
    }
}

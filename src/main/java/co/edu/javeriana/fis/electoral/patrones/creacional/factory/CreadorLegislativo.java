package co.edu.javeriana.fis.electoral.patrones.creacional.factory;

/** Creador de validadores para elecciones legislativas. */
public class CreadorLegislativo extends CreadorValidadorActa {

    @Override
    public ValidadorActa crearValidador() {
        return new ValidadorLegislativo();
    }
}

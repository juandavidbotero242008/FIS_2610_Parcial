package co.edu.javeriana.fis.electoral.patrones.creacional.factory;

/** Creador de validadores para consultas populares. */
public class CreadorConsulta extends CreadorValidadorActa {

    @Override
    public ValidadorActa crearValidador() {
        return new ValidadorConsulta();
    }
}

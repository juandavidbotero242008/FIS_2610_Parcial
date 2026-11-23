package co.edu.javeriana.fis.electoral.excepcion;

/**
 * Excepcion base del dominio electoral.
 *
 * <p>Permite distinguir los errores propios del negocio electoral de los
 * errores genericos de Java (NullPointerException, IllegalArgumentException,
 * etc.). Todas las excepciones del sistema heredan de esta clase para que la
 * capa de servicios pueda capturar el dominio de forma uniforme.</p>
 */
public class ElectoralException extends RuntimeException {

    public ElectoralException(String mensaje) {
        super(mensaje);
    }

    public ElectoralException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

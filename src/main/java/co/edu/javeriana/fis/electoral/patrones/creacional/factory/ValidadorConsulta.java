package co.edu.javeriana.fis.electoral.patrones.creacional.factory;

import co.edu.javeriana.fis.electoral.dominio.Acta;

/**
 * Reglas para consultas populares: se permite un acta sin votos, porque la
 * abstención total es un resultado valido de la consulta. Solo aplican las
 * validaciones comunes.
 */
public class ValidadorConsulta extends ValidadorActaBase {

    @Override
    protected void validarReglaEspecifica(Acta acta) {
        // Sin regla adicional: la consulta admite cero votos.
    }
}

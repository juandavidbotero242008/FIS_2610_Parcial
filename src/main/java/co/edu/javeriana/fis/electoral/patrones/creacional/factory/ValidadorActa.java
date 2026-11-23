package co.edu.javeriana.fis.electoral.patrones.creacional.factory;

import co.edu.javeriana.fis.electoral.dominio.Acta;

/** Contrato de validacion de un acta de escrutinio. */
public interface ValidadorActa {

    /**
     * Valida el acta segun las reglas del tipo de eleccion.
     *
     * @throws co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException si el acta es invalida
     */
    void validar(Acta acta);
}

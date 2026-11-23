package co.edu.javeriana.fis.electoral.patrones.creacional.factory;

import co.edu.javeriana.fis.electoral.dominio.Acta;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

/**
 * Reglas para elecciones presidenciales: el acta debe reflejar una contienda,
 * es decir, al menos dos candidatos con votos.
 */
public class ValidadorPresidencial extends ValidadorActaBase {

    @Override
    protected void validarReglaEspecifica(Acta acta) {
        long conVotos = acta.getResultados().values().stream().filter(votos -> votos > 0).count();
        if (conVotos < 2) {
            throw new ValidacionElectoralException(
                    "Un acta presidencial debe registrar al menos dos candidatos con votos");
        }
    }
}

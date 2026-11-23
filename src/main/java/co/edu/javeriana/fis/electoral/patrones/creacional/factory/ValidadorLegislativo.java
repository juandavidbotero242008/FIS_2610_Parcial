package co.edu.javeriana.fis.electoral.patrones.creacional.factory;

import co.edu.javeriana.fis.electoral.dominio.Acta;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

/**
 * Reglas para elecciones legislativas: el acta debe consignar al menos un
 * candidato con votos, porque la circunscripcion siempre tiene candidatos.
 */
public class ValidadorLegislativo extends ValidadorActaBase {

    @Override
    protected void validarReglaEspecifica(Acta acta) {
        boolean tieneVotos = acta.getResultados().values().stream().anyMatch(votos -> votos > 0);
        if (!tieneVotos) {
            throw new ValidacionElectoralException(
                    "Un acta legislativa debe registrar al menos un candidato con votos");
        }
    }
}

package co.edu.javeriana.fis.electoral.patrones.creacional.factory;

import co.edu.javeriana.fis.electoral.dominio.enums.TipoEleccion;

/**
 * Selector del creador concreto segun el tipo de eleccion.
 *
 * <p>Centraliza el unico punto donde se decide que familia de validadores se
 * usa. Si en el futuro aparece un nuevo tipo de eleccion, se agrega un creador
 * y una rama en el switch, sin modificar el resto del sistema.</p>
 */
public final class FabricaValidadores {

    private FabricaValidadores() {
    }

    public static CreadorValidadorActa para(TipoEleccion tipoEleccion) {
        if (tipoEleccion == null) {
            throw new IllegalArgumentException("El tipo de eleccion es obligatorio");
        }
        return switch (tipoEleccion) {
            case LEGISLATIVA -> new CreadorLegislativo();
            case PRESIDENCIAL -> new CreadorPresidencial();
            case CONSULTA_POPULAR -> new CreadorConsulta();
        };
    }
}

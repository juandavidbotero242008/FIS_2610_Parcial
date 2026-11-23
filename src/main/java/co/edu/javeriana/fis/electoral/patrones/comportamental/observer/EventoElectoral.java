package co.edu.javeriana.fis.electoral.patrones.comportamental.observer;

/**
 * Evento electoral publicado a los observadores.
 *
 * @param tipo          tipo de evento
 * @param referencia    identificador del acta, mesa o reclamacion
 * @param detalle       descripcion legible del evento
 * @param partidoNombre partido interesado, si aplica; {@code null} en caso contrario
 */
public record EventoElectoral(TipoEvento tipo, String referencia, String detalle, String partidoNombre) {

    public EventoElectoral {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de evento es obligatorio");
        }
        if (referencia == null || referencia.isBlank()) {
            throw new IllegalArgumentException("La referencia del evento es obligatoria");
        }
        if (detalle == null || detalle.isBlank()) {
            throw new IllegalArgumentException("El detalle del evento es obligatorio");
        }
    }
}

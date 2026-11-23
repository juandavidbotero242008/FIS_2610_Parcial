package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Candidato que participa en la contienda electoral.
 *
 * <p>Un candidato puede recibir el respaldo de uno o varios partidos. La regla
 * de negocio exige que conserve al menos un partido mientras su candidatura
 * este activa, para que siempre sea trazable la organizacion politica que lo
 * postula.</p>
 */
public class Candidato {

    private final String documento;
    private final String nombre;
    private final List<Partido> partidos = new ArrayList<>();
    private boolean activo;

    public Candidato(String documento, String nombre) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("El documento del candidato es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del candidato es obligatorio");
        }
        this.documento = documento;
        this.nombre = nombre;
        this.activo = true;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public List<Partido> getPartidos() {
        return List.copyOf(partidos);
    }

    /** Agrega un partido que respalda la candidatura. */
    public void agregarPartido(Partido partido) {
        if (partido == null) {
            throw new IllegalArgumentException("El partido es obligatorio");
        }
        if (partidos.contains(partido)) {
            throw new ValidacionElectoralException(
                    "El partido " + partido.getNombre() + " ya respalda al candidato");
        }
        partidos.add(partido);
    }

    /**
     * Retira el respaldo de un partido.
     *
     * @throws ValidacionElectoralException si el partido no respalda al candidato o
     *                                      si es el ultimo respaldo de una candidatura activa
     */
    public void retirarPartido(Partido partido) {
        if (partido == null) {
            throw new IllegalArgumentException("El partido es obligatorio");
        }
        if (!partidos.contains(partido)) {
            throw new ValidacionElectoralException(
                    "El partido " + partido.getNombre() + " no respalda al candidato");
        }
        if (partidos.size() == 1 && activo) {
            throw new ValidacionElectoralException(
                    "Un candidato activo debe conservar al menos un partido que lo respalde");
        }
        partidos.remove(partido);
    }

    /** Indica si el partido respalda actualmente al candidato. */
    public boolean estaRespaldadoPor(Partido partido) {
        if (partido == null) {
            throw new IllegalArgumentException("El partido es obligatorio");
        }
        return partidos.contains(partido);
    }

    /** Retira la candidatura de la contienda. */
    public void retirarCandidatura() {
        if (!activo) {
            throw new ValidacionElectoralException("El candidato ya se encuentra retirado");
        }
        activo = false;
    }

    /** Reactiva una candidatura previamente retirada. */
    public void reactivarCandidatura() {
        if (activo) {
            throw new ValidacionElectoralException("El candidato ya se encuentra activo");
        }
        activo = true;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Candidato candidato)) {
            return false;
        }
        return Objects.equals(documento, candidato.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documento);
    }

    @Override
    public String toString() {
        return "Candidato{" + "documento='" + documento + '\'' + ", nombre='" + nombre + '\'' + '}';
    }
}

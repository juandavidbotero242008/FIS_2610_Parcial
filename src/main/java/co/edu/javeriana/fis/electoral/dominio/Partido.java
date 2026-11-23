package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Partido politico que participa en el proceso electoral.
 *
 * <p>Mantiene la lista de candidatos que respalda. La relacion se sincroniza
 * en ambos sentidos para que las consultas por partido y por candidato sean
 * consistentes.</p>
 */
public class Partido {

    private final String id;
    private final String nombre;
    private final List<Candidato> candidatos = new ArrayList<>();

    public Partido(String id, String nombre) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del partido es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del partido es obligatorio");
        }
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Candidato> getCandidatos() {
        return List.copyOf(candidatos);
    }

    /** Vincula un candidato al partido, en ambas direcciones. */
    public void agregarCandidato(Candidato candidato) {
        if (candidato == null) {
            throw new IllegalArgumentException("El candidato es obligatorio");
        }
        if (candidatos.contains(candidato)) {
            throw new ValidacionElectoralException(
                    "El candidato " + candidato.getNombre() + " ya esta vinculado al partido");
        }
        candidato.agregarPartido(this);
        candidatos.add(candidato);
    }

    /**
     * Desvincula un candidato del partido.
     *
     * @throws ValidacionElectoralException si el candidato no pertenece al partido o
     *                                      si es el ultimo respaldo de una candidatura activa
     */
    public void retirarCandidato(Candidato candidato) {
        if (candidato == null) {
            throw new IllegalArgumentException("El candidato es obligatorio");
        }
        if (!candidatos.contains(candidato)) {
            throw new ValidacionElectoralException(
                    "El candidato " + candidato.getNombre() + " no pertenece al partido");
        }
        candidato.retirarPartido(this);
        candidatos.remove(candidato);
    }

    /** Numero de candidatos vinculados al partido. */
    public int numeroCandidatos() {
        return candidatos.size();
    }

    /** Indica si el candidato esta vinculado al partido. */
    public boolean tieneCandidato(Candidato candidato) {
        if (candidato == null) {
            throw new IllegalArgumentException("El candidato es obligatorio");
        }
        return candidatos.contains(candidato);
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Partido partido)) {
            return false;
        }
        return Objects.equals(id, partido.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Partido{" + "id='" + id + '\'' + ", nombre='" + nombre + '\'' + '}';
    }
}

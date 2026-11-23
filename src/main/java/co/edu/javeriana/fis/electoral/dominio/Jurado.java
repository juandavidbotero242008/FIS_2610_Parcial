package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.dominio.enums.EstadoMesa;
import co.edu.javeriana.fis.electoral.dominio.enums.FuncionJurado;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Ciudadano designado como jurado de votacion.
 *
 * <p>Conoce las mesas a las que fue asignado y la funcion que cumple en cada
 * una. Un jurado no puede estar dos veces en la misma mesa ni ser asignado a
 * una mesa ya cerrada.</p>
 */
public class Jurado {

    private final String documento;
    private final String nombre;
    private String contacto;
    private final List<AsignacionJurado> asignaciones = new ArrayList<>();

    public Jurado(String documento, String nombre, String contacto) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("El documento del jurado es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del jurado es obligatorio");
        }
        if (contacto == null || contacto.isBlank()) {
            throw new IllegalArgumentException("El contacto del jurado es obligatorio");
        }
        this.documento = documento;
        this.nombre = nombre;
        this.contacto = contacto;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContacto() {
        return contacto;
    }

    public List<AsignacionJurado> getAsignaciones() {
        return List.copyOf(asignaciones);
    }

    /**
     * Asigna el jurado a una mesa con una funcion.
     *
     * @throws ValidacionElectoralException si ya esta asignado a la mesa o si la mesa esta cerrada
     */
    public void asignar(Mesa mesa, FuncionJurado funcion) {
        if (mesa == null) {
            throw new IllegalArgumentException("La mesa es obligatoria");
        }
        if (funcion == null) {
            throw new IllegalArgumentException("La funcion es obligatoria");
        }
        if (mesa.getEstado() == EstadoMesa.CERRADA) {
            throw new ValidacionElectoralException("No se puede asignar un jurado a una mesa cerrada");
        }
        boolean yaAsignado = asignaciones.stream().anyMatch(asignacion -> asignacion.mesa().equals(mesa));
        if (yaAsignado) {
            throw new ValidacionElectoralException(
                    "El jurado ya esta asignado a la mesa " + mesa.getNumero());
        }
        asignaciones.add(new AsignacionJurado(mesa, funcion));
    }

    /** Retira al jurado de una mesa. */
    public void liberar(Mesa mesa) {
        if (mesa == null) {
            throw new IllegalArgumentException("La mesa es obligatoria");
        }
        boolean removida = asignaciones.removeIf(asignacion -> asignacion.mesa().equals(mesa));
        if (!removida) {
            throw new ValidacionElectoralException(
                    "El jurado no esta asignado a la mesa " + mesa.getNumero());
        }
    }

    /** Funcion que cumple el jurado en una mesa, si esta asignado. */
    public Optional<FuncionJurado> funcionEn(Mesa mesa) {
        if (mesa == null) {
            throw new IllegalArgumentException("La mesa es obligatoria");
        }
        return asignaciones.stream()
                .filter(asignacion -> asignacion.mesa().equals(mesa))
                .map(AsignacionJurado::funcion)
                .findFirst();
    }

    /** Numero de mesas asignadas. */
    public int totalAsignaciones() {
        return asignaciones.size();
    }

    /** Actualiza el dato de contacto del jurado. */
    public void actualizarContacto(String contacto) {
        if (contacto == null || contacto.isBlank()) {
            throw new IllegalArgumentException("El contacto no puede estar vacio");
        }
        this.contacto = contacto;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Jurado jurado)) {
            return false;
        }
        return Objects.equals(documento, jurado.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documento);
    }

    @Override
    public String toString() {
        return "Jurado{" + "documento='" + documento + '\'' + ", nombre='" + nombre + '\'' + '}';
    }
}

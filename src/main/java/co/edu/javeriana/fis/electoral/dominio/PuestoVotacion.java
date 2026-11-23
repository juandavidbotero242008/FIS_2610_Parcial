package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Puesto de votacion (nodo compuesto del Composite).
 *
 * <p>Agrupa las mesas habilitadas en un lugar fisico de un municipio. Aplica
 * las validaciones de composicion: solo admite mesas, no admite duplicados y
 * mantiene la relacion bidireccional mesa-puesto.</p>
 */
public class PuestoVotacion implements UnidadTerritorial {

    private final String id;
    private final String nombre;
    private final String direccion;
    private final Municipio municipio;
    private final List<Mesa> mesas = new ArrayList<>();

    public PuestoVotacion(String id, String nombre, String direccion, Municipio municipio) {
        this.id = requerido(id, "El id del puesto es obligatorio");
        this.nombre = requerido(nombre, "El nombre del puesto es obligatorio");
        this.direccion = requerido(direccion, "La direccion del puesto es obligatoria");
        if (municipio == null) {
            throw new IllegalArgumentException("El municipio del puesto es obligatorio");
        }
        this.municipio = municipio;
    }

    public String getId() {
        return id;
    }

    public String getDireccion() {
        return direccion;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    @Override
    public void agregar(UnidadTerritorial unidad) {
        if (!(unidad instanceof Mesa mesa)) {
            throw new ValidacionElectoralException(
                    "Un puesto de votacion solo puede contener mesas");
        }
        if (mesas.contains(mesa)) {
            throw new ValidacionElectoralException(
                    "La mesa " + mesa.getNumero() + " ya existe en el puesto");
        }
        mesa.vincularPuesto(this);
        mesas.add(mesa);
    }

    @Override
    public void remover(UnidadTerritorial unidad) {
        if (!(unidad instanceof Mesa mesa)) {
            throw new ValidacionElectoralException(
                    "Un puesto de votacion solo puede contener mesas");
        }
        if (!mesas.remove(mesa)) {
            throw new ValidacionElectoralException(
                    "La mesa " + mesa.getNumero() + " no pertenece al puesto");
        }
    }

    /** Busca una mesa por su numero dentro del puesto. */
    public Optional<Mesa> buscarMesa(int numero) {
        return mesas.stream().filter(mesa -> mesa.getNumero() == numero).findFirst();
    }

    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public int censoTotal() {
        return mesas.stream().mapToInt(Mesa::getCenso).sum();
    }

    @Override
    public int totalVotos() {
        return mesas.stream().mapToInt(Mesa::totalVotos).sum();
    }

    @Override
    public List<Mesa> mesas() {
        return List.copyOf(mesas);
    }

    @Override
    public List<? extends UnidadTerritorial> hijos() {
        return List.copyOf(mesas);
    }

    private static String requerido(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof PuestoVotacion puesto)) {
            return false;
        }
        return Objects.equals(id, puesto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PuestoVotacion{" + "id='" + id + '\'' + ", nombre='" + nombre + '\'' + '}';
    }
}

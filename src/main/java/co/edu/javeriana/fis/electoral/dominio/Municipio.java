package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Municipio (nodo compuesto del Composite territorial).
 *
 * <p>Contiene puestos de votacion y pertenece a un departamento. El total de
 * censo y votos se delega en los hijos, de modo que la consolidacion no
 * necesita conocer si se consulta un municipio, un puesto o una mesa.</p>
 */
public class Municipio implements UnidadTerritorial {

    private final String codigo;
    private final String nombre;
    private Departamento departamento;
    private final List<PuestoVotacion> puestos = new ArrayList<>();

    public Municipio(String codigo, String nombre, Departamento departamento) {
        this.codigo = requerido(codigo, "El codigo del municipio es obligatorio");
        this.nombre = requerido(nombre, "El nombre del municipio es obligatorio");
        if (departamento == null) {
            throw new IllegalArgumentException("El departamento del municipio es obligatorio");
        }
        this.departamento = departamento;
    }

    public String getCodigo() {
        return codigo;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    void vincularDepartamento(Departamento departamento) {
        if (departamento == null) {
            throw new IllegalArgumentException("El departamento no puede ser nulo");
        }
        if (this.departamento != departamento) {
            throw new ValidacionElectoralException("El municipio ya pertenece a otro departamento");
        }
        this.departamento = departamento;
    }

    @Override
    public void agregar(UnidadTerritorial unidad) {
        if (!(unidad instanceof PuestoVotacion puesto)) {
            throw new ValidacionElectoralException(
                    "Un municipio solo puede contener puestos de votacion");
        }
        if (puestos.contains(puesto)) {
            throw new ValidacionElectoralException(
                    "El puesto " + puesto.getId() + " ya existe en el municipio");
        }
        puestos.add(puesto);
    }

    @Override
    public void remover(UnidadTerritorial unidad) {
        if (!(unidad instanceof PuestoVotacion puesto)) {
            throw new ValidacionElectoralException(
                    "Un municipio solo puede contener puestos de votacion");
        }
        if (!puestos.remove(puesto)) {
            throw new ValidacionElectoralException(
                    "El puesto " + puesto.getId() + " no pertenece al municipio");
        }
    }

    /** Busca un puesto por su identificador dentro del municipio. */
    public Optional<PuestoVotacion> buscarPuesto(String id) {
        return puestos.stream().filter(puesto -> puesto.getId().equals(id)).findFirst();
    }

    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public int censoTotal() {
        return puestos.stream().mapToInt(PuestoVotacion::censoTotal).sum();
    }

    @Override
    public int totalVotos() {
        return puestos.stream().mapToInt(PuestoVotacion::totalVotos).sum();
    }

    @Override
    public List<Mesa> mesas() {
        return puestos.stream().flatMap(puesto -> puesto.mesas().stream()).toList();
    }

    @Override
    public List<? extends UnidadTerritorial> hijos() {
        return List.copyOf(puestos);
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
        if (!(otro instanceof Municipio municipio)) {
            return false;
        }
        return Objects.equals(codigo, municipio.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Municipio{" + "codigo='" + codigo + '\'' + ", nombre='" + nombre + '\'' + '}';
    }
}

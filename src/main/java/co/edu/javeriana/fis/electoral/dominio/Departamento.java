package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Departamento (nodo raiz del Composite territorial).
 *
 * <p>Contiene municipios y es el nivel que se usa para comparar resultados
 * entre regiones. Al ser un compuesto, su censo y sus votos son la suma de
 * todos los niveles inferiores.</p>
 */
public class Departamento implements UnidadTerritorial {

    private final String codigo;
    private final String nombre;
    private final List<Municipio> municipios = new ArrayList<>();

    public Departamento(String codigo, String nombre) {
        this.codigo = requerido(codigo, "El codigo del departamento es obligatorio");
        this.nombre = requerido(nombre, "El nombre del departamento es obligatorio");
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public void agregar(UnidadTerritorial unidad) {
        if (!(unidad instanceof Municipio municipio)) {
            throw new ValidacionElectoralException(
                    "Un departamento solo puede contener municipios");
        }
        if (municipios.contains(municipio)) {
            throw new ValidacionElectoralException(
                    "El municipio " + municipio.getCodigo() + " ya existe en el departamento");
        }
        municipio.vincularDepartamento(this);
        municipios.add(municipio);
    }

    @Override
    public void remover(UnidadTerritorial unidad) {
        if (!(unidad instanceof Municipio municipio)) {
            throw new ValidacionElectoralException(
                    "Un departamento solo puede contener municipios");
        }
        if (!municipios.remove(municipio)) {
            throw new ValidacionElectoralException(
                    "El municipio " + municipio.getCodigo() + " no pertenece al departamento");
        }
    }

    /** Busca un municipio por su codigo dentro del departamento. */
    public Optional<Municipio> buscarMunicipio(String codigo) {
        return municipios.stream().filter(municipio -> municipio.getCodigo().equals(codigo)).findFirst();
    }

    @Override
    public String nombre() {
        return nombre;
    }

    @Override
    public int censoTotal() {
        return municipios.stream().mapToInt(Municipio::censoTotal).sum();
    }

    @Override
    public int totalVotos() {
        return municipios.stream().mapToInt(Municipio::totalVotos).sum();
    }

    @Override
    public List<Mesa> mesas() {
        return municipios.stream()
                .flatMap(municipio -> municipio.mesas().stream())
                .toList();
    }

    @Override
    public List<? extends UnidadTerritorial> hijos() {
        return List.copyOf(municipios);
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
        if (!(otro instanceof Departamento departamento)) {
            return false;
        }
        return Objects.equals(codigo, departamento.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Departamento{" + "codigo='" + codigo + '\'' + ", nombre='" + nombre + '\'' + '}';
    }
}

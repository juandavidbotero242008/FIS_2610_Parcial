package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.dominio.enums.TipoEleccion;
import co.edu.javeriana.fis.electoral.excepcion.TransicionEstadoInvalidaException;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import co.edu.javeriana.fis.electoral.patrones.comportamental.state.EstadoProceso;
import co.edu.javeriana.fis.electoral.patrones.comportamental.state.EstadoProgramado;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Proceso electoral (contexto del patron <b>State</b>).
 *
 * <p>El proceso delega las transiciones de su ciclo de vida en el estado
 * actual (Programado, En curso, Escrutinio, Cerrado o Cancelado). Ademas es la
 * raiz de la distribucion territorial, por lo que agrega departamentos y
 * consolida sus resultados mediante el Composite.</p>
 */
public class ProcesoElectoral {

    private final String id;
    private final String nombre;
    private final TipoEleccion tipoEleccion;
    private final LocalDate fecha;
    private final List<Departamento> departamentos = new ArrayList<>();
    private EstadoProceso estado;

    public ProcesoElectoral(String id, String nombre, TipoEleccion tipoEleccion, LocalDate fecha) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del proceso es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del proceso es obligatorio");
        }
        if (tipoEleccion == null) {
            throw new IllegalArgumentException("El tipo de eleccion es obligatorio");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha del proceso es obligatoria");
        }
        this.id = id;
        this.nombre = nombre;
        this.tipoEleccion = tipoEleccion;
        this.fecha = fecha;
        this.estado = new EstadoProgramado();
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoEleccion getTipoEleccion() {
        return tipoEleccion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public EstadoProceso getEstado() {
        return estado;
    }

    /** Nombre del estado actual, util para reportes y pruebas. */
    public String estadoActual() {
        return estado.nombre();
    }

    /** Cambia el estado actual. Uso interno del patron State. */
    public void establecerEstado(EstadoProceso nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        this.estado = nuevoEstado;
    }

    public void iniciar() {
        estado.iniciar(this);
    }

    public void pasarAEscrutinio() {
        estado.pasarAEscrutinio(this);
    }

    public void cerrar() {
        estado.cerrar(this);
    }

    public void cancelar() {
        estado.cancelar(this);
    }

    /** Agrega un departamento a la distribucion territorial (solo en PROGRAMADO). */
    public void agregarTerritorio(Departamento departamento) {
        if (departamento == null) {
            throw new IllegalArgumentException("El departamento es obligatorio");
        }
        if (!(estado instanceof EstadoProgramado)) {
            throw new TransicionEstadoInvalidaException(
                    "Solo se puede configurar el territorio en estado PROGRAMADO; estado actual: "
                            + estado.nombre());
        }
        if (departamentos.contains(departamento)) {
            throw new ValidacionElectoralException(
                    "El departamento " + departamento.getCodigo() + " ya esta registrado en el proceso");
        }
        departamentos.add(departamento);
    }

    /** Remueve un departamento de la distribucion territorial (solo en PROGRAMADO). */
    public void removerTerritorio(Departamento departamento) {
        if (departamento == null) {
            throw new IllegalArgumentException("El departamento es obligatorio");
        }
        if (!(estado instanceof EstadoProgramado)) {
            throw new TransicionEstadoInvalidaException(
                    "Solo se puede configurar el territorio en estado PROGRAMADO; estado actual: "
                            + estado.nombre());
        }
        if (!departamentos.remove(departamento)) {
            throw new ValidacionElectoralException(
                    "El departamento " + departamento.getCodigo() + " no pertenece al proceso");
        }
    }

    /** Busca un departamento por codigo dentro del proceso. */
    public Optional<Departamento> buscarDepartamento(String codigo) {
        return departamentos.stream().filter(departamento -> departamento.getCodigo().equals(codigo)).findFirst();
    }

    public List<Departamento> getDepartamentos() {
        return List.copyOf(departamentos);
    }

    public List<Mesa> mesas() {
        return departamentos.stream()
                .flatMap(departamento -> departamento.mesas().stream())
                .toList();
    }

    public int censoTotal() {
        return departamentos.stream().mapToInt(Departamento::censoTotal).sum();
    }

    public int totalVotos() {
        return departamentos.stream().mapToInt(Departamento::totalVotos).sum();
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof ProcesoElectoral proceso)) {
            return false;
        }
        return Objects.equals(id, proceso.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProcesoElectoral{" + "id='" + id + '\'' + ", tipo=" + tipoEleccion
                + ", estado=" + estado.nombre() + '}';
    }
}

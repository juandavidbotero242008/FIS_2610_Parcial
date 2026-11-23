package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.dominio.enums.EstadoMesa;
import co.edu.javeriana.fis.electoral.excepcion.TransicionEstadoInvalidaException;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

import java.util.List;
import java.util.Objects;

/**
 * Mesa de votacion (hoja del Composite territorial).
 *
 * <p>Una mesa conoce su numero, su censo, el puesto al que pertenece y el acta
 * que se levanta al cierre de la jornada. El ciclo de vida de la mesa se
 * controla con transiciones explicitas: PENDIENTE -> INSTALADA -> CERRADA.</p>
 */
public class Mesa implements UnidadTerritorial {

    private final int numero;
    private final int censo;
    private EstadoMesa estado;
    private PuestoVotacion puesto;
    private Acta acta;

    public Mesa(int numero, int censo) {
        if (numero <= 0) {
            throw new IllegalArgumentException("El numero de mesa debe ser positivo");
        }
        if (censo <= 0) {
            throw new IllegalArgumentException("El censo de la mesa debe ser positivo");
        }
        this.numero = numero;
        this.censo = censo;
        this.estado = EstadoMesa.PENDIENTE;
    }

    public int getNumero() {
        return numero;
    }

    public int getCenso() {
        return censo;
    }

    public EstadoMesa getEstado() {
        return estado;
    }

    public PuestoVotacion getPuesto() {
        return puesto;
    }

    public Acta getActa() {
        return acta;
    }

    /** Instala la mesa el dia de la jornada. Solo es posible desde PENDIENTE. */
    public void instalar() {
        if (estado != EstadoMesa.PENDIENTE) {
            throw new TransicionEstadoInvalidaException(
                    "Solo una mesa PENDIENTE puede instalarse; estado actual: " + estado);
        }
        estado = EstadoMesa.INSTALADA;
    }

    /** Cierra la mesa para iniciar el escrutinio. Solo es posible desde INSTALADA. */
    public void cerrar() {
        if (estado != EstadoMesa.INSTALADA) {
            throw new TransicionEstadoInvalidaException(
                    "Solo una mesa INSTALADA puede cerrarse; estado actual: " + estado);
        }
        estado = EstadoMesa.CERRADA;
    }

    /**
     * Registra el acta de cierre de la mesa.
     *
     * <p>Reglas: la mesa debe estar cerrada, el acta debe estar cerrada, debe
     * corresponder a esta mesa y no puede existir un acta previa.</p>
     */
    public void registrarActa(Acta acta) {
        if (acta == null) {
            throw new IllegalArgumentException("El acta no puede ser nula");
        }
        if (estado != EstadoMesa.CERRADA) {
            throw new TransicionEstadoInvalidaException(
                    "Solo se registra acta en una mesa CERRADA; estado actual: " + estado);
        }
        if (acta.getMesa() != this) {
            throw new ValidacionElectoralException("El acta no corresponde a esta mesa");
        }
        if (!acta.estaCerrada()) {
            throw new ValidacionElectoralException("El acta debe estar cerrada antes de registrarse");
        }
        if (this.acta != null) {
            throw new ValidacionElectoralException("La mesa ya tiene un acta registrada");
        }
        this.acta = acta;
    }

    void vincularPuesto(PuestoVotacion puesto) {
        if (puesto == null) {
            throw new IllegalArgumentException("El puesto no puede ser nulo");
        }
        if (this.puesto != null && this.puesto != puesto) {
            throw new ValidacionElectoralException("La mesa ya pertenece a otro puesto de votacion");
        }
        this.puesto = puesto;
    }

    @Override
    public String nombre() {
        return "Mesa " + numero;
    }

    @Override
    public int censoTotal() {
        return censo;
    }

    @Override
    public int totalVotos() {
        return acta == null ? 0 : acta.totalVotos();
    }

    @Override
    public List<Mesa> mesas() {
        return List.of(this);
    }

    @Override
    public void agregar(UnidadTerritorial unidad) {
        throw new ValidacionElectoralException("Una mesa es una hoja y no puede contener unidades hijas");
    }

    @Override
    public void remover(UnidadTerritorial unidad) {
        throw new ValidacionElectoralException("Una mesa es una hoja y no puede contener unidades hijas");
    }

    @Override
    public List<? extends UnidadTerritorial> hijos() {
        return List.of();
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Mesa mesa)) {
            return false;
        }
        return numero == mesa.numero && Objects.equals(puesto, mesa.puesto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, puesto);
    }

    @Override
    public String toString() {
        return "Mesa{" + "numero=" + numero + ", censo=" + censo + ", estado=" + estado + '}';
    }
}

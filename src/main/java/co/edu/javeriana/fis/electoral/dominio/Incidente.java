package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.dominio.enums.TipoIncidente;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Situacion presentada durante la jornada electoral.
 *
 * <p>Registra incidentes logisticos, de orden publico, irregularidades o
 * fallas tecnicas. Un incidente puede resolverse y reabrirse, dejando siempre
 * la observacion de cierre para efectos de auditoria.</p>
 */
public class Incidente {

    private final String id;
    private final TipoIncidente tipo;
    private final String descripcion;
    private final Mesa mesa;
    private final LocalDateTime fecha;
    private boolean resuelto;
    private String observacion;

    public Incidente(String id, TipoIncidente tipo, String descripcion, Mesa mesa) {
        this(id, tipo, descripcion, mesa, LocalDateTime.now());
    }

    public Incidente(String id, TipoIncidente tipo, String descripcion, Mesa mesa, LocalDateTime fecha) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del incidente es obligatorio");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de incidente es obligatorio");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripcion del incidente es obligatoria");
        }
        if (mesa == null) {
            throw new IllegalArgumentException("La mesa del incidente es obligatoria");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha del incidente es obligatoria");
        }
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.mesa = mesa;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public TipoIncidente getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public boolean estaResuelto() {
        return resuelto;
    }

    public String getObservacion() {
        return observacion;
    }

    /** Marca el incidente como resuelto. */
    public void resolver(String observacion) {
        if (resuelto) {
            throw new ValidacionElectoralException("El incidente ya se encuentra resuelto");
        }
        if (observacion == null || observacion.isBlank()) {
            throw new IllegalArgumentException("La observacion de cierre es obligatoria");
        }
        this.resuelto = true;
        this.observacion = observacion;
    }

    /** Reabre un incidente previamente resuelto. */
    public void reabrir() {
        if (!resuelto) {
            throw new ValidacionElectoralException("El incidente no esta resuelto y no puede reabrirse");
        }
        this.resuelto = false;
        this.observacion = null;
    }

    /** Un incidente de orden publico o una irregularidad se considera critico. */
    public boolean esCritico() {
        return tipo == TipoIncidente.ORDEN_PUBLICO || tipo == TipoIncidente.IRREGULARIDAD;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Incidente incidente)) {
            return false;
        }
        return Objects.equals(id, incidente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Incidente{" + "id='" + id + '\'' + ", tipo=" + tipo + ", resuelto=" + resuelto + '}';
    }
}

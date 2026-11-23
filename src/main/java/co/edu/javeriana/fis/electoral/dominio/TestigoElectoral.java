package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

import java.util.Objects;

/** Testigo electoral acreditado por un partido politico. */
public class TestigoElectoral {

    private final String documento;
    private final String nombre;
    private Partido partido;

    public TestigoElectoral(String documento, String nombre, Partido partido) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("El documento del testigo es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del testigo es obligatorio");
        }
        if (partido == null) {
            throw new IllegalArgumentException("El partido del testigo es obligatorio");
        }
        this.documento = documento;
        this.nombre = nombre;
        this.partido = partido;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public Partido getPartido() {
        return partido;
    }

    /** Cambia el partido que acredita al testigo. */
    public void cambiarPartido(Partido nuevoPartido) {
        if (nuevoPartido == null) {
            throw new IllegalArgumentException("El partido es obligatorio");
        }
        if (nuevoPartido.equals(this.partido)) {
            throw new ValidacionElectoralException("El testigo ya esta acreditado por ese partido");
        }
        this.partido = nuevoPartido;
    }

    /** Indica si el testigo esta acreditado por el partido indicado. */
    public boolean esDelPartido(Partido partido) {
        if (partido == null) {
            throw new IllegalArgumentException("El partido es obligatorio");
        }
        return this.partido.equals(partido);
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof TestigoElectoral testigo)) {
            return false;
        }
        return Objects.equals(documento, testigo.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documento);
    }

    @Override
    public String toString() {
        return "TestigoElectoral{" + "documento='" + documento + '\'' + ", nombre='" + nombre + '\'' + '}';
    }
}

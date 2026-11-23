package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.dominio.enums.FuncionJurado;

import java.util.Objects;

/** Asignacion de un jurado a una mesa con una funcion determinada. */
public record AsignacionJurado(Mesa mesa, FuncionJurado funcion) {

    public AsignacionJurado {
        if (mesa == null) {
            throw new IllegalArgumentException("La mesa de la asignacion es obligatoria");
        }
        if (funcion == null) {
            throw new IllegalArgumentException("La funcion del jurado es obligatoria");
        }
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof AsignacionJurado asignacion)) {
            return false;
        }
        return Objects.equals(mesa, asignacion.mesa) && funcion == asignacion.funcion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mesa, funcion);
    }
}

package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.dominio.enums.EstadoMesa;
import co.edu.javeriana.fis.electoral.dominio.enums.FuncionJurado;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Jurado: asignaciones a mesas y funciones")
class JuradoTest {

    @Test
    @DisplayName("constructor: rechaza datos obligatorios invalidos")
    void constructorInvalidoFalla() {
        assertThrows(IllegalArgumentException.class, () -> new Jurado("", "Ana", "ana@correo.co"));
        assertThrows(IllegalArgumentException.class, () -> new Jurado("123", " ", "ana@correo.co"));
        assertThrows(IllegalArgumentException.class, () -> new Jurado("123", "Ana", " "));
    }

    @Test
    @DisplayName("constructor: conserva los datos del jurado")
    void constructorConservaDatos() {
        Jurado jurado = new Jurado("123", "Ana", "ana@correo.co");
        assertEquals("123", jurado.getDocumento());
        assertEquals("Ana", jurado.getNombre());
        assertEquals("ana@correo.co", jurado.getContacto());
        assertTrue(jurado.getAsignaciones().isEmpty());
    }

    @Test
    @DisplayName("asignar: registra mesa y funcion")
    void asignarValido() {
        Jurado jurado = Fixtures.jurado("1");
        Mesa mesa = new Mesa(1, 100);
        jurado.asignar(mesa, FuncionJurado.PRESIDENTE);
        assertEquals(1, jurado.totalAsignaciones());
        assertEquals(FuncionJurado.PRESIDENTE, jurado.funcionEn(mesa).orElseThrow());
    }

    @Test
    @DisplayName("asignar: rechaza mesa nula")
    void asignarMesaNulaFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> Fixtures.jurado("1").asignar(null, FuncionJurado.VOCAL));
    }

    @Test
    @DisplayName("asignar: rechaza funcion nula")
    void asignarFuncionNulaFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> Fixtures.jurado("1").asignar(new Mesa(1, 100), null));
    }

    @Test
    @DisplayName("asignar: no permite dos asignaciones a la misma mesa")
    void asignarDuplicadoFalla() {
        Jurado jurado = Fixtures.jurado("1");
        Mesa mesa = new Mesa(1, 100);
        jurado.asignar(mesa, FuncionJurado.PRESIDENTE);
        assertThrows(ValidacionElectoralException.class,
                () -> jurado.asignar(mesa, FuncionJurado.VOCAL));
    }

    @Test
    @DisplayName("asignar: no permite asignar a una mesa cerrada")
    void asignarMesaCerradaFalla() {
        Mesa mesa = new Mesa(1, 100);
        mesa.instalar();
        mesa.cerrar();
        assertThrows(ValidacionElectoralException.class,
                () -> Fixtures.jurado("1").asignar(mesa, FuncionJurado.VOCAL));
    }

    @Test
    @DisplayName("liberar: elimina la asignacion de la mesa")
    void liberarValido() {
        Jurado jurado = Fixtures.jurado("1");
        Mesa mesa = new Mesa(1, 100);
        jurado.asignar(mesa, FuncionJurado.SECRETARIO);
        jurado.liberar(mesa);
        assertEquals(0, jurado.totalAsignaciones());
    }

    @Test
    @DisplayName("liberar: falla si el jurado no esta asignado a la mesa")
    void liberarSinAsignacionFalla() {
        assertThrows(ValidacionElectoralException.class,
                () -> Fixtures.jurado("1").liberar(new Mesa(1, 100)));
    }

    @Test
    @DisplayName("liberar: rechaza mesa nula")
    void liberarMesaNulaFalla() {
        assertThrows(IllegalArgumentException.class, () -> Fixtures.jurado("1").liberar(null));
    }

    @Test
    @DisplayName("funcionEn: retorna vacio si no esta asignado (caso limite)")
    void funcionEnSinAsignacion() {
        assertTrue(Fixtures.jurado("1").funcionEn(new Mesa(1, 100)).isEmpty());
    }

    @Test
    @DisplayName("funcionEn: rechaza mesa nula")
    void funcionEnMesaNulaFalla() {
        assertThrows(IllegalArgumentException.class, () -> Fixtures.jurado("1").funcionEn(null));
    }

    @Test
    @DisplayName("totalAsignaciones: sin mesas retorna cero (caso limite)")
    void totalAsignacionesVacio() {
        assertEquals(0, Fixtures.jurado("1").totalAsignaciones());
    }

    @Test
    @DisplayName("totalAsignaciones: cuenta varias mesas")
    void totalAsignacionesMultiples() {
        Jurado jurado = Fixtures.jurado("1");
        jurado.asignar(new Mesa(1, 100), FuncionJurado.PRESIDENTE);
        jurado.asignar(new Mesa(2, 100), FuncionJurado.VOCAL);
        assertEquals(2, jurado.totalAsignaciones());
    }

    @Test
    @DisplayName("actualizarContacto: cambia el dato de contacto")
    void actualizarContactoValido() {
        Jurado jurado = Fixtures.jurado("1");
        jurado.actualizarContacto("nuevo@correo.co");
        assertEquals("nuevo@correo.co", jurado.getContacto());
    }

    @Test
    @DisplayName("actualizarContacto: rechaza contacto vacio")
    void actualizarContactoVacioFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> Fixtures.jurado("1").actualizarContacto(" "));
    }

    @Test
    @DisplayName("asignar: una mesa cerrada por el estado es la unica restriccion de estado")
    void mesaInstaladaPermiteAsignacion() {
        Mesa mesa = new Mesa(1, 100);
        mesa.instalar();
        Jurado jurado = Fixtures.jurado("1");
        jurado.asignar(mesa, FuncionJurado.VOCAL);
        assertEquals(EstadoMesa.INSTALADA, mesa.getEstado());
        assertEquals(1, jurado.totalAsignaciones());
    }
}

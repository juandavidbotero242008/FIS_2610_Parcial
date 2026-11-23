package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.dominio.enums.EstadoMesa;
import co.edu.javeriana.fis.electoral.excepcion.TransicionEstadoInvalidaException;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Mesa: ciclo de vida, acta y agregacion")
class MesaTest {

    @Test
    @DisplayName("instalar: una mesa PENDIENTE pasa a INSTALADA")
    void instalarCambiaEstado() {
        Mesa mesa = new Mesa(1, 100);
        mesa.instalar();
        assertEquals(EstadoMesa.INSTALADA, mesa.getEstado());
    }

    @Test
    @DisplayName("instalar: una mesa ya instalada no puede instalarse dos veces")
    void instalarDosVecesFalla() {
        Mesa mesa = new Mesa(1, 100);
        mesa.instalar();
        assertThrows(TransicionEstadoInvalidaException.class, mesa::instalar);
    }

    @Test
    @DisplayName("cerrar: una mesa INSTALADA pasa a CERRADA")
    void cerrarDesdeInstalada() {
        Mesa mesa = new Mesa(1, 100);
        mesa.instalar();
        mesa.cerrar();
        assertEquals(EstadoMesa.CERRADA, mesa.getEstado());
    }

    @Test
    @DisplayName("cerrar: no se puede cerrar una mesa sin instalar")
    void cerrarSinInstalarFalla() {
        Mesa mesa = new Mesa(1, 100);
        assertThrows(TransicionEstadoInvalidaException.class, mesa::cerrar);
    }

    @Test
    @DisplayName("registrarActa: mesa cerrada con acta cerrada y propia")
    void registrarActaValida() {
        Mesa mesa = Fixtures.mesaCerrada(1, 100);
        Acta acta = Fixtures.actaCerrada(mesa, Fixtures.candidato("1"), 10);
        mesa.registrarActa(acta);
        assertSame(acta, mesa.getActa());
    }

    @Test
    @DisplayName("registrarActa: falla si la mesa aun no esta cerrada")
    void registrarActaSinCerrarMesaFalla() {
        Mesa mesa = new Mesa(1, 100);
        mesa.instalar();
        Acta acta = Fixtures.actaCerrada(mesa, Fixtures.candidato("1"), 10);
        assertThrows(TransicionEstadoInvalidaException.class, () -> mesa.registrarActa(acta));
    }

    @Test
    @DisplayName("registrarActa: falla si el acta pertenece a otra mesa")
    void registrarActaDeOtraMesaFalla() {
        Mesa mesa = Fixtures.mesaCerrada(1, 100);
        Mesa otra = Fixtures.mesaCerrada(2, 100);
        Acta acta = Fixtures.actaCerrada(otra, Fixtures.candidato("1"), 10);
        assertThrows(ValidacionElectoralException.class, () -> mesa.registrarActa(acta));
    }

    @Test
    @DisplayName("registrarActa: falla si el acta no esta cerrada")
    void registrarActaAbiertaFalla() {
        Mesa mesa = Fixtures.mesaCerrada(1, 100);
        Acta acta = Fixtures.acta(mesa, Fixtures.candidato("1"), 10);
        assertThrows(ValidacionElectoralException.class, () -> mesa.registrarActa(acta));
    }

    @Test
    @DisplayName("registrarActa: falla si la mesa ya tiene acta")
    void registrarActaDuplicadaFalla() {
        Mesa mesa = Fixtures.mesaCerrada(1, 100);
        mesa.registrarActa(Fixtures.actaCerrada(mesa, Fixtures.candidato("1"), 10));
        Acta segunda = Fixtures.actaCerrada(mesa, Fixtures.candidato("2"), 5);
        assertThrows(ValidacionElectoralException.class, () -> mesa.registrarActa(segunda));
    }

    @Test
    @DisplayName("registrarActa: rechaza acta nula")
    void registrarActaNulaFalla() {
        Mesa mesa = Fixtures.mesaCerrada(1, 100);
        assertThrows(IllegalArgumentException.class, () -> mesa.registrarActa(null));
    }

    @Test
    @DisplayName("totalVotos: sin acta el total es cero (caso limite)")
    void totalVotosSinActaEsCero() {
        assertEquals(0, new Mesa(1, 100).totalVotos());
    }

    @Test
    @DisplayName("totalVotos: con acta suma los votos consignados")
    void totalVotosConActaSuma() {
        Mesa mesa = Fixtures.mesaCerrada(1, 100);
        Acta acta = Fixtures.actaCerrada(mesa, Fixtures.candidato("1"), 10);
        mesa.registrarActa(acta);
        assertEquals(10, mesa.totalVotos());
    }

    @Test
    @DisplayName("mesas: una hoja se retorna a si misma en la lista")
    void mesasConUnaSolaHoja() {
        Mesa mesa = new Mesa(4, 80);
        assertEquals(1, mesa.mesas().size());
        assertSame(mesa, mesa.mesas().get(0));
    }

    @Test
    @DisplayName("censoTotal y mesas: la hoja retorna su propio censo y a si misma")
    void censoYMesasDeLaHoja() {
        Mesa mesa = new Mesa(7, 250);
        assertEquals(250, mesa.censoTotal());
        assertEquals(List.of(mesa), mesa.mesas());
        assertTrue(mesa.hijos().isEmpty());
    }

    @Test
    @DisplayName("agregar: una mesa es hoja y rechaza unidades hijas")
    void agregarFallaEnHoja() {
        Mesa mesa = new Mesa(1, 100);
        assertThrows(ValidacionElectoralException.class, () -> mesa.agregar(new Mesa(2, 100)));
    }

    @Test
    @DisplayName("agregar: rechaza unidad hija nula")
    void agregarNuloFalla() {
        Mesa mesa = new Mesa(1, 100);
        assertThrows(ValidacionElectoralException.class, () -> mesa.agregar(null));
    }

    @Test
    @DisplayName("remover: una mesa es hoja y rechaza unidades hijas")
    void removerFallaEnHoja() {
        Mesa mesa = new Mesa(1, 100);
        assertThrows(ValidacionElectoralException.class, () -> mesa.remover(new Mesa(2, 100)));
    }

    @Test
    @DisplayName("remover: rechaza unidad hija nula")
    void removerNuloFalla() {
        Mesa mesa = new Mesa(1, 100);
        assertThrows(ValidacionElectoralException.class, () -> mesa.remover(null));
    }

    @Test
    @DisplayName("nombre: usa el numero de mesa")
    void nombreConNumero() {
        assertEquals("Mesa 12", new Mesa(12, 100).nombre());
    }

    @Test
    @DisplayName("nombre: cambia con el numero de mesa")
    void nombreConOtroNumero() {
        assertEquals("Mesa 3", new Mesa(3, 100).nombre());
    }

    @Test
    @DisplayName("censoTotal: retorna el censo en otra mesa")
    void censoTotalConOtroValor() {
        assertEquals(50, new Mesa(3, 50).censoTotal());
    }

    @Test
    @DisplayName("hijos: una mesa no tiene hijos")
    void hijosVacioEnMesa() {
        assertTrue(new Mesa(1, 100).hijos().isEmpty());
    }

    @Test
    @DisplayName("constructor: rechaza numero de mesa no positivo")
    void numeroInvalidoFalla() {
        assertThrows(IllegalArgumentException.class, () -> new Mesa(0, 100));
    }

    @Test
    @DisplayName("constructor: rechaza censo no positivo")
    void censoInvalidoFalla() {
        assertThrows(IllegalArgumentException.class, () -> new Mesa(1, 0));
    }

    @Test
    @DisplayName("getPuesto: inicia nulo y se vincula al agregarse a un puesto")
    void puestoSeVinculaAlAgregar() {
        Mesa mesa = new Mesa(1, 100);
        assertNull(mesa.getPuesto());
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        Municipio municipio = Fixtures.municipio(departamento, "05001", "Medellin");
        PuestoVotacion puesto = Fixtures.puesto(municipio, "P-01", "Colegio");
        puesto.agregar(mesa);
        assertSame(puesto, mesa.getPuesto());
    }
}

package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PuestoVotacion: composite de mesas")
class PuestoVotacionTest {

    private Municipio municipio() {
        return Fixtures.municipio(Fixtures.departamento("05", "Antioquia"), "05001", "Medellin");
    }

    @Test
    @DisplayName("agregar: vincula la mesa en ambas direcciones")
    void agregarMesaValida() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio");
        Mesa mesa = new Mesa(1, 100);
        puesto.agregar(mesa);
        assertEquals(1, puesto.mesas().size());
        assertSame(puesto, mesa.getPuesto());
    }

    @Test
    @DisplayName("agregar: rechaza unidades que no son mesas")
    void agregarNoMesaFalla() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio");
        assertThrows(ValidacionElectoralException.class,
                () -> puesto.agregar(Fixtures.departamento("05", "Antioquia")));
    }

    @Test
    @DisplayName("agregar: rechaza una mesa duplicada")
    void agregarDuplicadaFalla() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio");
        Mesa mesa = new Mesa(1, 100);
        puesto.agregar(mesa);
        assertThrows(ValidacionElectoralException.class, () -> puesto.agregar(mesa));
    }

    @Test
    @DisplayName("agregar: rechaza una mesa que ya pertenece a otro puesto")
    void agregarMesaDeOtroPuestoFalla() {
        Municipio municipio = municipio();
        PuestoVotacion primero = Fixtures.puesto(municipio, "P-01", "Colegio A");
        PuestoVotacion segundo = Fixtures.puesto(municipio, "P-02", "Colegio B");
        Mesa mesa = new Mesa(1, 100);
        primero.agregar(mesa);
        assertThrows(ValidacionElectoralException.class, () -> segundo.agregar(mesa));
    }

    @Test
    @DisplayName("remover: elimina la mesa del puesto")
    void removerMesaValida() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio");
        Mesa mesa = new Mesa(1, 100);
        puesto.agregar(mesa);
        puesto.remover(mesa);
        assertTrue(puesto.mesas().isEmpty());
    }

    @Test
    @DisplayName("remover: falla si la mesa no pertenece al puesto")
    void removerMesaAjenaFalla() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio");
        assertThrows(ValidacionElectoralException.class, () -> puesto.remover(new Mesa(9, 100)));
    }

    @Test
    @DisplayName("remover: rechaza unidades que no son mesas")
    void removerNoMesaFalla() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio");
        assertThrows(ValidacionElectoralException.class,
                () -> puesto.remover(Fixtures.departamento("05", "Antioquia")));
    }

    @Test
    @DisplayName("buscarMesa: encuentra una mesa existente por numero")
    void buscarMesaExistente() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio");
        Mesa mesa = new Mesa(4, 100);
        puesto.agregar(mesa);
        assertEquals(mesa, puesto.buscarMesa(4).orElseThrow());
    }

    @Test
    @DisplayName("buscarMesa: retorna vacio si no existe (caso limite)")
    void buscarMesaInexistente() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio");
        assertTrue(puesto.buscarMesa(99).isEmpty());
    }

    @Test
    @DisplayName("censoTotal y totalVotos: vacio suma cero (caso limite)")
    void totalesVaciosSonCero() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio");
        assertEquals(0, puesto.censoTotal());
        assertEquals(0, puesto.totalVotos());
    }

    @Test
    @DisplayName("censoTotal y totalVotos: suman las mesas con acta")
    void totalesConMesas() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio");
        Mesa mesa = Fixtures.mesaCerrada(1, 100);
        Acta acta = Fixtures.actaCerrada(mesa, Fixtures.candidato("1"), 30);
        mesa.registrarActa(acta);
        puesto.agregar(mesa);
        assertEquals(100, puesto.censoTotal());
        assertEquals(30, puesto.totalVotos());
    }

    @Test
    @DisplayName("hijos: retorna las mesas del puesto")
    void hijosContieneMesas() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio");
        puesto.agregar(new Mesa(1, 100));
        puesto.agregar(new Mesa(2, 100));
        List<? extends UnidadTerritorial> hijos = puesto.hijos();
        assertEquals(2, hijos.size());
        assertTrue(hijos.stream().allMatch(Mesa.class::isInstance));
    }

    @Test
    @DisplayName("hijos: un puesto sin mesas retorna lista vacia (caso limite)")
    void hijosVacio() {
        assertTrue(Fixtures.puesto(municipio(), "P-01", "Colegio").hijos().isEmpty());
    }

    @Test
    @DisplayName("nombre: retorna el nombre del puesto")
    void nombreRetornaNombre() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio Nacional");
        assertEquals("Colegio Nacional", puesto.nombre());
    }

    @Test
    @DisplayName("nombre: no confunde el nombre con el id")
    void nombreNoEsId() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio Nacional");
        assertNotEquals(puesto.getId(), puesto.nombre());
    }

    @Test
    @DisplayName("mesas: conserva las mesas agregadas")
    void mesasConDatos() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio");
        Mesa primera = new Mesa(1, 100);
        Mesa segunda = new Mesa(2, 120);
        puesto.agregar(primera);
        puesto.agregar(segunda);
        assertEquals(List.of(primera, segunda), puesto.mesas());
    }

    @Test
    @DisplayName("mesas: la lista retornada es inmutable")
    void mesasInmutables() {
        PuestoVotacion puesto = Fixtures.puesto(municipio(), "P-01", "Colegio");
        assertThrows(UnsupportedOperationException.class, () -> puesto.mesas().add(new Mesa(1, 100)));
    }

    @Test
    @DisplayName("constructor: conserva id, nombre, direccion y municipio")
    void constructorConservaDatos() {
        Municipio municipio = municipio();
        PuestoVotacion puesto = new PuestoVotacion("P-01", "Colegio", "Calle 1", municipio);
        assertEquals("P-01", puesto.getId());
        assertEquals("Colegio", puesto.nombre());
        assertEquals("Calle 1", puesto.getDireccion());
        assertSame(municipio, puesto.getMunicipio());
    }

    @Test
    @DisplayName("constructor: rechaza datos obligatorios vacios")
    void constructorInvalidoFalla() {
        Municipio municipio = municipio();
        assertThrows(IllegalArgumentException.class,
                () -> new PuestoVotacion("", "Colegio", "Calle 1", municipio));
        assertThrows(IllegalArgumentException.class,
                () -> new PuestoVotacion("P-01", " ", "Calle 1", municipio));
        assertThrows(IllegalArgumentException.class,
                () -> new PuestoVotacion("P-01", "Colegio", null, municipio));
        assertThrows(IllegalArgumentException.class,
                () -> new PuestoVotacion("P-01", "Colegio", "Calle 1", null));
    }
}

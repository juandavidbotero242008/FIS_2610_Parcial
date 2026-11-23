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

@DisplayName("Municipio: composite de puestos de votacion")
class MunicipioTest {

    private Departamento departamento() {
        return Fixtures.departamento("05", "Antioquia");
    }

    @Test
    @DisplayName("agregar: vincula el puesto al municipio")
    void agregarPuestoValido() {
        Municipio municipio = Fixtures.municipio(departamento(), "05001", "Medellin");
        PuestoVotacion puesto = Fixtures.puesto(municipio, "P-01", "Colegio");
        municipio.agregar(puesto);
        assertEquals(1, municipio.hijos().size());
        assertSame(municipio, puesto.getMunicipio());
    }

    @Test
    @DisplayName("agregar: rechaza unidades que no son puestos")
    void agregarNoPuestoFalla() {
        Municipio municipio = Fixtures.municipio(departamento(), "05001", "Medellin");
        assertThrows(ValidacionElectoralException.class, () -> municipio.agregar(new Mesa(1, 100)));
    }

    @Test
    @DisplayName("agregar: rechaza un puesto duplicado")
    void agregarPuestoDuplicadoFalla() {
        Municipio municipio = Fixtures.municipio(departamento(), "05001", "Medellin");
        PuestoVotacion puesto = Fixtures.puesto(municipio, "P-01", "Colegio");
        municipio.agregar(puesto);
        assertThrows(ValidacionElectoralException.class, () -> municipio.agregar(puesto));
    }

    @Test
    @DisplayName("remover: elimina el puesto del municipio")
    void removerPuestoValido() {
        Municipio municipio = Fixtures.municipio(departamento(), "05001", "Medellin");
        PuestoVotacion puesto = Fixtures.puesto(municipio, "P-01", "Colegio");
        municipio.agregar(puesto);
        municipio.remover(puesto);
        assertTrue(municipio.hijos().isEmpty());
    }

    @Test
    @DisplayName("remover: falla si el puesto no pertenece al municipio")
    void removerPuestoAjenoFalla() {
        Municipio municipio = Fixtures.municipio(departamento(), "05001", "Medellin");
        PuestoVotacion ajeno = Fixtures.puesto(municipio, "P-99", "Otro");
        assertThrows(ValidacionElectoralException.class, () -> municipio.remover(ajeno));
    }

    @Test
    @DisplayName("remover: rechaza unidades que no son puestos")
    void removerNoPuestoFalla() {
        Municipio municipio = Fixtures.municipio(departamento(), "05001", "Medellin");
        assertThrows(ValidacionElectoralException.class, () -> municipio.remover(new Mesa(1, 100)));
    }

    @Test
    @DisplayName("buscarPuesto: encuentra un puesto por id")
    void buscarPuestoExistente() {
        Municipio municipio = Fixtures.municipio(departamento(), "05001", "Medellin");
        PuestoVotacion puesto = Fixtures.puesto(municipio, "P-01", "Colegio");
        municipio.agregar(puesto);
        assertEquals(puesto, municipio.buscarPuesto("P-01").orElseThrow());
    }

    @Test
    @DisplayName("buscarPuesto: retorna vacio si no existe (caso limite)")
    void buscarPuestoInexistente() {
        Municipio municipio = Fixtures.municipio(departamento(), "05001", "Medellin");
        assertTrue(municipio.buscarPuesto("NO-EXISTE").isEmpty());
    }

    @Test
    @DisplayName("totales y mesas: municipio vacio suma cero (caso limite)")
    void totalesVacios() {
        Municipio municipio = Fixtures.municipio(departamento(), "05001", "Medellin");
        assertEquals(0, municipio.censoTotal());
        assertEquals(0, municipio.totalVotos());
        assertTrue(municipio.mesas().isEmpty());
        assertTrue(municipio.hijos().isEmpty());
    }

    @Test
    @DisplayName("totales y mesas: consolidan la jerarquia municipio-puesto-mesa")
    void totalesConJerarquia() {
        Municipio municipio = Fixtures.municipio(departamento(), "05001", "Medellin");
        PuestoVotacion puesto = Fixtures.puesto(municipio, "P-01", "Colegio");
        Mesa mesa = Fixtures.mesaCerrada(1, 200);
        mesa.registrarActa(Fixtures.actaCerrada(mesa, Fixtures.candidato("1"), 50));
        puesto.agregar(mesa);
        municipio.agregar(puesto);
        assertEquals(200, municipio.censoTotal());
        assertEquals(50, municipio.totalVotos());
        assertEquals(List.of(mesa), municipio.mesas());
    }

    @Test
    @DisplayName("constructor: rechaza codigo, nombre o departamento invalidos")
    void constructorInvalidoFalla() {
        Departamento departamento = departamento();
        assertThrows(IllegalArgumentException.class,
                () -> new Municipio("", "Medellin", departamento));
        assertThrows(IllegalArgumentException.class,
                () -> new Municipio("05001", "  ", departamento));
        assertThrows(IllegalArgumentException.class,
                () -> new Municipio("05001", "Medellin", null));
    }

    @Test
    @DisplayName("nombre: retorna el nombre del municipio")
    void nombreRetornaNombre() {
        Municipio municipio = Fixtures.municipio(departamento(), "05001", "Medellin");
        assertEquals("Medellin", municipio.nombre());
    }

    @Test
    @DisplayName("nombre: no confunde el nombre con el codigo")
    void nombreNoEsCodigo() {
        Municipio municipio = Fixtures.municipio(departamento(), "05001", "Medellin");
        assertNotEquals(municipio.getCodigo(), municipio.nombre());
    }
}

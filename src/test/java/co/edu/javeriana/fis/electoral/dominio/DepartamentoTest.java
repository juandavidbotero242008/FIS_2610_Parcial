package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Departamento: raiz del composite territorial")
class DepartamentoTest {

    @Test
    @DisplayName("agregar: incorpora el municipio a la jerarquia")
    void agregarMunicipioValido() {
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        Municipio municipio = Fixtures.municipio(departamento, "05001", "Medellin");
        departamento.agregar(municipio);
        assertEquals(1, departamento.hijos().size());
        assertEquals(departamento, municipio.getDepartamento());
    }

    @Test
    @DisplayName("agregar: rechaza unidades que no son municipios")
    void agregarNoMunicipioFalla() {
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        assertThrows(ValidacionElectoralException.class,
                () -> departamento.agregar(new Mesa(1, 100)));
    }

    @Test
    @DisplayName("agregar: rechaza un municipio duplicado")
    void agregarMunicipioDuplicadoFalla() {
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        Municipio municipio = Fixtures.municipio(departamento, "05001", "Medellin");
        departamento.agregar(municipio);
        assertThrows(ValidacionElectoralException.class, () -> departamento.agregar(municipio));
    }

    @Test
    @DisplayName("agregar: rechaza un municipio que ya pertenece a otro departamento")
    void agregarMunicipioDeOtroDepartamentoFalla() {
        Departamento antioquia = Fixtures.departamento("05", "Antioquia");
        Departamento cordoba = Fixtures.departamento("23", "Cordoba");
        Municipio municipio = Fixtures.municipio(cordoba, "05001", "Medellin");
        assertThrows(ValidacionElectoralException.class, () -> antioquia.agregar(municipio));
    }

    @Test
    @DisplayName("remover: elimina el municipio del departamento")
    void removerMunicipioValido() {
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        Municipio municipio = Fixtures.municipio(departamento, "05001", "Medellin");
        departamento.agregar(municipio);
        departamento.remover(municipio);
        assertTrue(departamento.hijos().isEmpty());
    }

    @Test
    @DisplayName("remover: falla si el municipio no pertenece al departamento")
    void removerMunicipioAjenoFalla() {
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        Municipio ajeno = Fixtures.municipio(departamento, "05002", "Envigado");
        assertThrows(ValidacionElectoralException.class, () -> departamento.remover(ajeno));
    }

    @Test
    @DisplayName("remover: rechaza unidades que no son municipios")
    void removerNoMunicipioFalla() {
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        assertThrows(ValidacionElectoralException.class,
                () -> departamento.remover(new Mesa(1, 100)));
    }

    @Test
    @DisplayName("buscarMunicipio: encuentra un municipio por codigo")
    void buscarMunicipioExistente() {
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        Municipio municipio = Fixtures.municipio(departamento, "05001", "Medellin");
        departamento.agregar(municipio);
        assertEquals(municipio, departamento.buscarMunicipio("05001").orElseThrow());
    }

    @Test
    @DisplayName("buscarMunicipio: retorna vacio si no existe (caso limite)")
    void buscarMunicipioInexistente() {
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        assertTrue(departamento.buscarMunicipio("99999").isEmpty());
    }

    @Test
    @DisplayName("totales y mesas: departamento vacio suma cero (caso limite)")
    void totalesVacios() {
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        assertEquals(0, departamento.censoTotal());
        assertEquals(0, departamento.totalVotos());
        assertTrue(departamento.mesas().isEmpty());
        assertTrue(departamento.hijos().isEmpty());
    }

    @Test
    @DisplayName("totales y mesas: consolidan toda la jerarquia")
    void totalesConJerarquia() {
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        Municipio municipio = Fixtures.municipio(departamento, "05001", "Medellin");
        PuestoVotacion puesto = Fixtures.puesto(municipio, "P-01", "Colegio");
        Mesa mesa1 = Fixtures.mesaCerrada(1, 100);
        mesa1.registrarActa(Fixtures.actaCerrada(mesa1, Fixtures.candidato("1"), 40));
        Mesa mesa2 = Fixtures.mesaCerrada(2, 150);
        mesa2.registrarActa(Fixtures.actaCerrada(mesa2, Fixtures.candidato("1"), 60));
        puesto.agregar(mesa1);
        puesto.agregar(mesa2);
        municipio.agregar(puesto);
        departamento.agregar(municipio);
        assertEquals(250, departamento.censoTotal());
        assertEquals(100, departamento.totalVotos());
        assertEquals(List.of(mesa1, mesa2), departamento.mesas());
    }

    @Test
    @DisplayName("constructor: rechaza codigo o nombre vacios")
    void constructorInvalidoFalla() {
        assertThrows(IllegalArgumentException.class, () -> new Departamento("", "Antioquia"));
        assertThrows(IllegalArgumentException.class, () -> new Departamento("05", " "));
    }

    @Test
    @DisplayName("constructor: conserva codigo y nombre")
    void constructorConservaDatos() {
        Departamento departamento = new Departamento("05", "Antioquia");
        assertEquals("05", departamento.getCodigo());
        assertEquals("Antioquia", departamento.nombre());
    }

    @Test
    @DisplayName("nombre: retorna el nombre del departamento")
    void nombreRetornaNombre() {
        assertEquals("Antioquia", Fixtures.departamento("05", "Antioquia").nombre());
    }

    @Test
    @DisplayName("nombre: no confunde el nombre con el codigo")
    void nombreNoEsCodigo() {
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        assertNotEquals(departamento.getCodigo(), departamento.nombre());
    }
}

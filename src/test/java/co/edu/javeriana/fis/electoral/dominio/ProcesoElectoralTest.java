package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.dominio.enums.TipoEleccion;
import co.edu.javeriana.fis.electoral.excepcion.TransicionEstadoInvalidaException;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import co.edu.javeriana.fis.electoral.patrones.comportamental.state.EstadoCerrado;
import co.edu.javeriana.fis.electoral.patrones.comportamental.state.EstadoProgramado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ProcesoElectoral: patron State y raiz territorial")
class ProcesoElectoralTest {

    @Test
    @DisplayName("ciclo de vida: PROGRAMADO -> EN_CURSO -> ESCRUTINIO -> CERRADO")
    void cicloDeVidaCompleto() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        assertEquals("PROGRAMADO", proceso.estadoActual());
        proceso.iniciar();
        assertEquals("EN_CURSO", proceso.estadoActual());
        proceso.pasarAEscrutinio();
        assertEquals("ESCRUTINIO", proceso.estadoActual());
        proceso.cerrar();
        assertEquals("CERRADO", proceso.estadoActual());
    }

    @Test
    @DisplayName("iniciar: no se puede iniciar un proceso ya iniciado")
    void iniciarDosVecesFalla() {
        ProcesoElectoral proceso = Fixtures.procesoEnCurso(TipoEleccion.LEGISLATIVA);
        assertThrows(TransicionEstadoInvalidaException.class, proceso::iniciar);
    }

    @Test
    @DisplayName("pasarAEscrutinio: falla si el proceso sigue PROGRAMADO")
    void pasarAEscrutinioSinIniciarFalla() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        assertThrows(TransicionEstadoInvalidaException.class, proceso::pasarAEscrutinio);
    }

    @Test
    @DisplayName("cerrar: falla si el proceso no esta en escrutinio")
    void cerrarSinEscrutinioFalla() {
        ProcesoElectoral proceso = Fixtures.procesoEnCurso(TipoEleccion.LEGISLATIVA);
        assertThrows(TransicionEstadoInvalidaException.class, proceso::cerrar);
    }

    @Test
    @DisplayName("cancelar: un proceso programado puede cancelarse")
    void cancelarProgramado() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        proceso.cancelar();
        assertEquals("CANCELADO", proceso.estadoActual());
    }

    @Test
    @DisplayName("cancelar: un proceso cerrado ya no puede cancelarse")
    void cancelarCerradoFalla() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        proceso.iniciar();
        proceso.pasarAEscrutinio();
        proceso.cerrar();
        assertThrows(TransicionEstadoInvalidaException.class, proceso::cancelar);
    }

    @Test
    @DisplayName("agregarTerritorio: se permite mientras el proceso esta PROGRAMADO")
    void agregarTerritorioProgramado() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        proceso.agregarTerritorio(departamento);
        assertEquals(1, proceso.getDepartamentos().size());
        assertEquals(departamento, proceso.buscarDepartamento("05").orElseThrow());
    }

    @Test
    @DisplayName("agregarTerritorio: falla si el proceso ya inicio")
    void agregarTerritorioEnCursoFalla() {
        ProcesoElectoral proceso = Fixtures.procesoEnCurso(TipoEleccion.LEGISLATIVA);
        assertThrows(TransicionEstadoInvalidaException.class,
                () -> proceso.agregarTerritorio(Fixtures.departamento("05", "Antioquia")));
    }

    @Test
    @DisplayName("agregarTerritorio: rechaza un departamento duplicado")
    void agregarTerritorioDuplicadoFalla() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        proceso.agregarTerritorio(departamento);
        assertThrows(ValidacionElectoralException.class,
                () -> proceso.agregarTerritorio(departamento));
    }

    @Test
    @DisplayName("removerTerritorio: elimina un departamento registrado")
    void removerTerritorioValido() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        Departamento departamento = Fixtures.departamento("05", "Antioquia");
        proceso.agregarTerritorio(departamento);
        proceso.removerTerritorio(departamento);
        assertTrue(proceso.getDepartamentos().isEmpty());
    }

    @Test
    @DisplayName("removerTerritorio: falla si el departamento no pertenece al proceso")
    void removerTerritorioAjenoFalla() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        assertThrows(ValidacionElectoralException.class,
                () -> proceso.removerTerritorio(Fixtures.departamento("05", "Antioquia")));
    }

    @Test
    @DisplayName("removerTerritorio: falla si el proceso ya inicio")
    void removerTerritorioEnCursoFalla() {
        ProcesoElectoral proceso = Fixtures.procesoEnCurso(TipoEleccion.LEGISLATIVA);
        assertThrows(TransicionEstadoInvalidaException.class,
                () -> proceso.removerTerritorio(Fixtures.departamento("05", "Antioquia")));
    }

    @Test
    @DisplayName("totales y mesas: sin territorios suman cero (caso limite)")
    void totalesSinTerritorios() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        assertEquals(0, proceso.censoTotal());
        assertEquals(0, proceso.totalVotos());
        assertTrue(proceso.mesas().isEmpty());
    }

    @Test
    @DisplayName("totales y mesas: consolidan toda la jerarquia territorial")
    void totalesConJerarquia() {
        Mesa mesa = Fixtures.mesaCerrada(1, 120);
        mesa.registrarActa(Fixtures.actaCerrada(mesa, Fixtures.candidato("1"), 70));
        ProcesoElectoral proceso = Fixtures.procesoConMesa(TipoEleccion.LEGISLATIVA, mesa);
        assertEquals(120, proceso.censoTotal());
        assertEquals(70, proceso.totalVotos());
        assertEquals(List.of(mesa), proceso.mesas());
    }

    @Test
    @DisplayName("buscarDepartamento: retorna vacio si no existe (caso limite)")
    void buscarDepartamentoInexistente() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        assertTrue(proceso.buscarDepartamento("99").isEmpty());
    }

    @Test
    @DisplayName("establecerEstado: el estado inicial es EstadoProgramado")
    void estadoInicialEsProgramado() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        assertInstanceOf(EstadoProgramado.class, proceso.getEstado());
    }

    @Test
    @DisplayName("establecerEstado: rechaza estado nulo")
    void establecerEstadoNuloFalla() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        assertThrows(IllegalArgumentException.class, () -> proceso.establecerEstado(null));
    }

    @Test
    @DisplayName("establecerEstado: cambia el estado actual del proceso")
    void establecerEstadoCambiaEstado() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        proceso.establecerEstado(new EstadoCerrado());
        assertEquals("CERRADO", proceso.estadoActual());
    }

    @Test
    @DisplayName("constructor: rechaza datos obligatorios invalidos")
    void constructorInvalidoFalla() {
        LocalDate fecha = Fixtures.FECHA_ELECCION;
        assertThrows(IllegalArgumentException.class,
                () -> new ProcesoElectoral("", "Elecciones", TipoEleccion.LEGISLATIVA, fecha));
        assertThrows(IllegalArgumentException.class,
                () -> new ProcesoElectoral("PE-1", " ", TipoEleccion.LEGISLATIVA, fecha));
        assertThrows(IllegalArgumentException.class,
                () -> new ProcesoElectoral("PE-1", "Elecciones", null, fecha));
        assertThrows(IllegalArgumentException.class,
                () -> new ProcesoElectoral("PE-1", "Elecciones", TipoEleccion.LEGISLATIVA, null));
    }
}

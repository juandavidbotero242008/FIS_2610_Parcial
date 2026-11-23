package co.edu.javeriana.fis.electoral.patrones.comportamental.state;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.dominio.ProcesoElectoral;
import co.edu.javeriana.fis.electoral.dominio.enums.TipoEleccion;
import co.edu.javeriana.fis.electoral.excepcion.TransicionEstadoInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("State: transiciones de cada estado del proceso")
class EstadoProcesoTest {

    @Test
    @DisplayName("EstadoProgramado: inicia el proceso")
    void programadoInicia() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        assertEquals("PROGRAMADO", proceso.getEstado().nombre());
        proceso.iniciar();
        assertEquals("EN_CURSO", proceso.estadoActual());
    }

    @Test
    @DisplayName("EstadoProgramado: no permite pasar directo a escrutinio")
    void programadoNoPasaAEscrutinio() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        assertThrows(TransicionEstadoInvalidaException.class, proceso::pasarAEscrutinio);
    }

    @Test
    @DisplayName("EstadoProgramado: se puede cancelar")
    void programadoCancela() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        proceso.cancelar();
        assertEquals("CANCELADO", proceso.estadoActual());
    }

    @Test
    @DisplayName("EstadoEnCurso: pasa a escrutinio")
    void enCursoPasaAEscrutinio() {
        ProcesoElectoral proceso = Fixtures.procesoEnCurso(TipoEleccion.LEGISLATIVA);
        proceso.pasarAEscrutinio();
        assertEquals("ESCRUTINIO", proceso.estadoActual());
    }

    @Test
    @DisplayName("EstadoEnCurso: no se puede cerrar sin escrutinio")
    void enCursoNoCierra() {
        ProcesoElectoral proceso = Fixtures.procesoEnCurso(TipoEleccion.LEGISLATIVA);
        assertThrows(TransicionEstadoInvalidaException.class, proceso::cerrar);
    }

    @Test
    @DisplayName("EstadoEscrutinio: cierra el proceso")
    void escrutinioCierra() {
        ProcesoElectoral proceso = Fixtures.procesoEnCurso(TipoEleccion.LEGISLATIVA);
        proceso.pasarAEscrutinio();
        proceso.cerrar();
        assertEquals("CERRADO", proceso.estadoActual());
    }

    @Test
    @DisplayName("EstadoEscrutinio: no se puede cancelar en escrutinio")
    void escrutinioNoCancela() {
        ProcesoElectoral proceso = Fixtures.procesoEnCurso(TipoEleccion.LEGISLATIVA);
        proceso.pasarAEscrutinio();
        assertThrows(TransicionEstadoInvalidaException.class, proceso::cancelar);
    }

    @Test
    @DisplayName("EstadoCerrado: no admite nuevas transiciones")
    void cerradoNoTransiciona() {
        ProcesoElectoral proceso = Fixtures.procesoEnCurso(TipoEleccion.LEGISLATIVA);
        proceso.pasarAEscrutinio();
        proceso.cerrar();
        assertThrows(TransicionEstadoInvalidaException.class, proceso::iniciar);
        assertThrows(TransicionEstadoInvalidaException.class, proceso::cancelar);
    }

    @Test
    @DisplayName("EstadoCancelado: no admite nuevas transiciones")
    void canceladoNoTransiciona() {
        ProcesoElectoral proceso = Fixtures.proceso(TipoEleccion.LEGISLATIVA);
        proceso.cancelar();
        assertThrows(TransicionEstadoInvalidaException.class, proceso::iniciar);
        assertThrows(TransicionEstadoInvalidaException.class, proceso::pasarAEscrutinio);
        assertThrows(TransicionEstadoInvalidaException.class, proceso::cerrar);
    }
}

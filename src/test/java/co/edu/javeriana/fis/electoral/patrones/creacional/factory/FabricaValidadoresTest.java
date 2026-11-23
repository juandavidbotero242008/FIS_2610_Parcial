package co.edu.javeriana.fis.electoral.patrones.creacional.factory;

import co.edu.javeriana.fis.electoral.Fixtures;
import co.edu.javeriana.fis.electoral.dominio.Acta;
import co.edu.javeriana.fis.electoral.dominio.Candidato;
import co.edu.javeriana.fis.electoral.dominio.Mesa;
import co.edu.javeriana.fis.electoral.dominio.enums.TipoEleccion;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FabricaValidadores: Factory Method por tipo de eleccion")
class FabricaValidadoresTest {

    private Acta acta(Mesa mesa, int... votos) {
        Acta acta = new Acta(mesa, Fixtures.FECHA_CIERRE);
        for (int i = 0; i < votos.length; i++) {
            acta.agregarResultado(Fixtures.candidato("C" + (i + 1)), votos[i]);
        }
        return acta;
    }

    @Test
    @DisplayName("para: retorna el creador legislativo")
    void creadorLegislativo() {
        CreadorValidadorActa creador = FabricaValidadores.para(TipoEleccion.LEGISLATIVA);
        assertInstanceOf(CreadorLegislativo.class, creador);
        assertInstanceOf(ValidadorLegislativo.class, creador.crearValidador());
    }

    @Test
    @DisplayName("para: retorna el creador presidencial")
    void creadorPresidencial() {
        CreadorValidadorActa creador = FabricaValidadores.para(TipoEleccion.PRESIDENCIAL);
        assertInstanceOf(CreadorPresidencial.class, creador);
        assertInstanceOf(ValidadorPresidencial.class, creador.crearValidador());
    }

    @Test
    @DisplayName("para: retorna el creador de consulta")
    void creadorConsulta() {
        CreadorValidadorActa creador = FabricaValidadores.para(TipoEleccion.CONSULTA_POPULAR);
        assertInstanceOf(CreadorConsulta.class, creador);
        assertInstanceOf(ValidadorConsulta.class, creador.crearValidador());
    }

    @Test
    @DisplayName("para: rechaza tipo de eleccion nulo")
    void tipoNuloFalla() {
        assertThrows(IllegalArgumentException.class, () -> FabricaValidadores.para(null));
    }

    @Test
    @DisplayName("legislativo: acepta un acta con al menos un voto")
    void legislativoValido() {
        FabricaValidadores.para(TipoEleccion.LEGISLATIVA).validar(acta(new Mesa(1, 100), 10));
    }

    @Test
    @DisplayName("legislativo: rechaza un acta sin votos (caso limite)")
    void legislativoSinVotosFalla() {
        assertThrows(ValidacionElectoralException.class,
                () -> FabricaValidadores.para(TipoEleccion.LEGISLATIVA)
                        .validar(acta(new Mesa(1, 100))));
    }

    @Test
    @DisplayName("legislativo: rechaza votos superiores al censo")
    void legislativoExcedeCensoFalla() {
        assertThrows(ValidacionElectoralException.class,
                () -> FabricaValidadores.para(TipoEleccion.LEGISLATIVA)
                        .validar(acta(new Mesa(1, 100), 150)));
    }

    @Test
    @DisplayName("presidencial: acepta dos candidatos con votos")
    void presidencialValido() {
        FabricaValidadores.para(TipoEleccion.PRESIDENCIAL)
                .validar(acta(new Mesa(1, 100), 30, 20));
    }

    @Test
    @DisplayName("presidencial: rechaza un acta con un solo candidato")
    void presidencialUnCandidatoFalla() {
        assertThrows(ValidacionElectoralException.class,
                () -> FabricaValidadores.para(TipoEleccion.PRESIDENCIAL)
                        .validar(acta(new Mesa(1, 100), 30)));
    }

    @Test
    @DisplayName("consulta: permite un acta sin votos (caso limite)")
    void consultaSinVotosValida() {
        FabricaValidadores.para(TipoEleccion.CONSULTA_POPULAR)
                .validar(acta(new Mesa(1, 100)));
    }

    @Test
    @DisplayName("consulta: rechaza votos superiores al censo")
    void consultaExcedeCensoFalla() {
        assertThrows(ValidacionElectoralException.class,
                () -> FabricaValidadores.para(TipoEleccion.CONSULTA_POPULAR)
                        .validar(acta(new Mesa(1, 100), 101)));
    }

    @Test
    @DisplayName("validar: rechaza acta nula")
    void actaNulaFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> FabricaValidadores.para(TipoEleccion.LEGISLATIVA).validar(null));
    }

    @Test
    @DisplayName("validar: acepta votos exactamente iguales al censo (caso limite)")
    void votosIgualesAlCenso() {
        Candidato candidato = Fixtures.candidato("1");
        Acta acta = new Acta(new Mesa(1, 100), Fixtures.FECHA_CIERRE);
        acta.agregarResultado(candidato, 100);
        FabricaValidadores.para(TipoEleccion.LEGISLATIVA).validar(acta);
        assertTrue(acta.totalVotos() == 100);
    }
}

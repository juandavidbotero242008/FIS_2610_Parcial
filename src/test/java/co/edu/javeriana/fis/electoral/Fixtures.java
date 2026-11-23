package co.edu.javeriana.fis.electoral;

import co.edu.javeriana.fis.electoral.dominio.Acta;
import co.edu.javeriana.fis.electoral.dominio.Candidato;
import co.edu.javeriana.fis.electoral.dominio.Departamento;
import co.edu.javeriana.fis.electoral.dominio.Jurado;
import co.edu.javeriana.fis.electoral.dominio.Mesa;
import co.edu.javeriana.fis.electoral.dominio.Municipio;
import co.edu.javeriana.fis.electoral.dominio.Partido;
import co.edu.javeriana.fis.electoral.dominio.ProcesoElectoral;
import co.edu.javeriana.fis.electoral.dominio.PuestoVotacion;
import co.edu.javeriana.fis.electoral.dominio.TestigoElectoral;
import co.edu.javeriana.fis.electoral.dominio.enums.FuncionJurado;
import co.edu.javeriana.fis.electoral.dominio.enums.TipoEleccion;
import co.edu.javeriana.fis.electoral.patrones.creacional.builder.ActaBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Datos de prueba reutilizables para mantener legibles las pruebas unitarias. */
public final class Fixtures {

    public static final LocalDate FECHA_ELECCION = LocalDate.of(2026, 3, 8);
    public static final LocalDateTime FECHA_CIERRE = LocalDateTime.of(2026, 3, 8, 17, 0);

    private Fixtures() {
    }

    public static ProcesoElectoral proceso(TipoEleccion tipo) {
        return new ProcesoElectoral("PE-2026", "Elecciones 2026", tipo, FECHA_ELECCION);
    }

    public static ProcesoElectoral procesoEnCurso(TipoEleccion tipo) {
        ProcesoElectoral proceso = proceso(tipo);
        proceso.iniciar();
        return proceso;
    }

    public static Departamento departamento(String codigo, String nombre) {
        return new Departamento(codigo, nombre);
    }

    public static Municipio municipio(Departamento departamento, String codigo, String nombre) {
        return new Municipio(codigo, nombre, departamento);
    }

    public static PuestoVotacion puesto(Municipio municipio, String id, String nombre) {
        return new PuestoVotacion(id, nombre, "Calle 1 # 2-3", municipio);
    }

    public static Mesa mesa(int numero, int censo) {
        return new Mesa(numero, censo);
    }

    public static Candidato candidato(String documento) {
        return new Candidato(documento, "Candidato " + documento);
    }

    public static Partido partido(String id) {
        return new Partido(id, "Partido " + id);
    }

    public static Jurado jurado(String documento) {
        return new Jurado(documento, "Jurado " + documento, "jurado" + documento + "@correo.co");
    }

    public static TestigoElectoral testigo(String documento, Partido partido) {
        return new TestigoElectoral(documento, "Testigo " + documento, partido);
    }

    public static Acta acta(Mesa mesa, Candidato candidato, int votos) {
        return new ActaBuilder()
                .conMesa(mesa)
                .conFechaCierre(FECHA_CIERRE)
                .agregarResultado(candidato, votos)
                .construir();
    }

    public static Acta actaCerrada(Mesa mesa, Candidato candidato, int votos) {
        Acta acta = acta(mesa, candidato, votos);
        acta.cerrar();
        return acta;
    }

    public static Acta actaCerrada(Mesa mesa, Candidato c1, int v1, Candidato c2, int v2) {
        Acta acta = new ActaBuilder()
                .conMesa(mesa)
                .conFechaCierre(FECHA_CIERRE)
                .agregarResultado(c1, v1)
                .agregarResultado(c2, v2)
                .construir();
        acta.cerrar();
        return acta;
    }

    public static Acta actaVacia(Mesa mesa) {
        return new ActaBuilder().conMesa(mesa).conFechaCierre(FECHA_CIERRE).construir();
    }

    /**
     * Construye un proceso con un unico departamento, municipio, puesto y la
     * mesa indicada. El proceso queda en estado PROGRAMADO.
     */
    public static ProcesoElectoral procesoConMesa(TipoEleccion tipo, Mesa mesa) {
        ProcesoElectoral proceso = proceso(tipo);
        Departamento departamento = departamento("05", "Antioquia");
        Municipio municipio = municipio(departamento, "05001", "Medellin");
        PuestoVotacion puesto = puesto(municipio, "P-01", "Colegio Nacional");
        departamento.agregar(municipio);
        municipio.agregar(puesto);
        puesto.agregar(mesa);
        proceso.agregarTerritorio(departamento);
        return proceso;
    }

    /** Deja la mesa en estado CERRADA, requisito para registrar su acta. */
    public static Mesa mesaCerrada(int numero, int censo) {
        Mesa mesa = new Mesa(numero, censo);
        mesa.instalar();
        mesa.cerrar();
        return mesa;
    }

    /** Crea un jurado asignado a una mesa con la funcion indicada. */
    public static Jurado juradoAsignado(String documento, Mesa mesa, FuncionJurado funcion) {
        Jurado jurado = jurado(documento);
        jurado.asignar(mesa, funcion);
        return jurado;
    }
}

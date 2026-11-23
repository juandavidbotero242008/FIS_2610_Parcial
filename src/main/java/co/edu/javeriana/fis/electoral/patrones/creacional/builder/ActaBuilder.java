package co.edu.javeriana.fis.electoral.patrones.creacional.builder;

import co.edu.javeriana.fis.electoral.dominio.Acta;
import co.edu.javeriana.fis.electoral.dominio.Candidato;
import co.edu.javeriana.fis.electoral.dominio.Mesa;
import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Constructor paso a paso de un {@link Acta} (patron <b>Builder</b>).
 *
 * <p>El acta de cierre tiene varios datos obligatorios y una coleccion de
 * resultados que debe quedar consistente (sin candidatos duplicados y sin
 * votos negativos). El builder concentra esas reglas y evita constructores
 * telescopicos; el acta solo se crea cuando la configuracion es valida.</p>
 *
 * <pre>{@code
 * Acta acta = new ActaBuilder()
 *         .conMesa(mesa)
 *         .agregarResultado(candidatoA, 120)
 *         .agregarResultado(candidatoB, 80)
 *         .construir();
 * }</pre>
 */
public class ActaBuilder {

    private Mesa mesa;
    private LocalDateTime fechaCierre;
    private final Map<Candidato, Integer> resultados = new LinkedHashMap<>();

    public ActaBuilder conMesa(Mesa mesa) {
        if (mesa == null) {
            throw new IllegalArgumentException("La mesa es obligatoria");
        }
        this.mesa = mesa;
        return this;
    }

    public ActaBuilder conFechaCierre(LocalDateTime fechaCierre) {
        if (fechaCierre == null) {
            throw new IllegalArgumentException("La fecha de cierre es obligatoria");
        }
        this.fechaCierre = fechaCierre;
        return this;
    }

    /** Agrega el resultado de un candidato; valida duplicados y votos negativos. */
    public ActaBuilder agregarResultado(Candidato candidato, int votos) {
        if (candidato == null) {
            throw new IllegalArgumentException("El candidato es obligatorio");
        }
        if (votos < 0) {
            throw new IllegalArgumentException("Los votos no pueden ser negativos");
        }
        if (resultados.containsKey(candidato)) {
            throw new ValidacionElectoralException(
                    "El candidato " + candidato.getNombre() + " ya fue agregado al acta");
        }
        resultados.put(candidato, votos);
        return this;
    }

    /**
     * Construye el acta.
     *
     * @throws ValidacionElectoralException si no se definio la mesa
     */
    public Acta construir() {
        if (mesa == null) {
            throw new ValidacionElectoralException("No se puede construir un acta sin mesa");
        }
        LocalDateTime fecha = fechaCierre == null ? LocalDateTime.now() : fechaCierre;
        Acta acta = new Acta(mesa, fecha);
        resultados.forEach(acta::agregarResultado);
        return acta;
    }
}

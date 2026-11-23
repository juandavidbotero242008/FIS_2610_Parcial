package co.edu.javeriana.fis.electoral.dominio;

import co.edu.javeriana.fis.electoral.excepcion.ValidacionElectoralException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Acta de escrutinio levantada al cierre de una mesa.
 *
 * <p>Contiene los votos obtenidos por cada candidato. Su construccion se
 * realiza preferentemente con {@code ActaBuilder} (patron Builder), que
 * centraliza las validaciones de creacion. Las reglas de validacion propias
 * del tipo de eleccion se aplican con los validadores creados por la fabrica
 * (patron Factory Method).</p>
 */
public class Acta {

    private final Mesa mesa;
    private final LocalDateTime fechaCierre;
    private final Map<Candidato, Integer> resultados = new LinkedHashMap<>();
    private boolean cerrada;

    public Acta(Mesa mesa, LocalDateTime fechaCierre) {
        if (mesa == null) {
            throw new IllegalArgumentException("El acta debe estar asociada a una mesa");
        }
        if (fechaCierre == null) {
            throw new IllegalArgumentException("La fecha de cierre del acta es obligatoria");
        }
        this.mesa = mesa;
        this.fechaCierre = fechaCierre;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public boolean estaCerrada() {
        return cerrada;
    }

    /**
     * Registra los votos de un candidato.
     *
     * @throws IllegalArgumentException      si el candidato es nulo o los votos son negativos
     * @throws ValidacionElectoralException  si el acta ya esta cerrada o el candidato ya fue registrado
     */
    public void agregarResultado(Candidato candidato, int votos) {
        if (candidato == null) {
            throw new IllegalArgumentException("El candidato es obligatorio");
        }
        if (votos < 0) {
            throw new IllegalArgumentException("Los votos no pueden ser negativos");
        }
        if (cerrada) {
            throw new ValidacionElectoralException("No se pueden modificar los resultados de un acta cerrada");
        }
        if (resultados.containsKey(candidato)) {
            throw new ValidacionElectoralException(
                    "El candidato " + candidato.getNombre() + " ya tiene un resultado en esta acta");
        }
        resultados.put(candidato, votos);
    }

    /** Total de votos consignados en el acta. */
    public int totalVotos() {
        return resultados.values().stream().mapToInt(Integer::intValue).sum();
    }

    /** Votos de un candidato; 0 si no aparece en el acta. */
    public int votosDe(Candidato candidato) {
        if (candidato == null) {
            throw new IllegalArgumentException("El candidato es obligatorio");
        }
        return resultados.getOrDefault(candidato, 0);
    }

    /**
     * Candidato con mayor votacion. Devuelve vacio si el acta no tiene
     * resultados o si hay un empate en el primer lugar, porque en ese caso no
     * existe un ganador unico.
     */
    public Optional<Candidato> candidatoGanador() {
        Candidato ganador = null;
        int maximo = -1;
        boolean empate = false;
        for (Map.Entry<Candidato, Integer> entrada : resultados.entrySet()) {
            int votos = entrada.getValue();
            if (votos > maximo) {
                maximo = votos;
                ganador = entrada.getKey();
                empate = false;
            } else if (votos == maximo) {
                empate = true;
            }
        }
        return empate ? Optional.empty() : Optional.ofNullable(ganador);
    }

    /**
     * Cierra el acta para que no admita mas cambios.
     *
     * @throws ValidacionElectoralException si el acta ya esta cerrada o no tiene resultados
     */
    public void cerrar() {
        if (cerrada) {
            throw new ValidacionElectoralException("El acta ya se encuentra cerrada");
        }
        if (resultados.isEmpty()) {
            throw new ValidacionElectoralException("No se puede cerrar un acta sin resultados");
        }
        this.cerrada = true;
    }

    /** Vista inmutable de los resultados registrados. */
    public Map<Candidato, Integer> getResultados() {
        return Collections.unmodifiableMap(resultados);
    }
}

package co.edu.javeriana.fis.electoral.patrones.estructural.facade;

import co.edu.javeriana.fis.electoral.dominio.UnidadTerritorial;

/**
 * Resumen consolidado de una unidad territorial.
 *
 * @param nombre       nombre de la unidad
 * @param censo        censo total
 * @param votos        votos registrados
 * @param participacion porcentaje de participacion (0 si el censo es 0)
 */
public record ResumenConsolidado(String nombre, int censo, int votos, double participacion) {

    public static ResumenConsolidado de(UnidadTerritorial unidad) {
        if (unidad == null) {
            throw new IllegalArgumentException("La unidad territorial es obligatoria");
        }
        int censo = unidad.censoTotal();
        int votos = unidad.totalVotos();
        double participacion = censo == 0 ? 0.0 : votos * 100.0 / censo;
        return new ResumenConsolidado(unidad.nombre(), censo, votos, participacion);
    }
}

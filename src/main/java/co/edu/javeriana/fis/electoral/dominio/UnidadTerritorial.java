package co.edu.javeriana.fis.electoral.dominio;

import java.util.List;

/**
 * Componente del patron <b>Composite</b>.
 *
 * <p>Representa cualquier nivel de la jerarquia territorial electoral:
 * Departamento (compuesto), Municipio (compuesto), Puesto de votacion
 * (compuesto) y Mesa (hoja). Gracias a esta interfaz comun, la consolidacion
 * de resultados se puede hacer de forma recursiva sin conocer el nivel
 * concreto sobre el que se consulta.</p>
 */
public interface UnidadTerritorial {

    /** Nombre legible de la unidad territorial. */
    String nombre();

    /** Suma del censo de todas las mesas contenidas. */
    int censoTotal();

    /** Suma de los votos registrados en las actas de las mesas contenidas. */
    int totalVotos();

    /** Mesas contenidas en la unidad (una sola si la unidad es una hoja). */
    List<Mesa> mesas();

    /** Agrega una unidad hija. Las hojas no permiten hijos. */
    void agregar(UnidadTerritorial unidad);

    /** Remueve una unidad hija. Las hojas no permiten hijos. */
    void remover(UnidadTerritorial unidad);

    /** Unidades hijas directas; vacio para las hojas. */
    List<? extends UnidadTerritorial> hijos();
}

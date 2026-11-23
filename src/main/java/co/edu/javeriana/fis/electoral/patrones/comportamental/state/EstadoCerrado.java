package co.edu.javeriana.fis.electoral.patrones.comportamental.state;

/** Estado final: el proceso fue cerrado y sus resultados son definitivos. */
public class EstadoCerrado extends EstadoProcesoBase {

    @Override
    public String nombre() {
        return "CERRADO";
    }
}

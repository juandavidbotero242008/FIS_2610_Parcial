package co.edu.javeriana.fis.electoral.patrones.comportamental.state;

/** Estado final: el proceso fue cancelado antes de completarse. */
public class EstadoCancelado extends EstadoProcesoBase {

    @Override
    public String nombre() {
        return "CANCELADO";
    }
}

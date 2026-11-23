package co.edu.javeriana.fis.electoral.patrones.comportamental.state;

import co.edu.javeriana.fis.electoral.dominio.ProcesoElectoral;

/** Estado inicial: el proceso esta en el calendario pero aun no inicia. */
public class EstadoProgramado extends EstadoProcesoBase {

    @Override
    public String nombre() {
        return "PROGRAMADO";
    }

    @Override
    public void iniciar(ProcesoElectoral proceso) {
        proceso.establecerEstado(new EstadoEnCurso());
    }

    @Override
    public void cancelar(ProcesoElectoral proceso) {
        proceso.establecerEstado(new EstadoCancelado());
    }
}

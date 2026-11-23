package co.edu.javeriana.fis.electoral.patrones.comportamental.state;

import co.edu.javeriana.fis.electoral.dominio.ProcesoElectoral;

/** Estado de jornada electoral: se instalan mesas y se registran actas. */
public class EstadoEnCurso extends EstadoProcesoBase {

    @Override
    public String nombre() {
        return "EN_CURSO";
    }

    @Override
    public void pasarAEscrutinio(ProcesoElectoral proceso) {
        proceso.establecerEstado(new EstadoEscrutinio());
    }

    @Override
    public void cancelar(ProcesoElectoral proceso) {
        proceso.establecerEstado(new EstadoCancelado());
    }
}

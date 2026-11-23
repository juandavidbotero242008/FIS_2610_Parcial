package co.edu.javeriana.fis.electoral.patrones.comportamental.state;

import co.edu.javeriana.fis.electoral.dominio.ProcesoElectoral;

/** Estado de escrutinio: las actas se consolidan y se atienden reclamaciones. */
public class EstadoEscrutinio extends EstadoProcesoBase {

    @Override
    public String nombre() {
        return "ESCRUTINIO";
    }

    @Override
    public void cerrar(ProcesoElectoral proceso) {
        proceso.establecerEstado(new EstadoCerrado());
    }
}

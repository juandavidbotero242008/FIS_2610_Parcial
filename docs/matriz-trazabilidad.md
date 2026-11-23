# Matriz de trazabilidad — métodos vs. pruebas

## 1. Criterio de alcance

El enunciado pide "al menos dos pruebas por método" para cada entidad. Aplicamos el siguiente criterio, documentado para que sea verificable:

1. Se consideran **métodos de negocio** los métodos públicos con lógica o reglas: operaciones del dominio, transiciones, validaciones y composición.
2. Se **excluyen** getters/setters triviales, `equals`, `hashCode` y `toString`, porque no contienen lógica de negocio y su inclusión inflaría la suite sin aportar cobertura de comportamiento.
3. Los **constructores** se incluyen en la matriz por sus validaciones de datos obligatorios.
4. Cada método de negocio tiene **al menos dos pruebas**: una de camino feliz (P) y una negativa (N) o de caso límite (L).
5. La suite completa tiene **273 pruebas**; los nombres exactos están en `src/test/java` y en `target/surefire-reports/`.

**Leyenda:** P = camino feliz · N = caso negativo (excepción) · L = caso límite (vacío, cero, empate, censo exacto).

## 2. Entidades del dominio

### 2.1 Acta (`ActaTest`, 20 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `agregarResultado` | `agregarResultadoValido`, `agregarResultadoNuloFalla`, `agregarResultadoNegativoFalla`, `agregarResultadoDuplicadoFalla`, `agregarResultadoTrasCerrarFalla` | P, N, L, N, N |
| `totalVotos` | `totalVotosVacio`, `totalVotosSuma` | L, P |
| `votosDe` | `votosDeCandidatoPresente`, `votosDeCandidatoAusente`, `votosDeNuloFalla` | P, L, N |
| `candidatoGanador` | `ganadorUnico`, `ganadorEmpatado`, `ganadorSinResultados` | P, L, L |
| `cerrar` | `cerrarValido`, `cerrarSinResultadosFalla`, `cerrarDosVecesFalla` | P, L, N |
| `getResultados` | `resultadosConservanRegistros`, `resultadosInmutables` | P, P |
| constructor | `constructorInvalidoFalla`, `gettersConservanDatos` | N, P |

### 2.2 Candidato (`CandidatoTest`, 16 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `agregarPartido` | `agregarPartidoValido`, `agregarPartidoNuloFalla`, `agregarPartidoDuplicadoFalla` | P, N, N |
| `retirarPartido` | `retirarPartidoValido`, `retirarPartidoAusenteFalla`, `retirarUltimoPartidoActivoFalla`, `retirarUltimoPartidoInactivoPermitido`, `retirarPartidoNuloFalla` | P, N, L, L, N |
| `estaRespaldadoPor` | `sinRespaldoRetornaFalso`, `estaRespaldadoNuloFalla` | L, N |
| `retirarCandidatura` | `retirarCandidaturaValida`, `retirarCandidaturaDosVecesFalla` | P, N |
| `reactivarCandidatura` | `reactivarValida`, `reactivarActivoFalla` | P, N |
| constructor | `constructorInvalidoFalla`, `gettersConservanDatos` | N, P |

### 2.3 Departamento (`DepartamentoTest`, 15 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `agregar` | `agregarMunicipioValido`, `agregarNoMunicipioFalla`, `agregarMunicipioDuplicadoFalla`, `agregarMunicipioDeOtroDepartamentoFalla` | P, N, N, N |
| `remover` | `removerMunicipioValido`, `removerMunicipioAjenoFalla`, `removerNoMunicipioFalla` | P, N, N |
| `buscarMunicipio` | `buscarMunicipioExistente`, `buscarMunicipioInexistente` | P, L |
| `nombre` | `nombreRetornaNombre`, `nombreNoEsCodigo` | P, P |
| `censoTotal` / `totalVotos` / `mesas` | `totalesVacios`, `totalesConJerarquia` | L, P |
| `hijos` | `totalesVacios`, `agregarMunicipioValido` | L, P |
| constructor | `constructorInvalidoFalla`, `constructorConservaDatos` | N, P |

### 2.4 Incidente (`IncidenteTest`, 12 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `resolver` | `resolverValido`, `resolverDosVecesFalla`, `resolverSinObservacionFalla` | P, N, N |
| `reabrir` | `reabrirValido`, `reabrirSinResolverFalla` | P, N |
| `esCritico` | `ordenPublicoEsCritico`, `irregularidadEsCritica`, `fallaTecnicaNoEsCritica` | P, P, L |
| `estaResuelto` | `iniciaSinResolver`, `resolverValido` | L, P |
| constructor | `constructorInvalidoFalla`, `fechaPorDefecto` | N, P |

### 2.5 Jurado (`JuradoTest`, 17 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `asignar` | `asignarValido`, `asignarMesaNulaFalla`, `asignarFuncionNulaFalla`, `asignarDuplicadoFalla`, `asignarMesaCerradaFalla`, `mesaInstaladaPermiteAsignacion` | P, N, N, N, N, P |
| `liberar` | `liberarValido`, `liberarSinAsignacionFalla`, `liberarMesaNulaFalla` | P, N, N |
| `funcionEn` | `funcionEnSinAsignacion`, `funcionEnMesaNulaFalla`, `asignarValido` | L, N, P |
| `totalAsignaciones` | `totalAsignacionesVacio`, `totalAsignacionesMultiples` | L, P |
| `actualizarContacto` | `actualizarContactoValido`, `actualizarContactoVacioFalla` | P, N |
| constructor | `constructorInvalidoFalla`, `constructorConservaDatos` | N, P |

### 2.6 Mesa (`MesaTest`, 25 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `instalar` | `instalarCambiaEstado`, `instalarDosVecesFalla` | P, N |
| `cerrar` | `cerrarDesdeInstalada`, `cerrarSinInstalarFalla` | P, N |
| `registrarActa` | `registrarActaValida`, `registrarActaSinCerrarMesaFalla`, `registrarActaDeOtraMesaFalla`, `registrarActaAbiertaFalla`, `registrarActaDuplicadaFalla`, `registrarActaNulaFalla` | P, N, N, N, N, N |
| `totalVotos` | `totalVotosSinActaEsCero`, `totalVotosConActaSuma` | L, P |
| `censoTotal` | `censoYMesasDeLaHoja`, `censoTotalConOtroValor` | P, P |
| `mesas` | `mesasConUnaSolaHoja`, `censoYMesasDeLaHoja` | P, P |
| `hijos` | `hijosVacioEnMesa`, `censoYMesasDeLaHoja` | L, P |
| `agregar` | `agregarFallaEnHoja`, `agregarNuloFalla` | N, N |
| `remover` | `removerFallaEnHoja`, `removerNuloFalla` | N, N |
| `nombre` | `nombreConNumero`, `nombreConOtroNumero` | P, P |
| constructor / vínculo | `numeroInvalidoFalla`, `censoInvalidoFalla`, `puestoSeVinculaAlAgregar` | N, N, P |

### 2.7 Municipio (`MunicipioTest`, 13 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `agregar` | `agregarPuestoValido`, `agregarNoPuestoFalla`, `agregarPuestoDuplicadoFalla` | P, N, N |
| `remover` | `removerPuestoValido`, `removerPuestoAjenoFalla`, `removerNoPuestoFalla` | P, N, N |
| `buscarPuesto` | `buscarPuestoExistente`, `buscarPuestoInexistente` | P, L |
| `nombre` | `nombreRetornaNombre`, `nombreNoEsCodigo` | P, P |
| `censoTotal` / `totalVotos` / `mesas` / `hijos` | `totalesVacios`, `totalesConJerarquia` | L, P |
| constructor | `constructorInvalidoFalla` | N |

### 2.8 Partido (`PartidoTest`, 15 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `agregarCandidato` | `agregarCandidatoValido`, `agregarCandidatoNuloFalla`, `agregarCandidatoDuplicadoFalla` | P, N, N |
| `retirarCandidato` | `retirarCandidatoValido`, `retirarCandidatoAjenoFalla`, `retirarUltimoRespaldoFalla`, `retirarCandidatoNuloFalla` | P, N, L, N |
| `numeroCandidatos` | `numeroCandidatosVacio`, `numeroCandidatosConDatos` | L, P |
| `tieneCandidato` | `tieneCandidatoFalso`, `tieneCandidatoNuloFalla` | L, N |
| `getCandidatos` | `candidatosConDatos`, `candidatosInmutables` | P, P |
| constructor | `constructorInvalidoFalla`, `constructorConservaDatos` | N, P |

### 2.9 ProcesoElectoral (`ProcesoElectoralTest`, 19 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `iniciar` | `cicloDeVidaCompleto`, `iniciarDosVecesFalla` | P, N |
| `pasarAEscrutinio` | `cicloDeVidaCompleto`, `pasarAEscrutinioSinIniciarFalla` | P, N |
| `cerrar` | `cicloDeVidaCompleto`, `cerrarSinEscrutinioFalla` | P, N |
| `cancelar` | `cancelarProgramado`, `cancelarCerradoFalla` | P, N |
| `agregarTerritorio` | `agregarTerritorioProgramado`, `agregarTerritorioEnCursoFalla`, `agregarTerritorioDuplicadoFalla` | P, N, N |
| `removerTerritorio` | `removerTerritorioValido`, `removerTerritorioAjenoFalla`, `removerTerritorioEnCursoFalla` | P, N, N |
| `buscarDepartamento` | `agregarTerritorioProgramado`, `buscarDepartamentoInexistente` | P, L |
| `estadoActual` | `cicloDeVidaCompleto`, `estadoInicialEsProgramado` | P, L |
| `establecerEstado` | `establecerEstadoCambiaEstado`, `establecerEstadoNuloFalla` | P, N |
| `censoTotal` / `totalVotos` / `mesas` | `totalesSinTerritorios`, `totalesConJerarquia` | L, P |
| constructor | `constructorInvalidoFalla` | N |

### 2.10 PuestoVotacion (`PuestoVotacionTest`, 19 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `agregar` | `agregarMesaValida`, `agregarNoMesaFalla`, `agregarDuplicadaFalla`, `agregarMesaDeOtroPuestoFalla` | P, N, N, N |
| `remover` | `removerMesaValida`, `removerMesaAjenaFalla`, `removerNoMesaFalla` | P, N, N |
| `buscarMesa` | `buscarMesaExistente`, `buscarMesaInexistente` | P, L |
| `nombre` | `nombreRetornaNombre`, `nombreNoEsId` | P, P |
| `censoTotal` / `totalVotos` | `totalesVaciosSonCero`, `totalesConMesas` | L, P |
| `mesas` | `mesasConDatos`, `mesasInmutables` | P, P |
| `hijos` | `hijosContieneMesas`, `hijosVacio` | P, L |
| constructor | `constructorConservaDatos`, `constructorInvalidoFalla` | P, N |

### 2.11 Reclamacion (`ReclamacionTest`, 15 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `iniciarRevision` | `iniciarRevisionValida`, `iniciarRevisionDosVecesFalla` | P, N |
| `resolver` | `resolverValido`, `resolverSinRevisionFalla`, `resolverSinDecisionFalla` | P, N, N |
| `rechazar` | `rechazarDesdeRadicada`, `rechazarDesdeRevision`, `rechazarResueltaFalla`, `rechazarSinMotivoFalla` | P, P, N, N |
| `estaAbierta` | `estaAbiertaEnEstadosIniciales`, `cerradaTrasResolver` | P, L |
| constructor | `constructorSinSolicitanteFalla`, `constructorConTestigoValido`, `constructorInvalidoFalla` | N, P, N |

### 2.12 TestigoElectoral (`TestigoElectoralTest`, 8 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `cambiarPartido` | `cambiarPartidoValido`, `cambiarPartidoNuloFalla`, `cambiarAlMismoPartidoFalla` | P, N, N |
| `esDelPartido` | `esDelPartidoVerdadero`, `esDelPartidoFalso`, `esDelPartidoNuloFalla` | P, L, N |
| constructor | `constructorInvalidoFalla`, `gettersConservanDatos` | N, P |

## 3. Patrones GoF

### 3.1 Builder (`ActaBuilderTest`, 10 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `conMesa` | `construirValido`, `conMesaNulaFalla` | P, N |
| `conFechaCierre` | `construirValido`, `conFechaNulaFalla`, `fechaPersonalizada` | P, N, P |
| `agregarResultado` | `construirValido`, `agregarCandidatoNuloFalla`, `agregarVotosNegativosFalla`, `agregarDuplicadoFalla` | P, N, L, N |
| `construir` | `construirValido`, `construirSinResultados`, `construirSinMesaFalla`, `construirSinFechaUsaAhora` | P, L, N, P |

### 3.2 Factory Method (`FabricaValidadoresTest`, 13 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `FabricaValidadores.para` | `creadorLegislativo`, `creadorPresidencial`, `creadorConsulta`, `tipoNuloFalla` | P, P, P, N |
| `CreadorValidadorActa.crearValidador` | `creadorLegislativo`, `creadorPresidencial`, `creadorConsulta` | P ×3 |
| `validar` (legislativo) | `legislativoValido`, `legislativoSinVotosFalla`, `legislativoExcedeCensoFalla` | P, L, N |
| `validar` (presidencial) | `presidencialValido`, `presidencialUnCandidatoFalla` | P, L |
| `validar` (consulta) | `consultaSinVotosValida`, `consultaExcedeCensoFalla` | L, N |
| `validar` (común) | `actaNulaFalla`, `votosIgualesAlCenso` | N, L |

### 3.3 Composite

El patrón Composite se implementa en las entidades territoriales y se prueba en `DepartamentoTest`, `MunicipioTest`, `PuestoVotacionTest` y `MesaTest` (47 pruebas en total). Cubre:

- Composición válida y rechazo del tipo incorrecto en cada nivel.
- Duplicados y pertenencia a otro padre.
- Hojas que rechazan `agregar`/`remover`.
- Consolidación recursiva de censo, votos y mesas.
- Colecciones vacías (caso límite).

### 3.4 Facade (`ServicioElectoralFacadeTest`, 27 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `registrarActa` | `registrarActaExitosa`, `registrarActaProcesoNoIniciadoFalla`, `registrarActaMesaAjenaFalla`, `registrarActaMesaNoCerradaFalla`, `registrarActaExcedeCensoFalla`, `registrarActaDuplicadaFalla`, `registrarActaNulaFalla` | P, N, N, N, N, N, N |
| `radicarReclamacion` | `radicarReclamacionValida`, `radicarReclamacionConTestigo`, `radicarReclamacionSinActaFalla`, `radicarReclamacionDuplicadaFalla`, `radicarReclamacionProcesoCerradoFalla`, `radicarReclamacionNulaFalla` | P, P, N, N, N, N |
| `iniciarRevision` | `iniciarRevisionValida`, `iniciarRevisionInexistenteFalla`, `iniciarRevisionIdVacioFalla` | P, N, N |
| `resolverReclamacion` | `resolverReclamacionValida`, `resolverReclamacionInexistenteFalla` | P, N |
| `rechazarReclamacion` | `rechazarReclamacionValida` | P |
| `consolidar` | `consolidarDepartamento`, `consolidarVacio`, `consolidarNuloFalla` | P, L, N |
| `actaDe` | `actaDeMesaSinActa`, `actaDeNulaFalla`, `registrarActaExitosa` | L, N, P |
| constructor / getters | `constructorInvalidoFalla`, `getActasReflejaRegistros` | N, P |

### 3.5 State (`EstadoProcesoTest` + `ProcesoElectoralTest`, 28 pruebas)

| Estado / método | Pruebas | Tipo |
|---|---|---|
| `EstadoProgramado.iniciar` | `programadoInicia` | P |
| `EstadoProgramado.pasarAEscrutinio` | `programadoNoPasaAEscrutinio` | N |
| `EstadoProgramado.cancelar` | `programadoCancela` | P |
| `EstadoEnCurso.pasarAEscrutinio` | `enCursoPasaAEscrutinio` | P |
| `EstadoEnCurso.cerrar` | `enCursoNoCierra` | N |
| `EstadoEscrutinio.cerrar` | `escrutinioCierra` | P |
| `EstadoEscrutinio.cancelar` | `escrutinioNoCancela` | N |
| `EstadoCerrado` | `cerradoNoTransiciona` | N |
| `EstadoCancelado` | `canceladoNoTransiciona` | N |
| Ciclo completo | `cicloDeVidaCompleto` | P |

### 3.6 Observer (`PublicadorEventosTest` + `ServicioElectoralFacadeTest`, 46 pruebas)

| Método | Pruebas | Tipo |
|---|---|---|
| `PublicadorEventos.suscribir` | `suscribirValido`, `suscribirDuplicadoFalla`, `suscribirNuloFalla` | P, N, N |
| `PublicadorEventos.desuscribir` | `desuscribirValido`, `desuscribirAusenteFalla`, `desuscribirNuloFalla` | P, N, N |
| `PublicadorEventos.publicar` | `publicarNotificaATodos`, `publicarSinObservadores`, `publicarNuloFalla`, `observadorPuedeDesuscribirseAlRecibir` | P, L, N, P |
| `ObservadorAuditoria.actualizar` | `auditoriaAlmacenaEventos`, `auditoriaEventoNuloFalla` | P, N |
| `ObservadorAnalitica.actualizar` | `analiticaCuentaPorTipo`, `analiticaRecibeEventos` | P, P |
| `ObservadorAnalitica.conteo` | `analiticaTipoSinEventos`, `analiticaTipoNuloFalla` | L, N |
| `ObservadorPartido.actualizar` | `partidoFiltraEventos`, `partidoIgnoraEventosSinPartido`, `partidoEventoNuloFalla` | P, L, N |
| `EventoElectoral` | `eventoValidaCampos` | N |

## 4. Resumen de cobertura por clase de prueba

| Clase de prueba | Pruebas |
|---|---:|
| `ActaTest` | 20 |
| `CandidatoTest` | 16 |
| `DepartamentoTest` | 15 |
| `IncidenteTest` | 12 |
| `JuradoTest` | 17 |
| `MesaTest` | 25 |
| `MunicipioTest` | 13 |
| `PartidoTest` | 15 |
| `ProcesoElectoralTest` | 19 |
| `PuestoVotacionTest` | 19 |
| `ReclamacionTest` | 15 |
| `TestigoElectoralTest` | 8 |
| `ActaBuilderTest` | 10 |
| `FabricaValidadoresTest` | 13 |
| `EstadoProcesoTest` | 9 |
| `PublicadorEventosTest` | 20 |
| `ServicioElectoralFacadeTest` | 27 |
| **Total** | **273** |

> Las cifras se verifican con `mvn test`; el reporte XML de Surefire es la fuente de verdad para la evidencia del informe.

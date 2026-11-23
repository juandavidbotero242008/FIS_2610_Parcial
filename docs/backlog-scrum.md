# Backlog Scrum — Sistema de Información Electoral

## 1. Equipo y roles

| Rol | Responsable |
|---|---|
| Product Owner | Juan David Botero Avila |
| Scrum Master | Esteban Cantillo Valverde |
| Development Team | Santiago Baez Parra · Sergio Pulido Grimaldo |

**Cadencia:** 3 sprints de 1 semana.
**Herramienta de gestión:** GitHub Issues con etiquetas de sprint (`sprint-1`, `sprint-2`, `sprint-3`).

## 2. Definition of Done (DoD)

Una historia se considera terminada cuando:

1. El código compila con `mvn clean compile`.
2. Las pruebas del método afectado están en verde.
3. Cada método nuevo tiene al menos dos pruebas: camino feliz y negativa o de límite.
4. La cobertura global no baja del 70 % (quality gate de JaCoCo).
5. El commit sigue *Conventional Commits* y referencia la historia.
6. La evidencia (captura, salida de pruebas o enlace al run) quedó en el informe.

## 3. Historias de usuario e issues

### Sprint 1 — Dominio y estructura territorial

#### US-01 · Modelo territorial con Composite
- **Como** autoridad electoral **quiero** organizar departamentos, municipios, puestos y mesas en una jerarquía **para** consolidar censo y votos por territorio.
- **Patrón:** Composite (estructural).
- **Criterios de aceptación:**
  - [ ] `UnidadTerritorial` define `censoTotal()`, `totalVotos()`, `mesas()`, `agregar()` y `remover()`.
  - [ ] Departamento solo acepta municipios; municipio solo puestos; puesto solo mesas.
  - [ ] Una mesa (hoja) rechaza operaciones de composición.
  - [ ] Los totales se calculan recursivamente y una unidad vacía retorna 0.
- **Story points:** 5.

#### US-02 · Registro del proceso electoral y su calendario
- **Como** funcionario **quiero** registrar el proceso electoral con tipo, fecha y estado **para** controlar su avance en el calendario.
- **Patrón:** State (comportamental).
- **Criterios de aceptación:**
  - [ ] Estados: PROGRAMADO → EN_CURSO → ESCRUTINIO → CERRADO; cancelación desde PROGRAMADO o EN_CURSO.
  - [ ] Las transiciones inválidas lanzan `TransicionEstadoInvalidaException`.
  - [ ] Solo se configura territorio en estado PROGRAMADO.
- **Story points:** 5.

#### US-03 · Partidos, candidatos y respaldos
- **Como** responsable del proceso **quiero** registrar partidos y candidatos con sus respaldos **para** conocer quién participa y bajo qué organización.
- **Criterios de aceptación:**
  - [ ] La relación partido-candidato se sincroniza en ambas direcciones.
  - [ ] No se permiten duplicados ni candidaturas activas sin partido.
  - [ ] Un candidato puede retirarse y reactivarse.
- **Story points:** 3.

#### US-04 · Designación de jurados
- **Como** autoridad electoral **quiero** asignar jurados a mesas con una función **para** garantizar la operación de la jornada.
- **Criterios de aceptación:**
  - [ ] Funciones: PRESIDENTE, VICEPRESIDENTE, SECRETARIO, VOCAL.
  - [ ] Un jurado no se asigna dos veces a la misma mesa.
  - [ ] No se asigna un jurado a una mesa cerrada.
- **Story points:** 3.

### Sprint 2 — Actas, patrones creacionales y comportamiento

#### US-05 · Construcción validada de actas (Builder)
- **Como** jurado **quiero** registrar el acta de cierre con los votos por candidato **para** dejar constancia de los resultados de la mesa.
- **Patrón:** Builder (creacional).
- **Criterios de aceptación:**
  - [ ] El builder exige mesa y admite fecha y resultados.
  - [ ] Rechaza votos negativos y candidatos duplicados.
  - [ ] El acta no se puede cerrar sin resultados ni modificarse después del cierre.
- **Story points:** 3.

#### US-06 · Validación según el tipo de elección (Factory Method)
- **Como** autoridad electoral **quiero** aplicar reglas distintas por tipo de elección **para** asegurar actas consistentes.
- **Patrón:** Factory Method (creacional).
- **Criterios de aceptación:**
  - [ ] Legislativa exige al menos un candidato con votos.
  - [ ] Presidencial exige al menos dos candidatos con votos.
  - [ ] Consulta popular admite cero votos.
  - [ ] Ningún tipo admite votos superiores al censo.
- **Story points:** 3.

#### US-07 · Notificación de eventos y auditoría (Observer)
- **Como** analista **quiero** recibir notificaciones cuando se registra un acta o una reclamación **para** auditar y monitorear el proceso.
- **Patrón:** Observer (comportamental).
- **Criterios de aceptación:**
  - [ ] El publicador admite suscribir, desuscribir y publicar.
  - [ ] Auditoría conserva la bitácora; analítica cuenta eventos por tipo.
  - [ ] Cada partido recibe solo los eventos que le competen.
- **Story points:** 5.

#### US-08 · Reclamaciones e incidentes
- **Como** partido o testigo **quiero** radicar reclamaciones sobre actas **para** solicitar la revisión de resultados.
- **Criterios de aceptación:**
  - [ ] Flujo RADICADA → EN_REVISION → RESUELTA, con rechazo desde estados abiertos.
  - [ ] No se radican reclamaciones sobre actas no registradas ni con id duplicado.
  - [ ] Los incidentes se resuelven y reabren con observación.
- **Story points:** 5.

#### US-09 · Fachada de servicios electorales (Facade)
- **Como** analista **quiero** una interfaz única para registrar actas, tramitar reclamaciones y consolidar resultados **para** simplificar los casos de uso.
- **Patrón:** Facade (estructural).
- **Criterios de aceptación:**
  - [ ] `registrarActa` valida etapa, pertenencia de la mesa, tipo de elección y duplicados.
  - [ ] La consolidación retorna censo, votos y participación.
  - [ ] La fachada publica eventos sin acoplar al observador concreto.
- **Story points:** 5.

### Sprint 3 — Calidad, CI/CD y documentación

#### US-10 · Suite de pruebas unitarias y matriz de trazabilidad
- **Como** equipo de desarrollo **quiero** una suite de pruebas con casos negativos y de límite **para** asegurar la calidad del sistema.
- **Criterios de aceptación:**
  - [x] 273 pruebas en verde.
  - [ ] Cada método de negocio tiene al menos dos pruebas.
  - [ ] Cobertura de instrucciones ≥ 70 %.
  - [ ] Matriz de trazabilidad publicada.
- **Story points:** 8.

#### US-11 · Pipeline CI/CD en GitHub Actions
- **Como** equipo **quiero** un pipeline que valide cada push **para** detectar errores temprano.
- **Criterios de aceptación:**
  - [ ] Se dispara con push a la rama del equipo y con pull requests.
  - [ ] Ejecuta `mvn clean verify` y publica reportes.
  - [ ] Falla si baja la cobertura mínima.
  - [ ] Evidencia del run exitoso en el informe.
- **Story points:** 5.

#### US-12 · Informe final y evidencias
- **Como** equipo **quiero** documentar diseño, patrones, pruebas y DevOps **para** sustentar la entrega.
- **Criterios de aceptación:**
  - [ ] Informe con justificación de cada patrón y fragmentos de código.
  - [ ] Evidencias de pruebas, cobertura y pipeline.
  - [ ] Historial de commits convencionales e issues cerrados.
- **Story points:** 5.

## 4. Resumen del sprint backlog

| Sprint | Issues | Story points |
|---|---|---|
| Sprint 1 | US-01, US-02, US-03, US-04 | 16 |
| Sprint 2 | US-05, US-06, US-07, US-08, US-09 | 21 |
| Sprint 3 | US-10, US-11, US-12 | 18 |
| **Total** | **12 historias** | **55** |

## 5. Cómo crear los issues

Los 12 issues ya están publicados y cerrados en <https://github.com/juandavidbotero242008/FIS_2610_Parcial/issues?q=is%3Aissue>.

Para recrearlos con GitHub CLI:

```bash
bash scripts/crear-issues.sh juandavidbotero242008/FIS_2610_Parcial
```

Opción manual: crear un issue por cada historia usando la plantilla *Historia de usuario*, con título `[US-XX] Nombre`, etiquetas `historia`, `scrum` y el sprint correspondiente (`sprint-1`, `sprint-2`, `sprint-3`).

## 6. Evidencias de Scrum

1. Los 12 issues están cerrados y etiquetados por sprint: [`docs/evidencias/06-issues.png`](evidencias/06-issues.png).
2. Cada issue tiene un comentario con el commit (`ee88120`, `0086e42`, `dc37def`, etc.) que implementa la historia.
3. El historial de commits convencionales está en [`docs/evidencias/08-commits.png`](evidencias/08-commits.png).
4. No se usó GitHub Projects; el estado del sprint se evidencia con las etiquetas `sprint-1`, `sprint-2` y `sprint-3`.
5. Burndown por sprint: opcional, no incluido.

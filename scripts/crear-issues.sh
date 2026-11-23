#!/usr/bin/env bash
# Crea el backlog Scrum del proyecto en GitHub Issues usando GitHub CLI.
# Uso: ./scripts/crear-issues.sh juandavidbotero242008/FIS_2610_Parcial
# Requiere: gh autenticado (gh auth login) y permisos sobre el repositorio.

set -euo pipefail

REPO="${1:-}"
if [[ -z "$REPO" ]]; then
  echo "Uso: $0 OWNER/REPO" >&2
  exit 1
fi

if ! command -v gh >/dev/null 2>&1; then
  echo "GitHub CLI (gh) no esta instalado. Instalelo desde https://cli.github.com/" >&2
  exit 1
fi

crear_label() {
  gh label create "$1" --repo "$REPO" --color "$2" --description "$3" --force >/dev/null
}

crear_label "historia" "1D76DB" "Historia de usuario del backlog"
crear_label "scrum" "5319E7" "Gestion Scrum"
crear_label "sprint-1" "0E8A16" "Sprint 1: dominio y territorio"
crear_label "sprint-2" "FBCA04" "Sprint 2: actas y comportamiento"
crear_label "sprint-3" "B60205" "Sprint 3: calidad, CI/CD e informe"
crear_label "creacional" "C2E0C6" "Patron creacional"
crear_label "estructural" "BFDADC" "Patron estructural"
crear_label "comportamental" "D4C5F9" "Patron comportamental"
crear_label "testing" "F9D0C4" "Pruebas unitarias y cobertura"
crear_label "devops" "EDEDED" "CI/CD y automatizacion"
crear_label "docs" "0075CA" "Documentacion e informe"

crear_issue() {
  local titulo="$1"; shift
  local etiquetas="$1"; shift
  local cuerpo="$1"
  gh issue create --repo "$REPO" --title "$titulo" --body "$cuerpo" --label "$etiquetas"
}

crear_issue "[US-01] Modelo territorial con Composite" \
  "historia,scrum,sprint-1,estructural" \
  "**Como** autoridad electoral **quiero** organizar departamentos, municipios, puestos y mesas **para** consolidar censo y votos por territorio.

**Patron:** Composite (estructural).

### Criterios de aceptacion
- [ ] UnidadTerritorial define censoTotal, totalVotos, mesas, agregar y remover.
- [ ] Cada nivel valida el tipo de hijo permitido.
- [ ] Una mesa (hoja) rechaza operaciones de composicion.
- [ ] Los totales se calculan recursivamente y una unidad vacia retorna 0.

**Story points:** 5"

crear_issue "[US-02] Registro del proceso electoral y su calendario" \
  "historia,scrum,sprint-1,comportamental" \
  "**Como** funcionario **quiero** registrar el proceso electoral con tipo, fecha y estado **para** controlar su avance.

**Patron:** State (comportamental).

### Criterios de aceptacion
- [ ] Estados PROGRAMADO -> EN_CURSO -> ESCRUTINIO -> CERRADO y cancelacion desde PROGRAMADO o EN_CURSO.
- [ ] Las transiciones invalidas lanzan TransicionEstadoInvalidaException.
- [ ] Solo se configura territorio en estado PROGRAMADO.

**Story points:** 5"

crear_issue "[US-03] Partidos, candidatos y respaldos" \
  "historia,scrum,sprint-1" \
  "**Como** responsable del proceso **quiero** registrar partidos y candidatos con sus respaldos **para** conocer quien participa.

### Criterios de aceptacion
- [ ] La relacion partido-candidato se sincroniza en ambos sentidos.
- [ ] No se permiten duplicados ni candidaturas activas sin partido.
- [ ] Un candidato puede retirarse y reactivarse.

**Story points:** 3"

crear_issue "[US-04] Designacion de jurados" \
  "historia,scrum,sprint-1" \
  "**Como** autoridad electoral **quiero** asignar jurados a mesas con una funcion **para** garantizar la jornada.

### Criterios de aceptacion
- [ ] Funciones PRESIDENTE, VICEPRESIDENTE, SECRETARIO y VOCAL.
- [ ] Un jurado no se asigna dos veces a la misma mesa.
- [ ] No se asigna un jurado a una mesa cerrada.

**Story points:** 3"

crear_issue "[US-05] Construccion validada de actas (Builder)" \
  "historia,scrum,sprint-2,creacional" \
  "**Como** jurado **quiero** registrar el acta de cierre con los votos por candidato **para** dejar constancia de los resultados.

**Patron:** Builder (creacional).

### Criterios de aceptacion
- [ ] El builder exige mesa y admite fecha y resultados.
- [ ] Rechaza votos negativos y candidatos duplicados.
- [ ] El acta no se cierra sin resultados ni se modifica despues del cierre.

**Story points:** 3"

crear_issue "[US-06] Validacion por tipo de eleccion (Factory Method)" \
  "historia,scrum,sprint-2,creacional" \
  "**Como** autoridad electoral **quiero** aplicar reglas distintas por tipo de eleccion **para** asegurar actas consistentes.

**Patron:** Factory Method (creacional).

### Criterios de aceptacion
- [ ] Legislativa exige al menos un candidato con votos.
- [ ] Presidencial exige al menos dos candidatos con votos.
- [ ] Consulta popular admite cero votos.
- [ ] Ningun tipo admite votos superiores al censo.

**Story points:** 3"

crear_issue "[US-07] Notificacion de eventos y auditoria (Observer)" \
  "historia,scrum,sprint-2,comportamental" \
  "**Como** analista **quiero** recibir notificaciones de actas y reclamaciones **para** auditar y monitorear.

**Patron:** Observer (comportamental).

### Criterios de aceptacion
- [ ] El publicador admite suscribir, desuscribir y publicar.
- [ ] Auditoria conserva la bitacora; analitica cuenta eventos por tipo.
- [ ] Cada partido recibe solo los eventos que le competen.

**Story points:** 5"

crear_issue "[US-08] Reclamaciones e incidentes" \
  "historia,scrum,sprint-2" \
  "**Como** partido o testigo **quiero** radicar reclamaciones sobre actas **para** solicitar revision de resultados.

### Criterios de aceptacion
- [ ] Flujo RADICADA -> EN_REVISION -> RESUELTA, con rechazo desde estados abiertos.
- [ ] No se radican reclamaciones sobre actas no registradas ni con id duplicado.
- [ ] Los incidentes se resuelven y reabren con observacion.

**Story points:** 5"

crear_issue "[US-09] Fachada de servicios electorales (Facade)" \
  "historia,scrum,sprint-2,estructural" \
  "**Como** analista **quiero** una interfaz unica para registrar actas, tramitar reclamaciones y consolidar **para** simplificar los casos de uso.

**Patron:** Facade (estructural).

### Criterios de aceptacion
- [ ] registrarActa valida etapa, pertenencia de la mesa, tipo de eleccion y duplicados.
- [ ] La consolidacion retorna censo, votos y participacion.
- [ ] La fachada publica eventos sin acoplarse al observador concreto.

**Story points:** 5"

crear_issue "[US-10] Suite de pruebas y matriz de trazabilidad" \
  "historia,scrum,sprint-3,testing" \
  "**Como** equipo **quiero** una suite de pruebas con casos negativos y de limite **para** asegurar la calidad.

### Criterios de aceptacion
- [x] 273 pruebas en verde.
- [ ] Cada metodo de negocio tiene al menos dos pruebas.
- [ ] Cobertura de instrucciones >= 70 %.
- [ ] Matriz de trazabilidad publicada.

**Story points:** 8"

crear_issue "[US-11] Pipeline CI/CD en GitHub Actions" \
  "historia,scrum,sprint-3,devops" \
  "**Como** equipo **quiero** un pipeline que valide cada push **para** detectar errores temprano.

### Criterios de aceptacion
- [ ] Se dispara con push a la rama del equipo y con pull requests.
- [ ] Ejecuta mvn clean verify y publica reportes.
- [ ] Falla si baja la cobertura minima.
- [ ] Evidencia del run exitoso en el informe.

**Story points:** 5"

crear_issue "[US-12] Informe final y evidencias" \
  "historia,scrum,sprint-3,docs" \
  "**Como** equipo **quiero** documentar diseno, patrones, pruebas y DevOps **para** sustentar la entrega.

### Criterios de aceptacion
- [ ] Informe con justificacion de cada patron y fragmentos de codigo.
- [ ] Evidencias de pruebas, cobertura y pipeline.
- [ ] Historial de commits convencionales e issues cerrados.

**Story points:** 5"

echo "Backlog creado en $REPO"

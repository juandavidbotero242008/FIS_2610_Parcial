# FIS_2610_Parcial — Sistema de Información Electoral

[![CI](https://github.com/juandavidbotero242008/FIS_2610_Parcial/actions/workflows/ci.yml/badge.svg)](https://github.com/juandavidbotero242008/FIS_2610_Parcial/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-blue)
![Maven](https://img.shields.io/badge/Maven-3.9-orange)
![JUnit](https://img.shields.io/badge/JUnit-5.11-green)
![Cobertura](https://img.shields.io/badge/cobertura-87.55%25-brightgreen)

Proyecto del **Segundo Parcial de Fundamentos de Ingeniería de Software** (Pontificia Universidad Javeriana). Modela el proceso electoral colombiano de 2026: planeación territorial, partidos y candidatos, jurados, actas de escrutinio, incidentes, reclamaciones y consolidación de resultados.

**Repositorio:** <https://github.com/juandavidbotero242008/FIS_2610_Parcial> · **Pipeline:** [GitHub Actions](https://github.com/juandavidbotero242008/FIS_2610_Parcial/actions/workflows/ci.yml) · **Issues:** [12 historias cerradas](https://github.com/juandavidbotero242008/FIS_2610_Parcial/issues?q=is%3Aissue)


## Contenido

- [Objetivo](#objetivo)
- [Patrones GoF implementados](#patrones-gof-implementados)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Cómo ejecutar](#cómo-ejecutar)
- [Pruebas y cobertura](#pruebas-y-cobertura)
- [CI/CD](#cicd)
- [Documentación](#documentación)

## Objetivo

Diseñar e implementar un sistema de información que permita:

1. Organizar el calendario electoral y la distribución territorial (departamentos, municipios, puestos y mesas).
2. Registrar partidos, candidatos, jurados y sus asignaciones.
3. Consignar los resultados de cada mesa mediante actas de cierre.
4. Registrar incidentes y tramitar reclamaciones con trazabilidad.
5. Consolidar y analizar resultados por territorio.

El proyecto aplica seis patrones GoF, pruebas unitarias con JUnit 5 y un pipeline de integración continua con GitHub Actions.

## Patrones GoF implementados

| Categoría | Patrón | Dónde | Situación que resuelve |
|---|---|---|---|
| Creacional | **Builder** | `ActaBuilder` | Construir actas con múltiples resultados y validaciones sin constructores telescópicos. |
| Creacional | **Factory Method** | `CreadorValidadorActa` + validadores | Aplicar reglas de validación distintas según el tipo de elección. |
| Estructural | **Composite** | `UnidadTerritorial`, `Departamento`, `Municipio`, `PuestoVotacion`, `Mesa` | Consolidar censo y votos recursivamente en la jerarquía territorial. |
| Estructural | **Facade** | `ServicioElectoralFacade` | Ofrecer una interfaz única para registrar actas, tramitar reclamaciones y consolidar. |
| Comportamental | **State** | `EstadoProceso` y estados concretos | Controlar el ciclo de vida del proceso electoral y rechazar transiciones inválidas. |
| Comportamental | **Observer** | `PublicadorEventos` y observadores | Notificar a auditoría, analítica y partidos cuando ocurre un evento electoral. |

El detalle, la justificación y los fragmentos de código están en [`docs/informe.md`](docs/informe.md).

## Estructura del proyecto

```text
FIS_2610_Parcial/
├── pom.xml
├── README.md
├── .github/
│   ├── workflows/ci.yml
│   ├── ISSUE_TEMPLATE/
│   └── pull_request_template.md
├── docs/
│   ├── informe.md                  # Informe final
│   ├── informe.pdf                 # Versión PDF con evidencias
│   ├── matriz-trazabilidad.md      # Métodos vs. pruebas
│   ├── backlog-scrum.md            # Historias, sprints y issues
│   ├── guia-github.md              # Runbook de Git, GitHub y evidencias
│   └── evidencias/                 # Capturas, logs, cobertura y diagramas
├── scripts/
│   ├── crear-issues.sh             # Crea el backlog con GitHub CLI
│   └── exportar_pdf.py             # Genera docs/informe.pdf
└── src/
    ├── main/java/co/edu/javeriana/fis/electoral/
    │   ├── dominio/                # Entidades y Composite
    │   ├── excepcion/              # Excepciones del dominio
    │   └── patrones/
    │       ├── creacional/         # Builder y Factory Method
    │       ├── estructural/        # Facade
    │       └── comportamental/     # State y Observer
    └── test/java/...               # 273 pruebas JUnit 5
```

## Cómo ejecutar

Requisitos: **JDK 17** y **Maven 3.9+**.

```bash
# Compilar
mvn clean compile

# Ejecutar las pruebas
mvn test

# Ejecutar pruebas + cobertura + quality gate
mvn clean verify

# Abrir el reporte de cobertura
xdg-open target/site/jacoco/index.html   # Linux
open target/site/jacoco/index.html       # macOS
```

## Pruebas y cobertura

- **273 pruebas unitarias** con JUnit 5.
- Cada método de negocio tiene al menos dos pruebas: camino feliz y caso negativo o límite.
- Cobertura actual (`mvn verify`):

| Métrica | Cobertura |
|---|---|
| Instrucciones | 87.55 % |
| Ramas | 81.43 % |
| Líneas | 90.38 % |
| Clases | 100 % |

El quality gate de JaCoCo falla la build si la cobertura de instrucciones baja del **70 %**.

## CI/CD

El workflow [`.github/workflows/ci.yml`](.github/workflows/ci.yml) se dispara con `push` a `main`, `develop` o cualquier rama `feature/**`, y con pull requests hacia `main` o `develop`.

Etapas:

1. Checkout del código.
2. Configuración de JDK 17 (Temurin) con caché de Maven.
3. `mvn clean verify`: compilación, 273 pruebas, reporte JaCoCo y quality gate.
4. Publicación de los artefactos de cobertura y de pruebas.
5. Resumen con el número de pruebas y el porcentaje de cobertura en el run.

Run de referencia: <https://github.com/juandavidbotero242008/FIS_2610_Parcial/actions/runs/45287569149>

## Documentación

- [Informe final](docs/informe.md) · [versión PDF](docs/informe.pdf)
- [Matriz de trazabilidad de pruebas](docs/matriz-trazabilidad.md)
- [Backlog Scrum](docs/backlog-scrum.md)
- [Guía de GitHub y evidencias](docs/guia-github.md)

## Autores

- Juan David Botero

**Curso:** Fundamentos de Ingeniería de Software — Segundo Parcial, 2026.

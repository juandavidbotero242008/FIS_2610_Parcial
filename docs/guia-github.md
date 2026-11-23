# Guía de GitHub, Git y evidencias

Esta guía deja el repositorio `FIS_2610_Parcial` listo para publicar y describe cómo recolectar las evidencias que exige el informe.

## 1. Requisitos

- Git 2.30 o superior.
- Cuenta de GitHub.
- Opcional: [GitHub CLI](https://cli.github.com/) (`gh`) para crear issues y repositorio desde la terminal.
- JDK 17 y Maven 3.9 para ejecutar `mvn clean verify`.

Configurar identidad (solo la primera vez):

```bash
git config --global user.name "Nombre Apellido"
git config --global user.email "correo@javeriana.edu.co"
```

## 2. Crear el repositorio

### Opción A — desde GitHub CLI

```bash
cd FIS_2610_Parcial
git init
git add .
git commit -m "chore: estructura inicial del proyecto electoral"
gh repo create FIS_2610_Parcial --public --source=. --remote=origin --push
```

### Opción B — desde la web

1. Crear el repositorio `FIS_2610_Parcial` en GitHub **sin** README ni `.gitignore`.
2. Enlazarlo y subir:

```bash
cd FIS_2610_Parcial
git init
git add .
git commit -m "chore: estructura inicial del proyecto electoral"
git branch -M main
git remote add origin https://github.com/juandavidbotero242008/FIS_2610_Parcial.git
git push -u origin main
```


## 3. Estrategia de ramas

| Rama | Propósito | Dispara CI |
|---|---|---|
| `main` | Versión estable / entrega | Sí |
| `develop` | Integración del equipo | Sí |
| `feature/parcial-2` | **Rama del equipo** para el parcial | Sí (`feature/**`) |

Crear y usar la rama del equipo:

```bash
git checkout -b feature/parcial-2
git push -u origin feature/parcial-2
```

Si el equipo usa otro nombre (por ejemplo `equipo-3`), ajuste el filtro del workflow en `.github/workflows/ci.yml`.

## 4. Conventional Commits

Formato: `tipo(alcance): descripción en imperativo`.

| Tipo | Uso |
|---|---|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de defecto |
| `test` | Pruebas nuevas o ajustes |
| `refactor` | Cambio interno sin alterar comportamiento |
| `docs` | Documentación |
| `ci` | Cambios en pipeline |
| `chore` | Tareas de mantenimiento |
| `build` | Cambios de build o dependencias |

Secuencia sugerida para reflejar la evolución del proyecto:

```bash
git commit -m "chore: estructura inicial de Maven y JUnit 5"
git commit -m "feat(dominio): agregar jerarquia territorial con Composite"
git commit -m "feat(dominio): implementar proceso electoral con patron State"
git commit -m "feat(dominio): registrar partidos, candidatos y jurados"
git commit -m "feat(actas): implementar ActaBuilder con validaciones"
git commit -m "feat(validacion): agregar Factory Method por tipo de eleccion"
git commit -m "feat(eventos): implementar Observer para auditoria y partidos"
git commit -m "feat(fachada): agregar ServicioElectoralFacade"
git commit -m "test(dominio): cubrir metodos con casos negativos y de limite"
git commit -m "ci: agregar pipeline de GitHub Actions con JaCoCo"
git commit -m "docs: redactar informe y matriz de trazabilidad"
```

Si un commit cierra una historia, agregue el trailer:

```bash
git commit -m "feat(actas): implementar ActaBuilder con validaciones

Closes #5"
```

Verificar el historial:

```bash
git log --oneline --graph --decorate
```

## 5. Issues y Scrum

### Automático

```bash
gh auth login
bash scripts/crear-issues.sh juandavidbotero242008/FIS_2610_Parcial
```

### Manual

1. Ir a **Issues → New issue → Historia de usuario**.
2. Usar los títulos `[US-01] ...` a `[US-12] ...` del archivo [`backlog-scrum.md`](backlog-scrum.md).
3. Asignar etiquetas `historia`, `scrum`, `sprint-N` y la categoría del patrón.
4. Crear un tablero en **Projects** con columnas: Backlog, En curso, En revisión, Hecho.

## 6. Pull requests

```bash
git checkout -b feature/us-05-acta-builder
# implementar
git add .
git commit -m "feat(actas): implementar ActaBuilder con validaciones"
git push -u origin feature/us-05-acta-builder
gh pr create --base feature/parcial-2 --fill
```

El pipeline corre automáticamente. No se hace merge si el quality gate falla.

## 7. Evidencias para el informe

Guardar en `docs/evidencias/` con nombres descriptivos:

| Evidencia | Cómo obtenerla | Nombre sugerido |
|---|---|---|
| Ejecución local de pruebas | `mvn clean verify` y captura de consola | `01-mvn-verify.png` |
| Reporte JaCoCo | Abrir `target/site/jacoco/index.html` | `02-jacoco.png` |
| Run exitoso de Actions | GitHub → Actions → último run | `03-actions-exito.png` |
| Detalle de etapas del pipeline | Pestaña del job `calidad` | `04-actions-etapas.png` |
| Resultados JUnit publicados | Sección *Checks* del run | `05-junit-actions.png` |
| Issues del backlog | GitHub → Issues filtrados | `06-issues.png` |
| Tablero Scrum | GitHub → Projects | `07-projects.png` |
| Historial de commits | `git log --oneline --graph` | `08-commits.png` |
| Cobertura por clase | `target/site/jacoco/index.html` | `09-cobertura-clases.png` |

Comandos útiles para generar texto de evidencia:

```bash
# Resumen de pruebas
mvn -B test | tee docs/evidencias/salida-pruebas.txt

# Historial de commits
git log --oneline --graph --decorate > docs/evidencias/historial-commits.txt

# Conteo de pruebas
grep -h "<testsuite" target/surefire-reports/*.xml \
  | sed -E 's/.*tests="([0-9]+)".*/\1/' \
  | awk '{s+=$1} END {print "Total pruebas:", s}'
```

## 8. Exportar el informe a PDF

El repositorio incluye un script que convierte `docs/informe.md` en `docs/informe.pdf`, renderiza los diagramas Mermaid y aplica el formato académico:

```bash
pip install markdown weasyprint pygments
python3 scripts/exportar_pdf.py
```

El resultado queda en `docs/informe.pdf`.

Alternativa con Visual Studio Code:

1. Instalar la extensión **Markdown PDF**.
2. Abrir `docs/informe.md`.
3. `Ctrl+Shift+P` → *Markdown PDF: Export (pdf)*.

Alternativa con Pandoc (si está instalado):

```bash
pandoc docs/informe.md -o docs/informe.pdf --pdf-engine=weasyprint --toc
```

> Los diagramas Mermaid se renderizan en GitHub y en el PDF generado por el script. En VS Code pueden requerir una extensión adicional.

## 9. Checklist final de entrega

- [ ] Repositorio público `FIS_2610_Parcial`.
- [ ] Rama del equipo con el código y el pipeline.
- [ ] Workflow en verde (evidencia del run).
- [ ] 273 pruebas en verde y cobertura ≥ 70 %.
- [ ] 12 issues creados y cerrados con sus commits.
- [ ] Historial con conventional commits.
- [ ] Informe PDF con justificaciones y capturas.
- [ ] Badge del README apuntando al repositorio real.

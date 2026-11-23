# Guía de GitHub, Git y evidencias

Esta guía documenta el repositorio `FIS_2610_Parcial`, ya publicado, y describe cómo reproducir las evidencias del informe.

## 1. Requisitos

- Git 2.30 o superior.
- Cuenta de GitHub.
- Opcional: [GitHub CLI](https://cli.github.com/) (`gh`) para gestionar issues desde la terminal.
- JDK 17 y Maven 3.9 para ejecutar `mvn clean verify`.

Configurar identidad (solo la primera vez):

```bash
git config --global user.name "Nombre Apellido"
git config --global user.email "correo@javeriana.edu.co"
```

## 2. Repositorio publicado

Repositorio: <https://github.com/juandavidbotero242008/FIS_2610_Parcial>

El proyecto ya tiene `main` y `feature/parcial-2` publicadas, 12 issues cerrados y el pipeline en verde. Para clonarlo en otra máquina:

```bash
git clone https://github.com/juandavidbotero242008/FIS_2610_Parcial.git
cd FIS_2610_Parcial
git checkout feature/parcial-2
```

Para publicar cambios nuevos:

```bash
git add .
git commit -m "tipo(alcance): descripcion"
git push origin feature/parcial-2
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

Los 12 issues ya están creados, etiquetados y cerrados: <https://github.com/juandavidbotero242008/FIS_2610_Parcial/issues?q=is%3Aissue>

### Recrearlos en otro repositorio

```bash
gh auth login
bash scripts/crear-issues.sh juandavidbotero242008/FIS_2610_Parcial
```

### Manual

1. Ir a **Issues → New issue → Historia de usuario**.
2. Usar los títulos `[US-01] ...` a `[US-12] ...` del archivo [`backlog-scrum.md`](backlog-scrum.md).
3. Asignar etiquetas `historia`, `scrum`, `sprint-N` y la categoría del patrón.
4. Al cerrar cada issue, comentar el commit que lo implementa.

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

Las evidencias ya están en `docs/evidencias/`:

| Evidencia | Archivo |
|---|---|
| Ejecución local de pruebas | `01-mvn-verify.png` y `salida-pruebas.txt` |
| Reporte JaCoCo | `02-jacoco.png` y `cobertura.txt` |
| Run exitoso de Actions | `03-actions-exito.png` y `03-actions-exito.txt` |
| Detalle de etapas del pipeline | `04-actions-etapas.png` y `04-actions-etapas.txt` |
| Resultados de pruebas del run | `05-junit-actions.png` y `05-junit-actions.txt` |
| Issues del backlog | `06-issues.png` y `06-issues.txt` |
| Historial de commits | `08-commits.png` y `historial-commits.txt` |
| Cobertura por clase | `09-cobertura-clases.png` |

> No se creó un tablero en GitHub Projects; el seguimiento se evidencia con las etiquetas de sprint y el estado cerrado de los issues.

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

- [x] Repositorio público `FIS_2610_Parcial`.
- [x] Rama del equipo con el código y el pipeline.
- [x] Workflow en verde (evidencia del run).
- [x] 273 pruebas en verde y cobertura ≥ 70 %.
- [x] 12 issues creados y cerrados con sus commits.
- [x] Historial con conventional commits.
- [x] Informe PDF con justificaciones y capturas.
- [x] Badge del README apuntando al repositorio real.
- [ ] Completar el nombre del docente en la portada.

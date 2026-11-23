# Evidencias

## Generadas localmente (ya incluidas)

| Archivo | Contenido |
|---|---|
| `salida-pruebas.txt` | Resumen de `mvn -B clean verify`: 265 pruebas, quality gate y conteo por clase |
| `cobertura.txt` | Contadores de JaCoCo (instrucciones, ramas, líneas, métodos y clases) |
| `diagramas/diagrama-1.png` | Diagrama de clases renderizado desde Mermaid |
| `diagramas/diagrama-2.png` | Flujo del pipeline renderizado desde Mermaid |

## Capturas pendientes (requieren publicar el repositorio)

Guardar con estos nombres para que el informe las muestre:

- `01-mvn-verify.png` — consola con `BUILD SUCCESS` y el quality gate.
- `02-jacoco.png` — portada del reporte en `target/site/jacoco/index.html`.
- `03-actions-exito.png` — run en verde en GitHub Actions.
- `04-actions-etapas.png` — detalle de las etapas del job.
- `05-junit-actions.png` — resultados JUnit publicados en el run.
- `06-issues.png` — listado de issues del backlog.
- `07-projects.png` — tablero Scrum.
- `08-commits.png` — `git log --oneline --graph`.
- `09-cobertura-clases.png` — cobertura por clase en JaCoCo.

> Mientras no existan, el PDF del informe solo muestra el texto alternativo de cada figura; al agregarlas, vuelva a ejecutar `python3 scripts/exportar_pdf.py`.

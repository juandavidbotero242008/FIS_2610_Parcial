#!/usr/bin/env python3
"""Exporta docs/informe.md a docs/informe.pdf.

Requisitos:
    pip install markdown weasyprint pygments

El script:
1. Renderiza los bloques Mermaid con mermaid.ink y los incrusta como PNG.
2. Convierte el Markdown a HTML con tablas y resaltado de codigo.
3. Aplica una hoja de estilos academica y genera el PDF con WeasyPrint.

Uso:
    python3 scripts/exportar_pdf.py
"""

from __future__ import annotations

import base64
import re
import sys
import urllib.request
from pathlib import Path

import markdown
from weasyprint import HTML

RAIZ = Path(__file__).resolve().parent.parent
DOCS = RAIZ / "docs"
ORIGEN = DOCS / "informe.md"
DESTINO = DOCS / "informe.pdf"
DIR_DIAGRAMAS = DOCS / "evidencias" / "diagramas"

PATRON_MERMAID = re.compile(r"```mermaid\n(.*?)```", re.DOTALL)


def renderizar_mermaid(codigo: str, indice: int) -> str:
    """Descarga el PNG del diagrama y retorna la ruta relativa a docs/."""
    DIR_DIAGRAMAS.mkdir(parents=True, exist_ok=True)
    destino = DIR_DIAGRAMAS / f"diagrama-{indice}.png"
    if destino.exists():
        return f"evidencias/diagramas/{destino.name}"
    codificado = base64.urlsafe_b64encode(codigo.encode("utf-8")).decode("ascii")
    url = f"https://mermaid.ink/img/{codificado}?type=png&bgColor=FFFFFF"
    try:
        solicitud = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(solicitud, timeout=60) as respuesta:
            destino.write_bytes(respuesta.read())
    except Exception as error:  # pragma: no cover - depende de la red
        print(f"Advertencia: no se pudo renderizar el diagrama {indice}: {error}", file=sys.stderr)
        return ""
    return f"evidencias/diagramas/{destino.name}"


def preparar_markdown(texto: str) -> str:
    contador = 0

    def reemplazo(coincidencia: re.Match[str]) -> str:
        nonlocal contador
        contador += 1
        codigo = coincidencia.group(1)
        ruta = renderizar_mermaid(codigo, contador)
        if not ruta:
            return coincidencia.group(0)
        return f"![Diagrama {contador}]({ruta})"

    return PATRON_MERMAID.sub(reemplazo, texto)


CSS = """
@page {
    size: A4;
    margin: 2.2cm 1.9cm 2cm 1.9cm;
    @bottom-center {
        content: counter(page);
        font-size: 8pt;
        color: #666;
    }
    @top-right {
        content: "Fundamentos de Ingeniería de Software — Parcial 2";
        font-size: 7.5pt;
        color: #888;
    }
}
@page :first {
    @top-right { content: ""; }
    @bottom-center { content: ""; }
}
body {
    font-family: "DejaVu Sans", sans-serif;
    font-size: 10pt;
    line-height: 1.45;
    color: #1a1a1a;
    text-align: justify;
}
h1, h2, h3, h4 {
    font-family: "DejaVu Sans", sans-serif;
    color: #0b2e59;
    line-height: 1.2;
    page-break-after: avoid;
}
h1 { font-size: 20pt; margin-top: 0; border-bottom: 2px solid #0b2e59; padding-bottom: 6px; }
h2 { font-size: 14pt; margin-top: 20px; border-bottom: 1px solid #c9d6e8; padding-bottom: 3px; }
h3 { font-size: 11.5pt; margin-top: 16px; }
h4 { font-size: 10.5pt; }
p { margin: 6px 0; }
ul, ol { margin: 6px 0 6px 18px; }
li { margin: 2px 0; }
a { color: #0b4f9e; text-decoration: none; }
table {
    width: 100%;
    border-collapse: collapse;
    margin: 10px 0;
    font-size: 8.6pt;
    page-break-inside: avoid;
}
th {
    background: #e8eef7;
    border: 1px solid #b9c8dc;
    padding: 4px 5px;
    text-align: left;
}
td {
    border: 1px solid #cfd8e3;
    padding: 3px 5px;
    vertical-align: top;
}
tr:nth-child(even) td { background: #fafbfd; }
pre {
    background: #f4f5f7;
    border-left: 3px solid #0b4f9e;
    padding: 7px 9px;
    font-size: 7.8pt;
    line-height: 1.35;
    white-space: pre-wrap;
    word-wrap: break-word;
    page-break-inside: avoid;
}
code {
    font-family: "DejaVu Sans Mono", monospace;
    font-size: 8.2pt;
}
p code, li code, td code {
    background: #eef1f5;
    padding: 0 2px;
    border-radius: 2px;
}
blockquote {
    margin: 8px 0;
    padding: 6px 10px;
    background: #fff8e6;
    border-left: 3px solid #d9a300;
    color: #4a3b00;
    font-size: 9pt;
}
img {
    max-width: 100%;
    max-height: 20.5cm;
    width: auto;
    height: auto;
    display: block;
    margin: 8px auto;
}
hr { border: none; border-top: 1px solid #ccc; margin: 14px 0; }
pre, pre code, .codehilite code {
    white-space: pre-wrap !important;
    overflow-wrap: break-word;
    word-break: break-word;
    overflow: hidden;
}
.portada {
    text-align: center;
    padding-top: 4.5cm;
    page-break-after: always;
}
.portada .institucion { font-size: 12pt; letter-spacing: 0.5px; color: #0b2e59; }
.portada .titulo { font-size: 24pt; font-weight: bold; color: #0b2e59; margin: 1.2cm 0 0.6cm; line-height: 1.25; }
.portada .subtitulo { font-size: 13pt; color: #333; margin-bottom: 2cm; }
.portada .datos { font-size: 10.5pt; line-height: 1.8; color: #222; }
.portada .pie { margin-top: 2.5cm; font-size: 9pt; color: #666; }
"""


def main() -> None:
    if not ORIGEN.exists():
        raise SystemExit(f"No existe {ORIGEN}")

    texto = preparar_markdown(ORIGEN.read_text(encoding="utf-8"))

    html_cuerpo = markdown.markdown(
        texto,
        extensions=["tables", "fenced_code", "toc", "attr_list", "sane_lists", "codehilite"],
        extension_configs={"codehilite": {"noclasses": True, "guess_lang": False}},
    )

    portada = """
    <div class="portada">
      <div class="institucion">PONTIFICIA UNIVERSIDAD JAVERIANA<br>DEPARTAMENTO DE INGENIERÍA DE SISTEMAS</div>
      <div class="titulo">Sistema de Información para la Gestión, Transparencia y Análisis del Proceso Electoral en Colombia</div>
      <div class="subtitulo">Segundo Parcial — Fundamentos de Ingeniería de Software</div>
      <div class="datos">
        <strong>Integrantes:</strong> Juan David Botero Avila · Esteban Cantillo Valverde<br>
        Santiago Baez Parra · Sergio Pulido Grimaldo<br>
        <strong>Docente:</strong> Fabrizio Bolaño Lopez<br>
        <strong>Repositorio:</strong> github.com/juandavidbotero242008/FIS_2610_Parcial<br>
        <strong>Fecha:</strong> 23/11/2026
      </div>
      <div class="pie">Java 17 · Maven · JUnit 5 · JaCoCo · GitHub Actions</div>
    </div>
    """

    html = f"""<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<title>Informe Parcial 2 FIS</title>
<style>{CSS}</style>
</head>
<body>
{portada}
{html_cuerpo}
</body></html>"""

    salida_temporal = DOCS / "informe.html"
    salida_temporal.write_text(html, encoding="utf-8")
    HTML(string=html, base_url=str(DOCS)).write_pdf(DESTINO)
    print(f"PDF generado: {DESTINO}")
    print(f"HTML intermedio: {salida_temporal}")


if __name__ == "__main__":
    main()

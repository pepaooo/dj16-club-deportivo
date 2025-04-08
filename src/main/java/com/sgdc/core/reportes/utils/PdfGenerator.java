package com.sgdc.core.reportes.utils;

import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.sgdc.core.miembro.domain.Miembro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfGenerator {

    private static final Logger log = LoggerFactory.getLogger(PdfGenerator.class);

    /**
     * Genera un reporte en PDF para la lista de miembros.
     *
     * @param miembros Lista de miembros a incluir en el reporte.
     * @return Un arreglo de bytes con el contenido del PDF.
     */
    public static byte[] generateMiembrosReport(List<Miembro> miembros) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // Inicializamos el PdfWriter, PdfDocument y Document.
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título del reporte
            Paragraph title = new Paragraph("Reporte de Miembros")
                    .setFontSize(18)
                    //.setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            document.add(new Paragraph("\n"));

            // Definimos los anchos de columnas
            //float[] columnWidths = {50, 150, 100, 100, 80};
            float[] columnWidths = {50, 150, 100, 100};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Agregamos la fila de encabezado
            addTableHeaderCell(table, "ID Miembro");
            addTableHeaderCell(table, "Nombre Completo");
            addTableHeaderCell(table, "Tipo de Membresía");
            addTableHeaderCell(table, "Fecha Inscripción");
            //addTableHeaderCell(table, "Estatus");

            // Usar DateTimeFormatter para formatear LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // Agregamos una fila por cada miembro.
            for (Miembro miembro : miembros) {
                // ID del Miembro
                table.addCell(new Cell().add(new Paragraph(String.valueOf(miembro.getId()))));

                // Nombre Completo
                String nombreCompleto = miembro.getNombre() + " " + miembro.getApellidoPaterno() + " " + miembro.getApellidoMaterno();
                table.addCell(new Cell().add(new Paragraph(nombreCompleto)));

                // Tipo de Membresía (se asume que miembro.getMembresia() no es nulo)
                String tipoMembresia = (miembro.getMembresia() != null) ? miembro.getMembresia().getNombre() : "";
                table.addCell(new Cell().add(new Paragraph(tipoMembresia)));

                // Fecha de Inscripción
                String fechaInscripcion = (miembro.getFechaInscripcion() != null) ?
                        miembro.getFechaInscripcion().format(formatter) : "";
                table.addCell(new Cell().add(new Paragraph(fechaInscripcion)));

                // Estatus
//                String estatus = (miembro.getEstatus() != null) ? miembro.getEstatus() : "";
//                table.addCell(new Cell().add(new Paragraph(estatus)));
                //table.addCell(new Cell().add(new Paragraph("Activo")));
            }

            // Agregamos la tabla al documento y cerramos el documento
            document.add(table);
            document.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return baos.toByteArray();
    }

    /**
     * Metodo auxiliar para agregar celdas de encabezado a la tabla con estilos predefinidos.
     *
     * @param table   La tabla a la que se le añadirá la celda.
     * @param content El texto de la celda de encabezado.
     * @throws Exception Si ocurre algún error al agregar la celda.
     */
    private static void addTableHeaderCell(Table table, String content) throws Exception {
        Cell headerCell = new Cell().add(new Paragraph(content));
        //headerCell.setBold();
        headerCell.setBackgroundColor(new DeviceGray(0.75f));
        headerCell.setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(headerCell);
    }
}

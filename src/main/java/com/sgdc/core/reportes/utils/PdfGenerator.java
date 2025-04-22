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
import com.sgdc.core.reservas.domain.Reserva;
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
        // 1. Definir encabezados y anchos
        List<String> headers = List.of(
                "ID Miembro", "Nombre Completo", "Correo Electrónico",
                "Fecha Inscripción"
        );
        float[] widths = {50f, 150f, 100f, 100f};

        // 2. Convertir cada Miembro a List<String>
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<List<String>> rows = miembros.stream().map(m -> List.of(
                String.valueOf(m.getId()),
                m.getNombre() + " " + m.getApellidoPaterno() + " " + m.getApellidoMaterno(),
                m.getCorreoElectronico() != null ? m.getCorreoElectronico() : "",
                m.getFechaInscripcion() != null
                        ? m.getFechaInscripcion().format(formatter) : ""
        )).toList();

        // 3. Llamar al metodo genérico
        return generateReport("Reporte de Miembros", headers, widths, rows);
    }

    /**
     * Genera un reporte en PDF para la lista de reservas.
     *
     * @param reservas Lista de reservas a incluir en el reporte.
     * @return Un arreglo de bytes con el contenido del PDF.
     */
    public static byte[] generateReservasReport(List<Reserva> reservas) {
        // 1. Definir encabezados y anchos
        List<String> headers = List.of(
                "ID Reserva", "Instalación", "Miembro",
                "Inicio", "Fin", "Estado"
        );
        float[] widths = {50f, 100f, 150f, 100f, 100f, 80f};

        // 2. Convertir cada Reserva a List<String>
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<List<String>> rows = reservas.stream().map(r -> List.of(
                String.valueOf(r.getId()),
                r.getInstalacion() != null ? r.getInstalacion().getNombre() : "",
                r.getMiembro() != null
                        ? (r.getMiembro().getNombre() + " " + r.getMiembro().getApellidoPaterno())
                        : "",
                r.getFechaHoraInicio() != null
                        ? r.getFechaHoraInicio().format(formatter) : "",
                r.getFechaHoraFin() != null
                        ? r.getFechaHoraFin().format(formatter) : "",
                r.getEstadoReserva() != null ? r.getEstadoReserva() : ""
        )).toList();

        // 3. Llamar al metodo genérico
        return generateReport("Reporte de Reservas", headers, widths, rows);
    }

    /**
     * Genera un PDF con tabla genérica.
     *
     * @param title        Título del reporte.
     * @param headers      Lista de encabezados (ej. ["ID", "Nombre", "Fecha"]).
     * @param columnWidths Anchos relativos de columnas (ej. {50f, 150f, 100f}).
     * @param rows         Filas de datos, cada fila es una lista de Strings.
     * @return Bytes del PDF generado.
     */
    public static byte[] generateReport(
            String title,
            List<String> headers,
            float[] columnWidths,
            List<List<String>> rows) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            document.add(new Paragraph(title)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            // Tabla
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            for (String h : headers) {
                Cell headerCell = new Cell().add(new Paragraph(h));
                headerCell.setBackgroundColor(new DeviceGray(0.75f));
                headerCell.setTextAlignment(TextAlignment.CENTER);
                table.addHeaderCell(headerCell);
            }

            // Filas
            for (List<String> row : rows) {
                for (String cellValue : row) {
                    table.addCell(new Cell().add(new Paragraph(cellValue)));
                }
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            log.error("Error generando reporte genérico", e);
        }
        return baos.toByteArray();
    }

}

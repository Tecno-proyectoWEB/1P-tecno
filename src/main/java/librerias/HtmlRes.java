package librerias;

import java.util.List;

/**
 * Clase para generar HTML con estilos modernos para emails
 */
public class HtmlRes {
    
    // Estilos CSS modernos
    private static final String CSS_STYLES = 
        "<style>" +
        "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; }" +
        ".container { width: 100%; max-width: none; margin: 0; background: white; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }" +
        ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px 20px; text-align: center; }" +
        ".header h1 { margin: 0; font-size: 32px; font-weight: 300; }" +
        ".header h2 { margin: 15px 0 0 0; font-size: 28px; font-weight: 300; opacity: 0.9; }" +
        ".content { padding: 40px 20px; }" +
        ".success-message { background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); color: white; padding: 30px; border-radius: 15px; margin: 30px 0; text-align: center; }" +
        ".error-message { background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%); color: white; padding: 30px; border-radius: 15px; margin: 30px 0; text-align: center; }" +
        ".info-message { background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%); color: white; padding: 30px; border-radius: 15px; margin: 30px 0; text-align: center; }" +
        ".table-container { margin: 30px 0; overflow-x: auto; }" +
        ".modern-table { width: 100%; border-collapse: collapse; border-radius: 15px; overflow: hidden; box-shadow: 0 8px 25px rgba(0,0,0,0.15); }" +
        ".modern-table th { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px 15px; text-align: left; font-weight: 600; font-size: 16px; }" +
        ".modern-table td { padding: 18px 15px; border-bottom: 1px solid #eee; font-size: 15px; }" +
        ".modern-table tr:nth-child(even) { background-color: #f8f9fa; }" +
        ".modern-table tr:hover { background-color: #e3f2fd; transition: background-color 0.3s ease; }" +
        ".command-example { background: #f5f5f5; border-left: 6px solid #667eea; padding: 20px; margin: 20px 0; border-radius: 8px; font-family: 'Courier New', monospace; font-size: 14px; }" +
        ".section-title { color: #333; font-size: 24px; font-weight: 600; margin: 35px 0 20px 0; padding-bottom: 15px; border-bottom: 3px solid #667eea; }" +
        ".command-list { list-style: none; padding: 0; }" +
        ".command-list li { background: #f8f9fa; margin: 12px 0; padding: 18px; border-radius: 10px; border-left: 5px solid #667eea; }" +
        ".command-list li strong { color: #667eea; }" +
        ".footer { background: #f8f9fa; padding: 30px 20px; text-align: center; color: #666; font-size: 14px; border-top: 2px solid #eee; }" +
        ".badge { display: inline-block; padding: 6px 12px; border-radius: 15px; font-size: 12px; font-weight: 600; text-transform: uppercase; margin: 0 8px; }" +
        ".badge-success { background: #4CAF50; color: white; }" +
        ".badge-error { background: #f44336; color: white; }" +
        ".badge-info { background: #2196F3; color: white; }" +
        ".badge-warning { background: #ff9800; color: white; }" +
        "@media (max-width: 768px) { " +
        "  .header { padding: 30px 15px; }" +
        "  .header h1 { font-size: 28px; }" +
        "  .header h2 { font-size: 24px; }" +
        "  .content { padding: 30px 15px; }" +
        "  .modern-table th, .modern-table td { padding: 15px 10px; font-size: 14px; }" +
        "  .success-message, .error-message, .info-message { padding: 25px 20px; margin: 25px 0; }" +
        "  .command-example { padding: 15px; font-size: 13px; }" +
        "  .footer { padding: 25px 15px; }" +
        "}" +
        "@media (max-width: 480px) { " +
        "  .header { padding: 25px 10px; }" +
        "  .header h1 { font-size: 24px; }" +
        "  .header h2 { font-size: 20px; }" +
        "  .content { padding: 25px 10px; }" +
        "  .modern-table th, .modern-table td { padding: 12px 8px; font-size: 13px; }" +
        "  .success-message, .error-message, .info-message { padding: 20px 15px; margin: 20px 0; }" +
        "  .command-example { padding: 12px; font-size: 12px; }" +
        "  .footer { padding: 20px 10px; font-size: 12px; }" +
        "}" +
        "</style>";

    public static String generateTable(String title, String[] headers, List<String[]> data) {
        StringBuilder tableHtml = new StringBuilder();
        
        // Header de la tabla
        tableHtml.append("<div class='table-container'>");
        tableHtml.append("<table class='modern-table'>");
        tableHtml.append("<thead><tr>");
        
        for (String header : headers) {
            tableHtml.append("<th>").append(header).append("</th>");
        }
        tableHtml.append("</tr></thead>");
        
        // Cuerpo de la tabla
        tableHtml.append("<tbody>");
        for (String[] row : data) {
            tableHtml.append("<tr>");
            for (String cell : row) {
                tableHtml.append("<td>").append(cell != null ? cell : "").append("</td>");
            }
            tableHtml.append("</tr>");
        }
        tableHtml.append("</tbody>");
        tableHtml.append("</table>");
        tableHtml.append("</div>");

        return generateFullHtml(title, tableHtml.toString());
    }

    public static String generateText(String[] args) {
        if (args.length == 0) return generateFullHtml("Mensaje", "");
        
        String title = args[0];
        StringBuilder content = new StringBuilder();
        
        // Determinar el tipo de mensaje y aplicar estilos
        String messageClass = "info-message";
        if (title.toLowerCase().contains("error") || title.toLowerCase().contains("error")) {
            messageClass = "error-message";
        } else if (title.toLowerCase().contains("éxito") || title.toLowerCase().contains("exitosamente") || 
                   title.toLowerCase().contains("correctamente") || title.toLowerCase().contains("guardado")) {
            messageClass = "success-message";
        }
        
        content.append("<div class='").append(messageClass).append("'>");
        content.append("<h2>").append(title).append("</h2>");
        
        for (int i = 1; i < args.length; i++) {
            content.append("<p>").append(args[i]).append("</p>");
        }
        content.append("</div>");

        return generateFullHtml(title, content.toString());
    }
       
    public static String generateTableForSimpleData(String title, String[] headers, String[] data) {
        StringBuilder tableHtml = new StringBuilder();
        
        tableHtml.append("<div class='table-container'>");
        tableHtml.append("<table class='modern-table'>");
        tableHtml.append("<thead><tr>");
        tableHtml.append("<th>Campo</th><th>Valor</th>");
        tableHtml.append("</tr></thead>");
        tableHtml.append("<tbody>");
        
        for (int i = 0; i < headers.length && i < data.length; i++) {
            tableHtml.append("<tr>");
            tableHtml.append("<td><strong>").append(headers[i]).append("</strong></td>");
            tableHtml.append("<td>").append(data[i] != null ? data[i] : "").append("</td>");
            tableHtml.append("</tr>");
        }
        
        tableHtml.append("</tbody>");
        tableHtml.append("</table>");
        tableHtml.append("</div>");

        return generateFullHtml(title, tableHtml.toString());
    }
    
    public static String generateSuccessMessage(String title, String message) {
        StringBuilder content = new StringBuilder();
        content.append("<div class='success-message'>");
        content.append("<h2>✅ ").append(title).append("</h2>");
        content.append("<p>").append(message).append("</p>");
        content.append("</div>");
        
        return generateFullHtml(title, content.toString());
    }
    
    public static String generateErrorMessage(String title, String message) {
        StringBuilder content = new StringBuilder();
        content.append("<div class='error-message'>");
        content.append("<h2>❌ ").append(title).append("</h2>");
        content.append("<p>").append(message).append("</p>");
        content.append("</div>");
        
        return generateFullHtml(title, content.toString());
    }
    
    public static String generateHelpMessage(String title, String[] sections) {
        StringBuilder content = new StringBuilder();
        
        content.append("<div class='info-message'>");
        content.append("<h2>📋 ").append(title).append("</h2>");
        content.append("<p>Comandos disponibles en el sistema:</p>");
        content.append("</div>");
        
        for (String section : sections) {
            content.append("<div class='command-example'>");
            content.append(section);
            content.append("</div>");
        }
        
        return generateFullHtml(title, content.toString());
    }
    
    private static String generateFullHtml(String title, String content) {
        return "<!DOCTYPE html>" +
               "<html lang='es'>" +
               "<head>" +
               "<meta charset='UTF-8'>" +
               "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
               "<title>" + title + "</title>" +
               CSS_STYLES +
               "</head>" +
               "<body>" +
               "<div class='container'>" +
               "<div class='header'>" +
               "<h1>🏢 Sistema de Gestión</h1>" +
               "<h2>" + title + "</h2>" +
               "</div>" +
               "<div class='content'>" +
               content +
               "</div>" +
               "<div class='footer'>" +
               "<p>📧 Sistema de Comunicación por Email | TecnoWeb 2025</p>" +
               "<p>🕒 " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "</p>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
}

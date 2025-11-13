/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package postgresConecction;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import librerias.Email;

/**
 *
 * @author nnn
 */
public class EmailSend implements Runnable {

    private final static String PORT_SMTP = "25";
    private final static String PROTOCOL = "smtp";
    private final static String HOST = "mail.tecnoweb.org.bo";
    private final static String USER = "grupo11sc";
    private final static String PASSWORD = "grup011grup011*";
    private final static String MAIL = "grupo11sc@tecnoweb.org.bo";
    private final static String MAIL_PASSWORD = "grup011grup011*";

    private Email email;

    public EmailSend(Email emailP) {
        this.email = emailP;
        //this.email.setFrom(MAIL);
    }

    @Override
    public void run() {
        Properties properties = new Properties();
        //properties.put("mail.transport.protocol", PROTOCOL);
        properties.setProperty("mail.smtp.host", HOST);
        properties.setProperty("mail.smtp.port", PORT_SMTP);
        
        // CONFIGURACIÓN BÁSICA SIN SEGURIDAD (para servidor lento/problemático)
        properties.setProperty("mail.smtp.auth", "false"); // Sin autenticación
        properties.setProperty("mail.smtp.starttls.enable", "false"); // Sin STARTTLS
        properties.setProperty("mail.smtp.starttls.required", "false"); // Sin TLS requerido
        properties.setProperty("mail.smtp.ssl.enable", "false"); // Sin SSL
        properties.setProperty("mail.smtp.tls.enable", "false"); // Sin TLS
        
        // HABILITAR DEBUG PARA VER RESPUESTAS DEL SERVIDOR
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.smtp.debug", "true");
        properties.setProperty("mail.transport.protocol.rset", "true");
        
        // TIMEOUTS MÁXIMOS PARA SERVIDOR MUY LENTO
        properties.setProperty("mail.smtp.connectiontimeout", "60000"); // 60 segundos
        properties.setProperty("mail.smtp.timeout", "60000"); // 60 segundos  
        properties.setProperty("mail.smtp.writetimeout", "60000"); // 60 segundos
        
        // CONFIGURACIONES ADICIONALES PARA COMPATIBILIDAD
        properties.setProperty("mail.smtp.quitwait", "false");
        properties.setProperty("mail.smtp.socketFactory.fallback", "true");
        properties.setProperty("mail.smtp.ehlo", "false"); // Usar HELO en lugar de EHLO
        properties.setProperty("mail.smtp.localhost", "localhost"); // Identificación local
        
        Session session = Session.getDefaultInstance(properties, null); // Sin autenticador
        
        // HABILITAR DEBUG EN LA SESIÓN
        session.setDebug(true);
        
        // Log de la configuración completa
        Logger.getLogger(EmailSend.class.getName()).info("=== CONFIGURACIÓN SMTP BÁSICA (SIN SEGURIDAD) ===");
        Logger.getLogger(EmailSend.class.getName()).info("Host: " + HOST);
        Logger.getLogger(EmailSend.class.getName()).info("Puerto: " + PORT_SMTP + " (BÁSICO)");
        Logger.getLogger(EmailSend.class.getName()).info("Usuario: SIN AUTENTICACIÓN");
        Logger.getLogger(EmailSend.class.getName()).info("Email remitente: " + MAIL);
        Logger.getLogger(EmailSend.class.getName()).info("Destinatario: " + email.getTo());
        Logger.getLogger(EmailSend.class.getName()).info("Asunto: " + email.getSubject());
        Logger.getLogger(EmailSend.class.getName()).info("Autenticación: FALSE");
        Logger.getLogger(EmailSend.class.getName()).info("TLS/SSL: DESHABILITADO");
        Logger.getLogger(EmailSend.class.getName()).info("Timeout: 60 segundos");
        Logger.getLogger(EmailSend.class.getName()).info("EHLO: DESHABILITADO (usar HELO)");
        Logger.getLogger(EmailSend.class.getName()).info("Debug habilitado: true");
        
        try {
            MimeMessage message;
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MAIL));
            InternetAddress[] toAddresses = { new InternetAddress(email.getTo())};

            message.setRecipients(MimeMessage.RecipientType.TO, toAddresses);
            message.setSubject(email.getSubject());

            Multipart multipart = new MimeMultipart("alternative");
            MimeBodyPart htmlPart = new MimeBodyPart();

            htmlPart.setContent(email.getMessage(), "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);
            message.saveChanges();

            // Log exitoso antes del envío
            Logger.getLogger(EmailSend.class.getName()).info("Intentando enviar email a: " + email.getTo() + " con asunto: " + email.getSubject());
            
            Transport.send(message);
            
            // Log exitoso después del envío
            Logger.getLogger(EmailSend.class.getName()).info("Email enviado exitosamente a: " + email.getTo());
            
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, 
                "NoSuchProviderException - Error de proveedor de email. Destinatario: " + email.getTo() + 
                ", Asunto: " + email.getSubject() + 
                ", Host: " + HOST + 
                ", Puerto: " + PORT_SMTP + 
                ", Usuario: " + USER, ex);
            System.err.println("=== DETALLE NoSuchProviderException ===");
            System.err.println("Mensaje: " + ex.getMessage());
            System.err.println("Causa: " + ex.getCause());
            System.err.println("Stack trace completo:");
            ex.printStackTrace();
        } catch (AddressException ex) {
            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, 
                "AddressException - Error en dirección de email. Destinatario: " + email.getTo() + 
                ", Email remitente: " + MAIL + 
                ", Asunto: " + email.getSubject(), ex);
            System.err.println("=== DETALLE AddressException ===");
            System.err.println("Mensaje: " + ex.getMessage());
            System.err.println("Dirección problemática: " + ex.getRef());
            System.err.println("Posición del error: " + ex.getPos());
            System.err.println("Stack trace completo:");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, 
                "MessagingException - Error en el envío del mensaje. Destinatario: " + email.getTo() + 
                ", Asunto: " + email.getSubject() + 
                ", Host: " + HOST + 
                ", Puerto: " + PORT_SMTP, ex);
            System.err.println("=== DETALLE MessagingException ===");
            System.err.println("Mensaje: " + ex.getMessage());
            System.err.println("Causa raíz: " + ex.getCause());
            
            // Información específica de la excepción de messaging
            if (ex.getNextException() != null) {
                System.err.println("Excepción anidada: " + ex.getNextException().getMessage());
                System.err.println("Tipo de excepción anidada: " + ex.getNextException().getClass().getName());
                
                // Si hay más detalles en la excepción anidada
                Exception nestedEx = ex.getNextException();
                if (nestedEx.getCause() != null) {
                    System.err.println("Causa de la excepción anidada: " + nestedEx.getCause().getMessage());
                }
            }
            
            // Intentar obtener más detalles del servidor SMTP
            System.err.println("Configuración SMTP utilizada:");
            System.err.println("  - Host: " + HOST);
            System.err.println("  - Puerto: " + PORT_SMTP);
            System.err.println("  - Usuario: " + USER);
            System.err.println("  - TLS habilitado: false");
            System.err.println("  - Auth habilitado: false");
            System.err.println("  - Debug habilitado: true");
            
            // Información adicional de la excepción
            System.err.println("Clase de la excepción: " + ex.getClass().getName());
            System.err.println("Mensaje original: " + ex.toString());
            
            System.err.println("Stack trace completo:");
            ex.printStackTrace();
        } catch (Exception ex) {
            // Capturar cualquier otra excepción inesperada
            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, 
                "Excepción inesperada durante el envío de email. Destinatario: " + email.getTo() + 
                ", Asunto: " + email.getSubject() + 
                ", Tipo de excepción: " + ex.getClass().getName(), ex);
            System.err.println("=== EXCEPCIÓN INESPERADA ===");
            System.err.println("Tipo: " + ex.getClass().getName());
            System.err.println("Mensaje: " + ex.getMessage());
            System.err.println("Stack trace completo:");
            ex.printStackTrace();
        }
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package postgresConecction;

import com.mycompany.parcial1.tecnoweb.run;
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

    // Configuración servidor TecnoWeb (por defecto)
    private final static String PORT_SMTP = "25";
    private final static String HOST = "mail.tecnoweb.org.bo";
    private final static String USER = "grupo11sc";
    private final static String MAIL = "grupo11sc@tecnoweb.org.bo";

    private Email email;

    public EmailSend(Email emailP) {
        this.email = emailP;
        //this.email.setFrom(MAIL);
    }

    @Override
    public void run() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║        INICIANDO ENVÍO DE EMAIL (EmailSend.run)         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("Thread actual: " + Thread.currentThread().getName());
        System.out.println("Destinatario del email: " + email.getTo());
        System.out.println("Asunto del email: " + email.getSubject());
        System.out.println("USE_GMAIL flag: " + run.USE_GMAIL);
        
        Properties properties = new Properties();
        Session session;
        String smtpHost;
        String smtpPort;
        String smtpUser;
        String smtpPassword;
        String fromEmail;
        
        // Configurar propiedades según la bandera USE_GMAIL
        if (run.USE_GMAIL) {
            System.out.println("\n>>> USANDO CONFIGURACIÓN GMAIL <<<");
            // ============ CONFIGURACIÓN GMAIL ============
            smtpHost = "smtp.gmail.com";
            smtpPort = run.GMAIL_PORT;
            smtpUser = run.GMAIL_USER;
            smtpPassword = run.GMAIL_APP_PASSWORD;
            fromEmail = run.GMAIL_USER;
            
            properties.setProperty("mail.smtp.host", smtpHost);
            properties.setProperty("mail.smtp.port", smtpPort);
            properties.setProperty("mail.smtp.auth", "true");
            
            // Configuración SSL/TLS para Gmail (SOLUCIÓN AL ERROR DE SSL)
            properties.setProperty("mail.smtp.ssl.enable", "true");
            properties.setProperty("mail.smtp.ssl.trust", smtpHost);
            properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3"); // Habilitar TLS moderno
            properties.setProperty("mail.smtp.ssl.checkserveridentity", "true");
            
            // SocketFactory SSL
            properties.setProperty("mail.smtp.socketFactory.port", smtpPort);
            properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.setProperty("mail.smtp.socketFactory.fallback", "false");
            
            // Timeouts
            properties.setProperty("mail.smtp.connectiontimeout", "10000");
            properties.setProperty("mail.smtp.timeout", "10000");
            properties.setProperty("mail.smtp.writetimeout", "10000");
            
            // Debug
            properties.setProperty("mail.debug", "true");
            properties.setProperty("mail.smtp.debug", "true");
            
            // Crear sesión con autenticación
            session = Session.getInstance(properties, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUser, smtpPassword);
                }
            });
            
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║           CONFIGURACIÓN GMAIL SMTP APLICADA              ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            System.out.println("Host: " + smtpHost);
            System.out.println("Puerto: " + smtpPort + " (SSL)");
            System.out.println("Usuario: " + smtpUser);
            System.out.println("Contraseña: " + (smtpPassword != null && !smtpPassword.isEmpty() ? "***configurada***" : "NO CONFIGURADA"));
            System.out.println("Email remitente: " + fromEmail);
            System.out.println("Destinatario: " + email.getTo());
            System.out.println("Asunto: " + email.getSubject());
            System.out.println("Autenticación: TRUE");
            System.out.println("SSL: HABILITADO (TLSv1.2/TLSv1.3)");
            System.out.println("SocketFactory: javax.net.ssl.SSLSocketFactory");
            
            Logger.getLogger(EmailSend.class.getName()).info("=== CONFIGURACIÓN GMAIL SMTP ===");
            Logger.getLogger(EmailSend.class.getName()).info("Host: " + smtpHost);
            Logger.getLogger(EmailSend.class.getName()).info("Puerto: " + smtpPort + " (SSL)");
            Logger.getLogger(EmailSend.class.getName()).info("Usuario: " + smtpUser);
            Logger.getLogger(EmailSend.class.getName()).info("Email remitente: " + fromEmail);
            Logger.getLogger(EmailSend.class.getName()).info("Destinatario: " + email.getTo());
            Logger.getLogger(EmailSend.class.getName()).info("Asunto: " + email.getSubject());
            Logger.getLogger(EmailSend.class.getName()).info("Autenticación: TRUE");
            Logger.getLogger(EmailSend.class.getName()).info("SSL: HABILITADO");
        } else {
            // ============ CONFIGURACIÓN SERVIDOR TECNOWEB ============
            smtpHost = HOST;
            smtpPort = PORT_SMTP;
            smtpUser = USER;
            fromEmail = MAIL;
            
            properties.setProperty("mail.smtp.host", smtpHost);
            properties.setProperty("mail.smtp.port", smtpPort);
            
            // CONFIGURACIÓN BÁSICA SIN SEGURIDAD (para servidor lento/problemático)
            properties.setProperty("mail.smtp.auth", "false");
            properties.setProperty("mail.smtp.starttls.enable", "false");
            properties.setProperty("mail.smtp.starttls.required", "false");
            properties.setProperty("mail.smtp.ssl.enable", "false");
            properties.setProperty("mail.smtp.tls.enable", "false");
            
            // HABILITAR DEBUG PARA VER RESPUESTAS DEL SERVIDOR
            properties.setProperty("mail.debug", "true");
            properties.setProperty("mail.smtp.debug", "true");
            properties.setProperty("mail.transport.protocol.rset", "true");
            
            // TIMEOUTS MÁXIMOS PARA SERVIDOR MUY LENTO
            properties.setProperty("mail.smtp.connectiontimeout", "60000");
            properties.setProperty("mail.smtp.timeout", "60000");
            properties.setProperty("mail.smtp.writetimeout", "60000");
            
            // CONFIGURACIONES ADICIONALES PARA COMPATIBILIDAD
            properties.setProperty("mail.smtp.quitwait", "false");
            properties.setProperty("mail.smtp.socketFactory.fallback", "true");
            properties.setProperty("mail.smtp.ehlo", "false");
            properties.setProperty("mail.smtp.localhost", "localhost");
            
            session = Session.getDefaultInstance(properties, null);
            
            Logger.getLogger(EmailSend.class.getName()).info("=== CONFIGURACIÓN SMTP BÁSICA (SIN SEGURIDAD) ===");
            Logger.getLogger(EmailSend.class.getName()).info("Host: " + smtpHost);
            Logger.getLogger(EmailSend.class.getName()).info("Puerto: " + smtpPort + " (BÁSICO)");
            Logger.getLogger(EmailSend.class.getName()).info("Usuario: SIN AUTENTICACIÓN");
            Logger.getLogger(EmailSend.class.getName()).info("Email remitente: " + fromEmail);
            Logger.getLogger(EmailSend.class.getName()).info("Destinatario: " + email.getTo());
            Logger.getLogger(EmailSend.class.getName()).info("Asunto: " + email.getSubject());
            Logger.getLogger(EmailSend.class.getName()).info("Autenticación: FALSE");
            Logger.getLogger(EmailSend.class.getName()).info("TLS/SSL: DESHABILITADO");
            Logger.getLogger(EmailSend.class.getName()).info("Timeout: 60 segundos");
            Logger.getLogger(EmailSend.class.getName()).info("EHLO: DESHABILITADO (usar HELO)");
        }
        
        // HABILITAR DEBUG EN LA SESIÓN
        session.setDebug(true);
        Logger.getLogger(EmailSend.class.getName()).info("Debug habilitado: true");
        
        try {
            MimeMessage message;
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
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
            System.out.println("\n>>> INTENTANDO ENVIAR EMAIL VÍA TRANSPORT.SEND() <<<");
            System.out.println("Destinatario: " + email.getTo());
            System.out.println("Asunto: " + email.getSubject());
            Logger.getLogger(EmailSend.class.getName()).info("Intentando enviar email a: " + email.getTo() + " con asunto: " + email.getSubject());
            
            Transport.send(message);
            
            // Log exitoso después del envío
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║              ✅ EMAIL ENVIADO EXITOSAMENTE               ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            System.out.println("Destinatario: " + email.getTo());
            System.out.println("Asunto: " + email.getSubject());
            if (run.USE_GMAIL) {
                System.out.println("Servidor: Gmail SMTP (smtp.gmail.com:" + run.GMAIL_PORT + ")");
            } else {
                System.out.println("Servidor: TecnoWeb (mail.tecnoweb.org.bo:25)");
            }
            Logger.getLogger(EmailSend.class.getName()).info("Email enviado exitosamente a: " + email.getTo());
            
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, 
                "NoSuchProviderException - Error de proveedor de email. Destinatario: " + email.getTo() + 
                ", Asunto: " + email.getSubject() + 
                ", Host: " + smtpHost + 
                ", Puerto: " + smtpPort + 
                ", Usuario: " + smtpUser, ex);
            System.err.println("=== DETALLE NoSuchProviderException ===");
            System.err.println("Mensaje: " + ex.getMessage());
            System.err.println("Causa: " + ex.getCause());
            System.err.println("Stack trace completo:");
            ex.printStackTrace();
        } catch (AddressException ex) {
            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, 
                "AddressException - Error en dirección de email. Destinatario: " + email.getTo() + 
                ", Email remitente: " + fromEmail + 
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
                ", Host: " + smtpHost + 
                ", Puerto: " + smtpPort, ex);
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
            System.err.println("  - Host: " + smtpHost);
            System.err.println("  - Puerto: " + smtpPort);
            System.err.println("  - Usuario: " + smtpUser);
            System.err.println("  - Modo Gmail: " + run.USE_GMAIL);
            System.err.println("  - TLS/SSL habilitado: " + run.USE_GMAIL);
            System.err.println("  - Auth habilitado: " + run.USE_GMAIL);
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

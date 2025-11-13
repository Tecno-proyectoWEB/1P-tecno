package com.mycompany.parcial1.tecnoweb;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nnn
 */
public class run {
    
    // ============================================================
    // CONFIGURACIÓN DE CORREO - CAMBIAR useGmail A true PARA GMAIL
    // ============================================================
    
    /**
     * Bandera para cambiar entre servidor SMTP predeterminado y Gmail
     * false = Usa mail.tecnoweb.org.bo (servidor actual)
     * true = Usa smtp.gmail.com (Gmail con las credenciales configuradas)
     cambia a false el de abajo*/
    public static final boolean USE_GMAIL = false;
    
    // Credenciales de Gmail (solo se usan si USE_GMAIL = true)
    public static final String GMAIL_USER = "marcodavidtoledo@gmail.com";
    public static final String GMAIL_APP_PASSWORD = "vtwavyxqqlusrzbh"; // Sin espacios - importante
    public static final String GMAIL_PORT = "465"; // Puerto SSL para Gmail                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
    
    public static void main(String[] args) {
        System.out.println("===================================");
        System.out.println("Iniciando EmailApp");
        if (USE_GMAIL) {
            System.out.println("Modo: GMAIL SMTP");
            System.out.println("Usuario: " + GMAIL_USER);
            System.out.println("Puerto: " + GMAIL_PORT + " (SSL)");
        } else {
            System.out.println("Modo: SERVIDOR TECNOWEB");
            System.out.println("Servidor: mail.tecnoweb.org.bo");
        }
        System.out.println("===================================");
        
        EmailApp app = new EmailApp();
        app.start();
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package librerias;

/**
 *
 * @author nnn
 */
public class Email {
    public static final String SUBJECT = "Request response";
    private String from;
    private String to;
    private String subject;
    private String message;

    public Email(){
    }
    
    public Email(String to, String subject, String message){
        this.to = to;
        this.subject = subject;
        this.message = message;
    }
    
    public Email(String from, String subject){
        this.from = from;
        this.subject = subject;
    }
    
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMesssage(String messsage) {
        this.message = messsage;
    }
    
    public static Email getEmail(String plain_text){
        return new Email(getFrom(plain_text),getSubject(plain_text));
    }
    private static String getFrom(String plain_text){
        String search = "Return-Path: <";
        int index_begin = plain_text.indexOf(search) + search.length();
        int index_end = plain_text.indexOf(">");
        return plain_text.substring(index_begin, index_end);
    }

    private static String getSubject(String plain_text) {
        try {
            // Define el inicio buscando después de "Subject: "
            String search = "Subject: ";
            int startIndex = plain_text.indexOf(search) + search.length();

            // Define el final del substring basado en varios posibles finales
            String[] possibleEnds = {"To:", "Thread-Topic:", "Content-Type: text/plain;"};
            int endIndex = -1;

            // Busca el primer índice válido para cualquiera de los posibles finales
            for (String end : possibleEnds) {
                int tempIndex = plain_text.indexOf(end, startIndex);
                if (tempIndex != -1 && (endIndex == -1 || tempIndex < endIndex)) {
                    endIndex = tempIndex;
                }
            }

            // Si no se encontró ningún final válido, usar la longitud del texto
            if (endIndex == -1) {
                endIndex = plain_text.length();
            }

            // Extrae la subcadena
            String subject = plain_text.substring(startIndex, endIndex).trim();

            // Quita "RV:" si está presente al inicio del asunto
            String rvPrefix = "RV:";
            if (subject.startsWith(rvPrefix)) {
                subject = subject.substring(rvPrefix.length()).trim();
            }

            return subject + " ";
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }


    @Override
    public String toString() {
        return "[From: " + from + ", To: " + to + ", Subject: " + subject + ", Message: " + message + "]";
    }
}

package librerias;

import interfaces.ICasoUsoListener;
import librerias.analex.Analex;
import librerias.analex.Token;

/**
 * Clase Interpreter se encarga de interpretar los comandos entrantes y enviar
 * las acciones a los manejadores correspondientes.
 */
public class Interpreter implements Runnable {

    private ICasoUsoListener IcasoUsoListener; // Listener para los casos de uso
    private Analex analex; // Analizador léxico

    private String instruccion; // Comando recibido
    private String sender; // Remitente del comando

    public Interpreter(String instruccion, String sender) {
        this.instruccion = instruccion;
        this.sender = sender;
    }

    public ICasoUsoListener getCasoUsoListener() {
        return IcasoUsoListener;
    }

    public void setCasoUsoListener(ICasoUsoListener casoUsoListener) {
        this.IcasoUsoListener = casoUsoListener;
    }

    public String getInstruccion() {
        return instruccion;
    }

    public void setInstruccion(String instruction) {
        this.instruccion = instruccion;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    private void filterEvent(Instruccion instruccion) {
        ParamsAction paramsAction = new ParamsAction(this, sender, this.instruccion);
        paramsAction.setAction(instruccion.getAction());

        int count_params = instruccion.countParams();
        for (int i = 0; i < count_params; i++) {
            int pos = instruccion.getParams(i);
            paramsAction.addParams(analex.getParam(pos));
        }

        switch (instruccion.getCasoUso()) {
            case Token.USUARIO:
                IcasoUsoListener.usuario(paramsAction);
                break;
            case Token.HELP:
                IcasoUsoListener.help(paramsAction);
                break;
            case Token.REGISTER:
                IcasoUsoListener.register(paramsAction);
                break;
            case Token.CATEGORIA:
                IcasoUsoListener.categoria(paramsAction);
                break;
            case Token.PRODUCTO:
                IcasoUsoListener.producto(paramsAction);
                break;
            case Token.TIPOPAGO:
                IcasoUsoListener.metodoPago(paramsAction);
                break;
            case Token.METODOPAGO:
                IcasoUsoListener.metodoPago(paramsAction);
                break;
            case Token.CARRITO:
                IcasoUsoListener.carrito(paramsAction);
                break;
            case Token.NOTAVENTA:
                IcasoUsoListener.notaVenta(paramsAction);
                break;
            case Token.PEDIDO:
                IcasoUsoListener.pedido(paramsAction);
                break;
            case Token.COMPRAR:
                IcasoUsoListener.comprar(paramsAction);
                break;
            default:
                IcasoUsoListener.error(paramsAction);
                break;
        }
    }

    private void tokenError(Token token, String error) {
        ParamsAction paramsAction = new ParamsAction(this, sender);
        paramsAction.setAction(token.getAttribute());
        paramsAction.addParams(instruccion);
        paramsAction.addParams(error);
        IcasoUsoListener.error(paramsAction);
    }

    @Override
    public void run() {
        System.out.println("=== INTERPRETER RUN ===");
        System.out.println("Instrucción: " + instruccion);
        System.out.println("Sender: " + sender);
        
        if (instruccion == null || instruccion.trim().isEmpty()) {
            System.out.println("No se proporcionó un comando válido.");
            return;
        }
        
        analex = new Analex(instruccion);
        Instruccion instruccionObj = new Instruccion(); // TokenCommand
        Token token;

        while ((token = analex.Preanalisis()).getName() != Token.END && token.getName() != Token.ERROR) {
            System.out.println("Token encontrado - Name: " + token.getName() + ", Attribute: " + token.getAttribute());
            
            if (token.getName() == Token.CU) {
                instruccionObj.setCasoUso(token.getAttribute()); // ID del CU
                System.out.println("Caso de uso establecido: " + token.getAttribute());
            } else if (token.getName() == Token.ACTION) {
                instruccionObj.setAction(token.getAttribute()); // ID de la acción
                System.out.println("Acción establecida: " + token.getAttribute());
            } else if (token.getName() == Token.PARAMS) {
                instruccionObj.addParams(token.getAttribute()); // La posición del parámetro
                System.out.println("Parámetro agregado: " + token.getAttribute());
            }
            analex.next();
        }

        System.out.println("Token final - Name: " + token.getName() + ", Attribute: " + token.getAttribute());
        
        if (token.getName() == Token.END) {
            System.out.println("Comando analizado exitosamente");
            filterEvent(instruccionObj); // Se analizó el comando con éxito
        } else if (token.getName() == Token.ERROR) {
            System.out.println("Error en análisis léxico: " + analex.lexeme());
            tokenError(token, analex.lexeme()); // Se produjo un error en el análisis
        }
    }

}

package CalculadoraException;

public class CalculadoraException extends RuntimeException {
    
    public CalculadoraException(String collection){
        super("¡¡¡Calculator Exception!!!     " + collection);
    }

}

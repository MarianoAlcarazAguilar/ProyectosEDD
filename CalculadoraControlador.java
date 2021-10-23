package Calculadora;

import ADTsException.ADTsException;
import java.util.Objects;
import java.util.StringTokenizer;
import CalculadoraException.CalculadoraException;
import Stacks.ArrayStack;

public class CalculadoraControlador {
    
    private String intFixExp;
    private String postFixExp;
    private double resultado;

    public CalculadoraControlador() {
        intFixExp = "";
        postFixExp = "";
        resultado = 0.0;
    }

    public CalculadoraControlador(String intFixExp) {
        this();
        this.intFixExp = intFixExp;
        
    }

    public String getIntFixExp() {
        return intFixExp;
    }

    public String getPostFixExp() {
        return postFixExp;
    }

    public double getResultado() {
        return resultado;
    }

    public void setIntFixExp(String intFixExp) {
        this.intFixExp = intFixExp;
        //Falta agregar que tiene que cambiar la postFixExp
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CalculadoraControlador other = (CalculadoraControlador) obj;
        return Objects.equals(this.intFixExp, other.intFixExp);
    }
    
    public String toString(){
        return "\nExpresión intFix: " + intFixExp +
                "\nExpresión postFix: " + postFixExp +
                "\nResultado: " + resultado;
    }
    
    public boolean revisaParentesis(){
        if(intFixExp == null)
            throw new CalculadoraException("Expresión intFix es null. Método: revisaParentesis()");
        
        if(intFixExp.equals(""))
            throw new CalculadoraException("Expresión intFix está vacía. Método: revisaParentesis()");
        
        boolean resp = true;
        StringTokenizer st = new StringTokenizer(intFixExp, "+-*/()", true);
        ArrayStack<String> stackParen = new ArrayStack<>();
        
        String element;
        while(resp && st.hasMoreTokens()){
            element = st.nextToken();
            if(element.equals("(")){
                stackParen.push(element);
            } else {
                if(element.equals(")")){
                    try{
                        stackParen.pop();
                    } catch (ADTsException e){
                        resp = false;
                    }
                }
            }
        }
        return resp && stackParen.isEmpty();
    }
    
    /**
    *La calculadora NO entiende los paréntesis como multiplicación
    *sino solo como orden de operaciones
     * @return 
    */
    public boolean revisaOperadores() {
        if(intFixExp == null || intFixExp.equals(""))
            throw new CalculadoraException("Expresión intFix es null. Método: revisaOperadores()");
        
        if(intFixExp.equals(""))
            throw new CalculadoraException("Expresión intFix está vacía. Método: revisaOperadores()");
        
        boolean resp = true;
        StringTokenizer st = new StringTokenizer(intFixExp, "+-*/()", true);
        ArrayStack<String> stack = new ArrayStack<>();
        
        String element;
        String firstElement = st.nextToken();
        
        if(firstElement.equals("*") || firstElement.equals("/") || firstElement.equals("+"))
            resp = false;
        
        while (resp && st.hasMoreTokens()){
            element = st.nextToken();
            if(element.equals("+") || element.equals("-") || element.equals("*") || element.equals("/")){
                stack.push(element);
            } else {
                if(!element.equals("(") && !element.equals(")")){
                    try {
                        stack.pop();
                    } catch (ADTsException e){
                        resp = false;
                    }
                }
            }
        }
        return resp && stack.isEmpty();
    }    
    
    private int precedence(String token){
        
        int valor = -1;
        switch (token){
            case "+":
            case "-":
                valor = 1;
                break;
            case "*":
            case "/":
                valor = 2;
                break;
            case "^":   //Este caso se hace con fines de práctica, ya que la calculadora final no tendrá este operador
                valor = 3;
                break;
        }
        return valor;
    }
    
    private int encuentraOperador(String token){
        
        int valor = -1;
        switch (token){
            case "+":
                valor = 1;
                break;
            case "-":
                valor = 2;
                break;
            case "*":
                valor = 3;
                break;
            case "/":
                valor = 4;
                break;
        }
        return valor;
    }
    
    public boolean infixToPostFix(){
        boolean valida;
        try{
            valida = this.revisaParentesis() && this.revisaOperadores();
        } catch (Exception e){
            throw new CalculadoraException("Expresión no manejable. Método: infixToPostFix");
        }
        if(valida){
            String result = "";
            ArrayStack<String> stack = new ArrayStack<>();
            StringTokenizer st = new StringTokenizer(intFixExp, "+-/*()", true);
            while(st.hasMoreTokens()) {
                String c = st.nextToken();

                //Verificamos si c es operador

                if(precedence(c)>0){
                    while(stack.isEmpty()==false && precedence(stack.peek())>=precedence(c)){
                        result = result + stack.pop() + " ";
                    }
                    stack.push(c);

                }else 
                    if(c.equals(")")){
                    String x = stack.pop();
                    while(!x.equals("(")){
                        result = result + x + " ";
                        x = stack.pop();
                    }
                }else if(c.equals("(")){
                    stack.push(c);
                }else{
                    //character is neither operator nor ( 
                    result = result + c + " ";
                }
            }
            for (int i = 0; i <=stack.size() ; i++) {
                result = result + stack.pop() + " ";
            }
            postFixExp = result;
        } else {
            postFixExp = "Expresión Infix No Válida";
        }

        return valida;
    }
    
    public void calculaResultado(){
        try{ //Intentamos convertir la expresión para manejarla
            infixToPostFix();
        }catch (Exception e) {
            throw new CalculadoraException("Expresión no manejable");
        }
        
        StringTokenizer st = new StringTokenizer(postFixExp);
        ArrayStack<Double> stack = new ArrayStack<>();
        String element;
        Double num1, num2, aux;
        while(st.hasMoreTokens()){
            element = st.nextToken();
            
            //Verificamos si es operador
            if(precedence(element) > 0){
                num1 = stack.pop();
                num2 = stack.pop();  //Este menos el de arriba
                //Operamos dependiendo del operando
                switch(encuentraOperador(element)){
                    case 1: //Esta es suma
                        aux = num2 + num1;
                        break;
                    case 2: //Esta es resta
                        aux = num2 - num1;
                        break;
                    case 3: //Esta es multiplicación
                        aux = num2 * num1;
                        break;
                    case 4: //Esta es división
                        try{
                            aux = num2 / num1;
                        } catch (Exception e) {
                            throw new RuntimeException("División entre 0 no está definida");
                        }
                        break;
                    default:
                        aux = 0.0;
                }
                stack.push(aux);
                
            } else {
                Double num = Double.parseDouble(element);
                stack.push(num);
            }
        }
        resultado = stack.pop();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}

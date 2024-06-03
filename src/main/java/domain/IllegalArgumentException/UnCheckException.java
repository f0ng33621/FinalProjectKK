package domain.IllegalArgumentException;

public class UnCheckException extends Exception{
    public UnCheckException(){
        super();
    }
    public UnCheckException(String message){
        super(message);
    }
    public UnCheckException(Throwable cause){
        super(cause);
    }
    public UnCheckException(String message, Throwable cause){
        super(message,cause);
    }
}

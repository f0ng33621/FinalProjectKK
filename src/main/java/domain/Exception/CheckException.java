package domain.Exception;

public class CheckException extends  Exception{

    public CheckException(){
        super();
    }
    public CheckException(String Message){
        super(Message);
    }
    public CheckException(Throwable cause){
        super(cause);
    }
    public CheckException(String message, Throwable cause){
        super(message,cause);
    }
}

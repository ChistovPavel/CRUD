package CRUD.Exceptions;

public class CRUDException extends Exception {
    public CRUDException(String message, Throwable cause) {super(message, cause);}
    public CRUDException(String message)
    {
        super(message);
    }
    public CRUDException(Throwable cause)
    {
        super(cause);
    }
}

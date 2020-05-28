package CRUD.Exceptions;

/**
 * Класс исключение, применяемый при возникновении ошибок при выполнении CRUD операция с хранилищем.
 * */
public abstract class CRUDException extends Exception
{
    private int code;

    public CRUDException(int inCode, String message, Throwable cause)
    {
        super(message, cause);
        this.code = inCode;
    }
    public CRUDException(int inCode, String message)
    {
        super(message);
        this.code = inCode;
    }
    public CRUDException(int inCode, Throwable cause)
    {
        super(cause);
        this.code = inCode;
    }

    public int getCode(){return this.code;}
}

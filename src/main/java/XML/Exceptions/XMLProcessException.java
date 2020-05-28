package XML.Exceptions;

import CRUD.Exceptions.CRUDException;

/**
 * Класс исключение, применяемый при возникновении ошибок при выполнении CRUD операция с XML хранилищем.
 * */
public class XMLProcessException extends CRUDException
{
    public static final int XML_INIT_EXCEPTION = 1;
    public static final int XML_FORMAT_EXCEPTION = 2;
    public static final int XML_USER_SEARCH_EXCEPTION = 3;
    public static final int XML_UPDATE_FILE_EXCEPTION = 4;
    public static final int XML_ATTRIBUTE_EXCEPTION = 5;

    public XMLProcessException(int inCode, String message)
    {
        super(inCode, message);
    }

    public XMLProcessException(int inCode, Throwable cause)
    {
        super(inCode, cause);
    }

    public XMLProcessException(int inCode, String message, Throwable cause)
    {
        super(inCode, message, cause);
    }

    @Override
    public String getMessage()
    {
        return new StringBuilder(super.getMessage()).append("\texception code: ").append(super.getCode()).toString();
    }
}

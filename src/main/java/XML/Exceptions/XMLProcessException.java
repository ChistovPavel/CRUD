package XML.Exceptions;

import CRUD.Exceptions.CRUDException;
import lombok.Getter;

public class XMLProcessException extends CRUDException {

    @Getter private short code;

    public static final short XML_INIT_EXCEPTION = 1;
    public static final short XML_FORMAT_EXCEPTION = 2;
    public static final short XML_USER_SEARCH_EXCEPTION = 3;
    public static final short XML_UPDATE_FILE_EXCEPTION = 4;
    public static final short XML_ATTRIBUTE_EXCEPTION = 5;

    public XMLProcessException(short inCode, String message)
    {
        super(message);
        this.code = inCode;
    }

    public XMLProcessException(short inCode, Throwable cause)
    {
        super(cause);
        this.code = inCode;
    }

    public XMLProcessException(short inCode, String message, Throwable cause)
    {
        super(message, cause);
        this.code = inCode;
    }
}

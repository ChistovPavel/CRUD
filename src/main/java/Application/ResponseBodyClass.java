package Application;

import lombok.Getter;
import lombok.Setter;

public class ResponseBodyClass {

    private final String URI = "user/";
    @Getter @Setter private String userURI;

    public ResponseBodyClass(Integer id)
    {
        this.userURI = URI + id.toString();
    }
}

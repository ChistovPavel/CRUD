package application;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс предназначен для хранения тела ответа на входящие запросы.
 * */
public class ResponseBodyClass
{
    private final String URI = "user/";
    @Getter @Setter private String userURI;

    /**
     * Конструктор класса.
     * @param id пользователя. На основе данного id будет сформирован URI,
     *           по которому можно будет получить, обновить или удалить данные пользователя.
     *           URI имеет формат: user/{id}.
     * */
    public ResponseBodyClass(Integer id)
    {
        this.userURI = URI + id.toString();
    }
}

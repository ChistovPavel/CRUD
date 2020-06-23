package application;

import exceptions.CRUDException;
import exceptions.XMLProcessException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Класс, который занимается приемом входящих http запросов, обрабатывает их и формирует ответ.
 * */
@RestController
public class InputController
{
    private static Logger logger = LogManager.getLogger(InputController.class);

    private CRUD xmlHandler;

    /**
     * Конструктор класса.
     * */
    public InputController()
    {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ConfigurationClass.class);
        this.xmlHandler = applicationContext.getBean(CRUD.class);
    }

    /**
     * Метод для получения, обработки и формирования ответа.
     * Обработка HTTP POST запроса на добавление информации о пользователе с определенным id.
     * @param hsr объект {@link HttpServletResponse}, позволяющий настраивать HTTP ответ.
     * <p>Данный функционал недоступен, поэтому в ответе устанавливается статус NOT_FOUND.</p>
     * */
    @RequestMapping(method = POST, value = "/user/{id}")
    private void create(HttpServletResponse hsr)
    {
        hsr.setStatus(HttpStatus.NOT_FOUND.value());
        return;
    }

    /**
     * Метод для получения, обработки и формирования ответа.
     * Обработка HTTP POST запроса на добавление информации о пользователе.
     * @param user объект {@link User}, содержащий информацию о пользователе. Данный объект формирует Spring'ом
     *             по средствам десериализации тела входящего запроса.
     * @param hsr объект {@link HttpServletResponse}, позволяющий настраивать HTTP ответ.
     *
     * <p>В случае успешного добавления, в ответе устанавливается статус CREATED. В заголовках ответа устанавливается
     *            заголов "location" со значение URI добавленного пользователя.</p>
     * <p>В случае отсутствия какой либо информации о пользователе, в ответе устанавливается статус BAD_REQUEST.</p>
     * <p>В случае ошибки работы с XML файлом, в ответе устанавливается статус INTERNAL_SERVER_ERROR.</p>
     * */
    @RequestMapping(method = POST, value = "/users")
    private void create(@RequestBody User user, HttpServletResponse hsr)
    {
        logger.info("Input create request - user: " + user.toString());

        if (user == null || user.hasNull() == true)
        {
            logger.info("Input user data contains null value fields");
            hsr.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }
        else
        {
            Integer id = null;
            try
            {
                id = xmlHandler.create(user);
            }
            catch (CRUDException e)
            {
                logger.error(e);
                hsr.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                return;
            }

            logger.info("Create new user info successfully");
            hsr.setStatus(HttpStatus.CREATED.value());
            hsr.setHeader("Location", new ResponseBodyClass(id).getUserURI());
            return;
        }
    }

    /**
     * Метод для получения, обработки и формирования ответа.
     * Обработка HTTP GET запроса на получение информации о всех пользователях.
     * @param hsr объект {@link HttpServletResponse}, позволяющий настраивать HTTP ответ.
     *
     * <p>В случае успешного получения данных, в ответе устанавливается статус OK. Тело ответа содержится список
     *            URI для каждой ячейки хранилица.</p>
     * <p>В случае ошибки работы с XML файлом, в ответе устанавливается статус INTERNAL_SERVER_ERROR.</p>
     * */
    @RequestMapping(method = GET, value = "/users")
    private List<ResponseBodyClass> read(HttpServletResponse hsr)
    {
        logger.info("Input read all users URI request");

        List<Integer> id = null;

        try
        {
            id = xmlHandler.read();
        }
        catch (CRUDException e) {
            logger.error(e);
            hsr.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return null;
        }

        List<ResponseBodyClass> responseBodyClasses = new ArrayList<>();

        for (Integer var : id)
        {
            responseBodyClasses.add(new ResponseBodyClass(var));
        }

        hsr.setStatus(HttpStatus.OK.value());
        logger.info("read all users URI successfully");
        return responseBodyClasses;
    }

    /**
     * Метод для получения, обработки и формирования ответа.
     * Обработка HTTP GET запроса на получение информации о пользователе с конкретным id.
     * @param id идентификатор пользователя в хранилище. Является частью URI.
     * @param hsr объект {@link HttpServletResponse}, позволяющий настраивать HTTP ответ.
     *
     * <p>В случае успешного получения данных, в ответе устанавливается статус OK. Тело ответа содержит информацию о
     *            получкнном пользователе.</p>
     * <p>Если id равен null, в ответе устанавливается BAD_REQUEST.</p>
     * <p>В случае остутствия информации о пользовател, в ответе устанавливается статус NOT_FOUND.</p>
     * <p>В случае ошибки работы с XML файлом, в ответе устанавливается статус INTERNAL_SERVER_ERROR.</p>
     * */
    @RequestMapping(method = GET, value = "/user/{id}")
    private User read(@PathVariable Integer id, HttpServletResponse hsr)
    {
        logger.info("Input read request - id: " + id);

        if (id == null)
        {
            logger.info("Input id is null");
            hsr.setStatus(HttpStatus.BAD_REQUEST.value());
            return null;
        }
        User user = null;
        try
        {
            user = xmlHandler.read(id);
        }
        catch (CRUDException e)
        {
            if (e.getCode() == XMLProcessException.XML_USER_SEARCH_EXCEPTION)
            {
                logger.error(new StringBuilder("User with id: ").append(id).append("does not exist").toString());
                hsr.setStatus(HttpStatus.NOT_FOUND.value());
                return null;
            }

            logger.error(e);
            hsr.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return null;
        }

        logger.info("Read user info successfully - id: " + id);
        hsr.setStatus(HttpStatus.OK.value());
        return user;
    }

    /**
     * Метод для получения, обработки и формирования ответа.
     * Обработка HTTP GET запроса на получение информации о пользователе с заданными параметрами.
     * Заданные параметры передаются в качестве параметров запроса.
     * В качестве параметра можно задать имя (firstName), фамилию (secondName) или дату рождения (birthDate).
     * @param hsr объект {@link HttpServletResponse}, позволяющий настраивать HTTP ответ.
     *
     * <p>В случае успешного получения данных, в ответе устанавливается статус OK. Тело ответа содержится список URI
     *           для соответствующих ячеек хранилица.</p>
     * <p>В случае ошибки работы с XML файлом, в ответе устанавливается статус INTERNAL_SERVER_ERROR.</p>
     * */
    @RequestMapping(method = GET, value = "/users/")
    private List<ResponseBodyClass> read(@RequestParam(value = "firstName") String firstName,
                                        @RequestParam(value = "secondName") String secondName,
                                        @RequestParam(value = "birthDate") String birthDate,
                                        HttpServletResponse hsr)
    {
        User parameters = new User(firstName, secondName, birthDate);

        logger.info(new StringBuilder("Input read users URI request with params - ")
        .append(parameters.toString()).toString());

        if (parameters.getFirstName() == null &&
                parameters.getSecondName() == null &&
                parameters.getBirthDate() == null)
        {
            return this.read(hsr);
        }

        List<Integer> id;

        try
        {
            id = xmlHandler.read(parameters);
        }
        catch (CRUDException e)
        {
            logger.error(e);
            hsr.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return null;
        }

        List<ResponseBodyClass> responseBodyClasses = new ArrayList<>();

        for (Integer var : id)
        {
            responseBodyClasses.add(new ResponseBodyClass(var));
        }

        hsr.setStatus(HttpStatus.OK.value());
        logger.info("read parameterised users URI successfully");
        return responseBodyClasses;
    }

    /**
     * Метод для получения, обработки и формирования ответа.
     * Обработка HTTP DELETE запроса на удаление информации о всех пользователях.
     * @param hsr объект {@link HttpServletResponse}, позволяющий настраивать HTTP ответ.
     * <p>Данный функционал недоступен, поэтому в ответе устанавливается статус NOT_FOUND.</p>
     * */
    @RequestMapping(method = DELETE, value = "/users")
    private void delete(HttpServletResponse hsr)
    {
        hsr.setStatus(HttpStatus.NOT_FOUND.value());
        return;
    }

    /**
     * Метод для получения, обработки и формирования ответа.
     * Обработка HTTP DELETE запроса на удаление информации о пользователе с конкретным id.
     * @param id идентификатор пользователя в хранилище. Является частью URI.
     * @param hsr объект {@link HttpServletResponse}, позволяющий настраивать HTTP ответ.
     *
     * <p>В случае успешного удаления данных, в ответе устанавливается статус NO_CONTENT.</p>
     * <p>Если id равен null, в ответе устанавливается BAD_REQUEST.</p>
     * <p>В случае остутствия информации о пользовател, в ответе устанавливается статус NOT_FOUND.</p>
     * <p>В случае ошибки работы с XML файлом, в ответе устанавливается статус INTERNAL_SERVER_ERROR.</p>
     * */
    @RequestMapping(method = DELETE, value = "/user/{id}")
    private void delete(@PathVariable Integer id,
                          HttpServletResponse hsr)
    {
        logger.info("Input delete user data request - id " + id);

        if (id == null)
        {
            logger.info("Input id is null");
            hsr.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        try
        {
            xmlHandler.delete(id);
        }
        catch (CRUDException e)
        {
            if (e.getCode() == XMLProcessException.XML_USER_SEARCH_EXCEPTION)
            {
                logger.error(new StringBuilder("User with id: ").append(id).append("does not exist").toString());
                hsr.setStatus(HttpStatus.NOT_FOUND.value());
                return;
            }

            logger.error(e);
            hsr.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return;
        }

        logger.info("Delete user successfully");
        hsr.setStatus(HttpStatus.NO_CONTENT.value());
        return;
    }

    /**
     * Метод для получения, обработки и формирования ответа.
     * Обработка HTTP PUT запроса на обновление информации о всех пользователях.
     * @param hsr объект {@link HttpServletResponse}, позволяющий настраивать HTTP ответ.
     * <p>Данный функционал недоступен, поэтому в ответе устанавливается статус NOT_FOUND.</p>
     * */
    @RequestMapping(method = PUT, value = "/users")
    private void update(HttpServletResponse hsr)
    {
        hsr.setStatus(HttpStatus.NOT_FOUND.value());
        return;
    }

    /**
     * Метод для получения, обработки и формирования ответа.
     * Обработка HTTP PUT запроса на обновление информации о пользователе с конкретным id.
     * @param id идентификатор пользователя в хранилище. Является частью URI.
     * @param user объект {@link User}, содержащий информацию о пользователе. Данный объект формирует Spring'ом
     *             по средствам десериализации тела входящего запроса. Некоторые поля могут быть не заданы.
     * @param hsr объект {@link HttpServletResponse}, позволяющий настраивать HTTP ответ.
     *
     * <p>В случае успешного удаления данных, в ответе устанавливается статус OK. Тело ответа содержит
     *            информацию об обновленном пользователе.</p>
     * <p>Если id равен null, в ответе устанавливается BAD_REQUEST.</p>
     * <p>В случае остутствия информации о пользовател, в ответе устанавливается статус NOT_FOUND.</p>
     * <p>В случае ошибки работы с XML файлом, в ответе устанавливается статус INTERNAL_SERVER_ERROR.</p>
     * */
    @RequestMapping(method = PUT, value = "/user/{id}")
    private User update(@PathVariable Integer id,
                                    @RequestBody User user,
                                    HttpServletResponse hsr)
    {
        logger.info(new StringBuilder("Input update user data request - id ").append(id).append("\t").append(user.toString()));

        if (id == null)
        {
            logger.info("Input id is null");
            hsr.setStatus(HttpStatus.BAD_REQUEST.value());
            return null;
        }
        if (user.getFirstName() == null &&
                user.getSecondName() == null &&
                user.getBirthDate() == null)
        {
            logger.info("Input user data is null");
            hsr.setStatus(HttpStatus.BAD_REQUEST.value());
            return null;
        }

        User newUser = null;

        try
        {
            newUser = xmlHandler.update(id, user);
        }
        catch (CRUDException e)
        {
            if (e.getCode() == XMLProcessException.XML_USER_SEARCH_EXCEPTION)
            {
                logger.error(new StringBuilder("User with id: ").append(id).append("does not exist").toString());
                hsr.setStatus(HttpStatus.NOT_FOUND.value());
                return null;
            }

            logger.error(e);
            hsr.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return null;
        }

        logger.info("Update user info successfully");
        hsr.setStatus(HttpStatus.OK.value());
        return newUser;
    }
}

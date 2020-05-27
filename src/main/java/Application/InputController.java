package Application;

import XML.Exceptions.XMLProcessException;
import XML.XMLHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class InputController
{
    private static Logger logger = LogManager.getLogger(InputController.class);

    @Value("${storagePath}")
    private String path;
    private XMLHandler xmlHandler;

    @PostConstruct
    public void init() throws XMLProcessException {
        xmlHandler = new XMLHandler(path);
    }

    @RequestMapping(method = POST, value = "/user/{id}")
    public void create(HttpServletResponse hsr)
    {
        hsr.setStatus(HttpStatus.NOT_FOUND.value());
        return;
    }

    @RequestMapping(method = POST, value = "/users")
    public void create(@RequestBody User user, HttpServletResponse hsr)
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
            catch (XMLProcessException e)
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

    @RequestMapping(method = GET, value = "/users")
    public List<ResponseBodyClass> read(HttpServletResponse hsr)
    {
        logger.info("Input read all users URI request");

        List<Integer> id = null;

        try
        {
            id = xmlHandler.read();
        }
        catch (XMLProcessException e) {
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

    @RequestMapping(method = GET, value = "/user/{id}")
    public User read(@PathVariable Integer id, HttpServletResponse hsr)
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
        catch (XMLProcessException e)
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

    @RequestMapping(method = GET, value = "/users/")
    public List<ResponseBodyClass> read(@RequestParam(value = "firstName") String firstName,
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
        catch (XMLProcessException e)
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

    @RequestMapping(method = DELETE, value = "/users")
    public void delete(HttpServletResponse hsr)
    {
        hsr.setStatus(HttpStatus.NOT_FOUND.value());
        return;
    }

    @RequestMapping(method = DELETE, value = "/user/{id}")
    public void delete(@PathVariable Integer id,
                          HttpServletResponse hsr)
    {
        logger.info("Input delete user data request - ID" + id);

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
        catch (XMLProcessException e)
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

    @RequestMapping(method = PUT, value = "/users")
    public void update(HttpServletResponse hsr)
    {
        hsr.setStatus(HttpStatus.NOT_FOUND.value());
        return;
    }

    @RequestMapping(method = PUT, value = "/user/{id}")
    public User update(@PathVariable Integer id,
                                    @RequestBody User user,
                                    HttpServletResponse hsr)
    {
        logger.info("Input delete user data request - ID" + id);

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
        catch (XMLProcessException e)
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

package application;

import application.User;
import exceptions.CRUDException;

import java.util.List;

/**
 * Интерфейс определяет методы для работы с хранилищем для текущего REST API.
 * */
public interface CRUD
{
    /**
     * Метод добовления информации о пользователе в хранилище.
     * @param user объект {@link User} хранит информацию о пользователе.
     * @return идентификатор добавленного пользователя в хранилище.
     * @exception CRUDException throws в случае возникновения ошибки добовления.
     * */
    Integer create(User user) throws CRUDException;
    /**
     * Метод получения информации о всех пользователях в хранилище.
     * @return список {@link List} идентификаторов полученных пользователей.
     * @exception CRUDException throws в случае возникновения ошибки получения информации.
     * */
    List<Integer> read() throws CRUDException;
    /**
     * Метод получения информации о всех пользователях в хранилище с заданными параметрами.
     * @param parameters объект {@link User} содержит параметры, по которым будет осуществлено получение данных.
     * @return список {@link List} идентификаторов полученных пользователей.
     * @exception CRUDException throws в случае возникновения ошибки получения информации.
     * */
    List<Integer> read(User parameters) throws CRUDException;
    /**
     * Метод получения информации о пользователе в хранилище по заданному идентификатору.
     * @param id идентификатор пользователя в хранилище.
     * @return объект {@link User} содержит информацию о полученном пользователе.
     * @exception CRUDException throws в случае возникновения ошибки получения информации.
     * */
    User read(Integer id) throws CRUDException;
    /**
     * Метод обновление информации о пользователе в хранилище по заданному идентификатору.
     * @param id идентификатор пользователя в хранилище;
     * @param user содержит информацию для обновления.
     * @return объект {@link User} содержит информацию об обновленном пользователе.
     * @exception CRUDException throws в случае возникновения ошибки обновления информации.
     * */
    User update(Integer id, User user) throws CRUDException;
    /**
     * Метод удаления информации о пользователе в хранилище по заданному идентификатору.
     * @param id идентификатор пользователя в хранилище.
     * @exception CRUDException throws в случае возникновения ошибки удаления информации.
     * */
    void delete(Integer id) throws CRUDException;
}
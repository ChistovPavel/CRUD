package CRUD;

import Application.User;
import CRUD.Exceptions.CRUDException;

import java.util.List;

public interface CRUD {
    Integer create(User user) throws CRUDException;
    List<Integer> read() throws CRUDException;
    List<Integer> read(User user) throws CRUDException;
    User read(Integer id) throws CRUDException;
    User update(Integer id, User user) throws CRUDException;
    void delete(Integer id) throws CRUDException;
}
package Application;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс для хранения и обработки информации о пользователе.
 * */
public class User{

    @Setter @Getter private String firstName;
    @Setter @Getter private String secondName;
    @Setter @Getter private String birthDate;

    /**
     * Конструктор класса.
     * @param inFirstName имя пользователя;
     * @param inSecondName фамилия пользователя;
     * @param inBirthDate дата рождения пользователя.
     * */
    public User(String inFirstName,String inSecondName,String inBirthDate)
    {
        this.firstName = inFirstName;
        this.secondName = inSecondName;
        this.birthDate = inBirthDate;
    }

    /**
     * Переопределение методы {@link Object#toString()}.
     * @return строка {@link String} формата: firstName + \t + secondName + \t + birthDate.
     * */
    @Override
    public String toString() {
        return new StringBuilder("first name: ")
                .append(this.firstName)
                .append("\tsecond name: ")
                .append(this.secondName)
                .append("\tbirth date: ")
                .append(this.birthDate).toString();
    }

    /**
     * Метод проверки атрибутов класса на значение null.
     * @return true, если хотя бы один из атрибутов равен null, или false, если все атрибуты не равны null.
     * */
    public boolean hasNull()
    {
        if (this.firstName == null || this.secondName == null || this.birthDate == null)
        {
            return true;
        }
        return false;
    }
}

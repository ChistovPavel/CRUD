package Application;

import lombok.Getter;
import lombok.Setter;

public class User{

    @Setter @Getter private String firstName;
    @Setter @Getter private String secondName;
    @Setter @Getter private String birthDate;

    public User(String inFirstName,String inSecondName,String inBirthDate)
    {
        this.firstName = inFirstName;
        this.secondName = inSecondName;
        this.birthDate = inBirthDate;
    }

    @Override
    public String toString() {
        return new StringBuilder("first name: ")
                .append(this.firstName)
                .append("\tsecond name: ")
                .append(this.secondName)
                .append("\tbirth date: ")
                .append(this.birthDate).toString();
    }

    public boolean hasNull()
    {
        if (this.firstName == null || this.secondName == null || this.birthDate == null)
        {
            return true;
        }
        return false;
    }
}

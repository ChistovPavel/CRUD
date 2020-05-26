package Application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    static
    {
        //System.setProperty("LogFileName", new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date()));
        System.setProperty("LogDir", "log");
        System.setProperty("LogDirXMLUtil", "XMLUtil");
        System.setProperty("LogDirXMLHandler", "XMLHandler");
        System.setProperty("LogDirController", "Controller");
    }

    public static void main(String[] args)
    {
        SpringApplication.run(Main.class, args);
    }
}

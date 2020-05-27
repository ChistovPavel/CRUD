package Application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main
{
    @Value("${log.dir}")
    private static String LogDir;
    @Value("${Log.Dir.XMLUtil}")
    private static String LogDirXMLUtil;
    @Value("${Log.Dir.XMLHandler}")
    private static String LogDirXMLHandler;
    @Value("${Log.Dir.Controller}")
    private static String LogDirController;

    static
    {
        //System.setProperty("LogFileName", new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date()));
    }

    public static void main(String[] args)
    {
        System.setProperty("LogDir", LogDir);
        System.setProperty("LogDirXMLUtil", LogDirXMLUtil);
        System.setProperty("LogDirXMLHandler", LogDirXMLHandler);
        System.setProperty("LogDirController", LogDirController);
        SpringApplication.run(Main.class, args);
    }
}

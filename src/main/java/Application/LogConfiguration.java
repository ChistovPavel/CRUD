package Application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class LogConfiguration
{
    @Value("LogDir")
    private String LogDir;

    @Value("LogDirXMLUtil")
    private String LogDirXMLUtil;

    @Value("LogDirXMLHandler")
    private String LogDirXMLHandler;

    @Value("LogDirController")
    private String LogDirController;

    @Bean
    @Order(1)
    public void logConf()
    {
        System.setProperty("LogDir", LogDir);
        System.setProperty("LogDirXMLUtil", LogDirXMLUtil);
        System.setProperty("LogDirXMLHandler", LogDirXMLHandler);
        System.setProperty("LogDirController", LogDirController);
    }
}

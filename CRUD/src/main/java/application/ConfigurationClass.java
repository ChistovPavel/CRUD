package application;

import exceptions.XMLProcessException;
import xml.XMLHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Конфигурационный класс для дополнительной настройки Spring
 * */
@Configuration
@PropertySource("classpath:application.properties")
public class ConfigurationClass
{
    @Value("${storage.path}")
    private String path;

    @Bean
    public CRUD configureXMLHandler() throws XMLProcessException {
        return new XMLHandler(path);
    }
}

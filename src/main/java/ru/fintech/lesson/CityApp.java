package ru.fintech.lesson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CityApp {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    private static final Logger log = LoggerFactory.getLogger(CityApp.class);

    public static void main(String[] args) {
        processJsonFile("city.json");
        processJsonFile("city-error.json");
    }

    public static void processJsonFile(String fileName) {
        try {
            log.debug("Попытка загрузить файл: {}", fileName);
            InputStream inputStream = CityApp.class.getClassLoader().getResourceAsStream(fileName);

            if (inputStream == null) {
                log.warn("Файл {} не найден в resources", fileName);
            }

            log.debug("Файл: {} найден, начинаем парсить json", fileName);

            ObjectMapper mapper = new ObjectMapper();
            City city = mapper.readValue(inputStream, City.class);
            log.info("{} успешно распаршен", fileName);

            String xml = city.toXML();
            log.info("Конвертируем объект в XML формат");


            Files.write(Paths.get(String.format("%s.xml", fileName)), xml.getBytes());
            log.info("Сохраняем XML файл");
        } catch (JsonParseException e) {
            log.error("Произошла ошибка во время парсинга json файла: {}", fileName, e);
        }
        catch (Exception e) {
            log.error("Произошла ошибка во время обработки json файла: {}", fileName, e);
        }
    }
}

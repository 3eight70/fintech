package ru.fintech.lesson;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {
    private String slug;
    private Coords coords;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coords {
        private double lat;
        private double lon;
    }

    public String toXML() throws Exception {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.writeValueAsString(this);
    }
}

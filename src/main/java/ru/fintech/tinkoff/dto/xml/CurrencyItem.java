package ru.fintech.tinkoff.dto.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyItem {
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    private String id;

    @JacksonXmlProperty(localName = "Name")
    private String name;

    @JacksonXmlProperty(localName = "EngName")
    private String engName;

    @JacksonXmlProperty(localName = "Nominal")
    private String nominal;

    @JacksonXmlProperty(localName = "ParentCode")
    private String parentCode;

    @JacksonXmlProperty(localName = "ISO_Num_Code")
    private String isoNumCode;

    @JacksonXmlProperty(localName = "ISO_Char_Code")
    private String isoCharCode;
}

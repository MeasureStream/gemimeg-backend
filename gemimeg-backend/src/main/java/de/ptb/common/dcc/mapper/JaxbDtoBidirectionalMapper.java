package de.ptb.common.dcc.mapper;

import javax.annotation.Nonnull;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;

public interface JaxbDtoBidirectionalMapper<JAXB, DTO> {

  DTO mapToDto(JAXB jaxbObject);

  JAXB mapToJaxbObject(DTO dto);

  default XMLGregorianCalendar convertDate(@Nonnull LocalDate date) {
    GregorianCalendar gregorianCalendar = GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault()));
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  default XMLGregorianCalendar convertDateTime(@Nonnull LocalDateTime dateTime) {
    GregorianCalendar gregorianCalendar = GregorianCalendar.from(dateTime.atZone(ZoneId.systemDefault()));
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  default LocalDate convertDate(@Nonnull XMLGregorianCalendar calendar) {
    return LocalDate.of(calendar.getYear(), calendar.getMonth(), calendar.getDay());
  }

  default LocalDateTime convertDateTime(@Nonnull XMLGregorianCalendar calendar) {
    return LocalDateTime.of(convertDate(calendar), LocalTime.of(calendar.getHour(), calendar.getMinute(),
        calendar.getSecond()));
  }
}

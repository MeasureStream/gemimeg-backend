package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dto.LocationDto;
import de.ptb.common.dcc.api.v1.dto.RichContentDto;
import de.ptb.common.dcc.util.DccServiceUtil;
import de.ptb.common.dcc.xjc.generated.LocationType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.RichContentType;
import jakarta.xml.bind.JAXBElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;

@Component
public class LocationMapper implements JaxbDtoBidirectionalMapper<LocationType, LocationDto> {

  public static final String COUNTRY_CODE = "countryCode";
  public static final String STATE = "state";
  public static final String POST_CODE = "postCode";
  public static final String CITY = "city";
  public static final String STREET = "street";
  public static final String STREET_NO = "streetNo";
  public static final String POST_OFFICE_BOX = "postOfficeBox";
  public static final String FURTHER = "further";

  private final RichContentMapper richContentMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public LocationMapper(RichContentMapper richContentMapper, ObjectFactory objectFactory) {
    this.richContentMapper = richContentMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  public LocationDto mapToDto(LocationType jaxbObject) {
    LocationDto target = new LocationDto();
    for (JAXBElement<?> jaxbElement : jaxbObject.getCityOrCountryCodeOrPostCode()) {
      if (StringUtils.equalsIgnoreCase(COUNTRY_CODE, jaxbElement.getName().getLocalPart())) {
        target.setCountryCode(jaxbElement.getValue().toString());
      }
      if (StringUtils.equalsIgnoreCase(STATE, jaxbElement.getName().getLocalPart())) {
        target.setStateCode(jaxbElement.getValue().toString());
      }
      if (StringUtils.equalsIgnoreCase(POST_CODE, jaxbElement.getName().getLocalPart())) {
        target.setPostalCode(jaxbElement.getValue().toString());
      }
      if (StringUtils.equalsIgnoreCase(CITY, jaxbElement.getName().getLocalPart())) {
        target.setCity(jaxbElement.getValue().toString());
      }
      if (StringUtils.equalsIgnoreCase(STREET, jaxbElement.getName().getLocalPart())) {
        target.setStreet(jaxbElement.getValue().toString());
      }
      if (StringUtils.equalsIgnoreCase(STREET_NO, jaxbElement.getName().getLocalPart())) {
        target.setHouseNumber(jaxbElement.getValue().toString());
      }
      if (StringUtils.equalsIgnoreCase(POST_OFFICE_BOX, jaxbElement.getName().getLocalPart())) {
        target.setPoBox(jaxbElement.getValue().toString());
      }
      if (StringUtils.equalsIgnoreCase(FURTHER, jaxbElement.getName().getLocalPart())) {
        target.setAdditionalInformation(richContentMapper.mapToDto((RichContentType) jaxbElement.getValue()));
      }
    }
    return target;
  }

  @Override
  public LocationType mapToJaxbObject(LocationDto dto) {
    LocationType target = objectFactory.createLocationType();
    optionallyAddField(target, dto.getCountryCode(), COUNTRY_CODE);
    optionallyAddField(target, dto.getStateCode(), STATE);
    optionallyAddField(target, dto.getPostalCode(), POST_CODE);
    optionallyAddField(target, dto.getCity(), CITY);
    optionallyAddField(target, dto.getStreet(), STREET);
    optionallyAddField(target, dto.getHouseNumber(), STREET_NO);
    optionallyAddField(target, dto.getPoBox(), POST_OFFICE_BOX);
    optionallyAddFurther(target, dto.getAdditionalInformation());
    return target;
  }

  private void optionallyAddField(@Nonnull LocationType target, @Nullable String source, String fieldName) {
    if (StringUtils.isNotBlank(source)) {
      target.getCityOrCountryCodeOrPostCode().add(new JAXBElement<>(new QName(DccServiceUtil.NS_DCC, fieldName),
          String.class, source));
    }
  }

  private void optionallyAddFurther(@Nonnull LocationType target, @Nullable RichContentDto source) {
    // Formula content is in this context not allowed (due to schema restrictions!)
    if (isNotEmpty(source) && !isNotEmpty(source.getFormulaContent())) {
      target.getCityOrCountryCodeOrPostCode()
          .add(new JAXBElement<>(new QName(DccServiceUtil.NS_DCC, FURTHER), RichContentType.class,
              richContentMapper.mapToJaxbObject(source)));
    }
  }
}

/**
 * Copyright 2025 Physikalisch-Technische Bundesanstalt
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.LocationDto;
import de.ptb.common.dcc.api.v1.dcc.RichContentDto;
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

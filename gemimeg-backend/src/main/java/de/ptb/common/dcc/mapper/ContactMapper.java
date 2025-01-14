package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.ContactDto;
import de.ptb.common.dcc.xjc.generated.ContactType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Component
public class ContactMapper implements JaxbDtoBidirectionalMapper<ContactType, ContactDto> {

  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final LocationMapper locationMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public ContactMapper(LanguageSpecificStringsMapper languageSpecificStringsMapper, LocationMapper locationMapper,
                       ObjectFactory objectFactory) {
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.locationMapper = locationMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public ContactDto mapToDto(@Nonnull ContactType jaxbObject) {
    ContactDto target = new ContactDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (jaxbObject.getName() != null) {
      target.setName(languageSpecificStringsMapper.mapToDto(jaxbObject.getName()));
    }
    target.setEMailAddress(jaxbObject.getEMail());
    target.setPhoneNumber(jaxbObject.getPhone());
    if (jaxbObject.getLocation() != null) {
      target.setLocation(locationMapper.mapToDto(jaxbObject.getLocation()));
    }
    return target;
  }

  @Override
  @Nonnull
  public ContactType mapToJaxbObject(@Nonnull ContactDto dto) {
    ContactType target = objectFactory.createContactType();
    if (StringUtils.isNotBlank(dto.getId())) {
      target.setId(dto.getId());
    }
    if (isNotEmpty(dto.getRefIds())) {
      target.getRefId().addAll(dto.getRefIds());
    }
    if (isNotEmpty(dto.getRefTypes())) {
      target.getRefType().addAll(dto.getRefTypes());
    }
    if (isNotEmpty(dto.getName())) {
      target.setName(languageSpecificStringsMapper.mapToJaxbObject(dto.getName()));
    }
    target.setEMail(dto.getEMailAddress());
    target.setPhone(dto.getPhoneNumber());
    if (isNotEmpty(dto.getLocation())) {
      target.setLocation(locationMapper.mapToJaxbObject(dto.getLocation()));
    }
    return target;
  }
}

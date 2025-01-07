package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dto.IdentificationDto;
import de.ptb.common.dcc.xjc.generated.IdentificationType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Component
public class IdentificationMapper implements JaxbDtoBidirectionalMapper<IdentificationType, IdentificationDto> {

  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public IdentificationMapper(LanguageSpecificStringsMapper languageSpecificStringsMapper, ObjectFactory objectFactory) {
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  public IdentificationDto mapToDto(IdentificationType jaxbObject) {
    IdentificationDto target = new IdentificationDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (jaxbObject.getName() != null && !jaxbObject.getName().getContent().isEmpty()) {
      target.setName(languageSpecificStringsMapper.mapToDto(jaxbObject.getName()));
    }
    if (StringUtils.isNotBlank(jaxbObject.getIssuer())) {
      target.setIssuer(jaxbObject.getIssuer());
    } else {
      target.setIssuer("other");
    }
    target.setValue(jaxbObject.getValue());
    return target;
  }

  @Override
  public IdentificationType mapToJaxbObject(IdentificationDto dto) {
    IdentificationType target = objectFactory.createIdentificationType();
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
    if (StringUtils.isNotBlank(dto.getIssuer())) {
      target.setIssuer(dto.getIssuer());
    } else {
      target.setIssuer("other");
    }
    target.setValue(dto.getValue());
    return target;
  }
}

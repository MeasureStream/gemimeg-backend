package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.LangTextPair;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.StringWithLangType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Component
public class LangTextPairMapper implements JaxbDtoBidirectionalMapper<StringWithLangType, LangTextPair> {

  private final ObjectFactory objectFactory;

  @Autowired
  public LangTextPairMapper(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  @Override
  public LangTextPair mapToDto(StringWithLangType jaxbObject) {
    LangTextPair target = LangTextPair.of(jaxbObject.getLang(), jaxbObject.getValue());
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    return target;
  }

  @Override
  public StringWithLangType mapToJaxbObject(LangTextPair dto) {
    StringWithLangType target = objectFactory.createStringWithLangType();
    if (StringUtils.isNotBlank(dto.getId())) {
      target.setId(dto.getId());
    }
    if (isNotEmpty(dto.getRefIds())) {
      target.getRefId().addAll(dto.getRefIds());
    }
    if (isNotEmpty(dto.getRefTypes())) {
      target.getRefType().addAll(dto.getRefTypes());
    }
    target.setLang(dto.getLang());
    target.setValue(dto.getText());
    return target;
  }
}

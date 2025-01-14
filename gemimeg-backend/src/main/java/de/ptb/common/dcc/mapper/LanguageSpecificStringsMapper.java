package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.LangTextPair;
import de.ptb.common.dcc.api.v1.dcc.LanguageSpecificStringsDto;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.StringWithLangType;
import de.ptb.common.dcc.xjc.generated.TextType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;

@Component
public class LanguageSpecificStringsMapper implements JaxbDtoBidirectionalMapper<TextType, LanguageSpecificStringsDto> {

  private final LangTextPairMapper langTextPairMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public LanguageSpecificStringsMapper(LangTextPairMapper langTextPairMapper,
                                       ObjectFactory objectFactory) {
    this.langTextPairMapper = langTextPairMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public LanguageSpecificStringsDto mapToDto(@Nonnull TextType jaxbObject) {
    LanguageSpecificStringsDto target = new LanguageSpecificStringsDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    List<LangTextPair> content = jaxbObject.getContent().stream()
        .filter(stringWithLangType -> StringUtils.isNotBlank(stringWithLangType.getValue()))
        .map(langTextPairMapper::mapToDto).toList();
    if (!content.isEmpty()) {
      target.setContent(content);
    }
    return target;
  }

  @Override
  @Nonnull
  public TextType mapToJaxbObject(@Nonnull LanguageSpecificStringsDto dto) {
    TextType target = objectFactory.createTextType();
    if (StringUtils.isNotBlank(dto.getId())) {
      target.setId(dto.getId());
    }
    if (isNotEmpty(dto.getRefIds())) {
      target.getRefId().addAll(dto.getRefIds());
    }
    List<StringWithLangType> content = dto.getContent().stream()
        .filter(langTextPair -> StringUtils.isNotBlank(langTextPair.getText()))
        .map(langTextPairMapper::mapToJaxbObject).toList();
    if (!content.isEmpty()) {
      target.getContent().addAll(content);
    }
    return target;
  }
}

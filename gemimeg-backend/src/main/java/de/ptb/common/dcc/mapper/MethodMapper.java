package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.MethodDto;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.UsedMethodType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Component
public class MethodMapper implements JaxbDtoBidirectionalMapper<UsedMethodType, MethodDto> {

  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final RichContentMapper richContentMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public MethodMapper(LanguageSpecificStringsMapper languageSpecificStringsMapper, RichContentMapper richContentMapper,
                      ObjectFactory objectFactory) {
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.richContentMapper = richContentMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public MethodDto mapToDto(@Nonnull UsedMethodType jaxbObject) {
    MethodDto target = new MethodDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (jaxbObject.getName() != null) {
      target.setName(languageSpecificStringsMapper.mapToDto(jaxbObject.getName()));
    }
    if (jaxbObject.getDescription() != null) {
      target.setDescription(richContentMapper.mapToDto(jaxbObject.getDescription()));
    }
    target.setNorms(jaxbObject.getNorm());
    return target;
  }

  @Override
  @Nonnull
  public UsedMethodType mapToJaxbObject(@Nonnull MethodDto dto) {
    UsedMethodType target = objectFactory.createUsedMethodType();
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
    if (isNotEmpty(dto.getDescription())) {
      target.setDescription(richContentMapper.mapToJaxbObject(dto.getDescription()));
    }
    if (dto.getNorms() != null && !dto.getNorms().isEmpty()) {
      List<String> targetNorms = dto.getNorms().stream()
          .filter(StringUtils::isNotBlank)
          .toList();
      if (!targetNorms.isEmpty()) {
        target.getNorm().addAll(targetNorms);
      }
    }
    return target;
  }
}

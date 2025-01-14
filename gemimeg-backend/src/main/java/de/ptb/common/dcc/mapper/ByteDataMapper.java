package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.ByteDataDto;
import de.ptb.common.dcc.xjc.generated.ByteDataType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;

@Component
public class ByteDataMapper implements JaxbDtoBidirectionalMapper<ByteDataType, ByteDataDto> {

  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public ByteDataMapper(LanguageSpecificStringsMapper languageSpecificStringsMapper, ObjectFactory objectFactory) {
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public ByteDataDto mapToDto(@Nonnull ByteDataType jaxbObject) {
    ByteDataDto target = new ByteDataDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    target.setContent(jaxbObject.getDataBase64());
    target.setMimeType(jaxbObject.getMimeType());
    target.setFileName(jaxbObject.getFileName());
    if (jaxbObject.getName() != null) {
      target.setName(languageSpecificStringsMapper.mapToDto(jaxbObject.getName()));
    }
    return target;
  }

  @Override
  @Nonnull
  public ByteDataType mapToJaxbObject(@Nonnull ByteDataDto dto) {
    ByteDataType target = objectFactory.createByteDataType();
    if (StringUtils.isNotBlank(dto.getId())) {
      target.setId(dto.getId());
    }
    if (isNotEmpty(dto.getRefIds())) {
      target.getRefId().addAll(dto.getRefIds());
    }
    if (dto.getContent() != null && dto.getContent().length > 0) {
      target.setDataBase64(dto.getContent());
    }
    if (StringUtils.isNotBlank(dto.getMimeType())) {
      target.setMimeType(dto.getMimeType());
    }
    if (StringUtils.isNotBlank(dto.getFileName())) {
      target.setFileName(dto.getFileName());
    }
    if (isNotEmpty(dto.getName())) {
      target.setName(languageSpecificStringsMapper.mapToJaxbObject(dto.getName()));
    }
    return target;
  }
}

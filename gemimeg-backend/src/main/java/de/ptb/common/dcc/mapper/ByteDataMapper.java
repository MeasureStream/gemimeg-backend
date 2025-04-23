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

import de.ptb.common.dcc.api.v1.dcc.ByteDataDto;
import de.ptb.common.dcc.xjc.generated.ByteDataType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;

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

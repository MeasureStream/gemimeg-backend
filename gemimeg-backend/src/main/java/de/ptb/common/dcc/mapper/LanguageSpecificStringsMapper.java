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

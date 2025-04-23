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

import de.ptb.common.dcc.api.v1.dcc.LanguageSpecificStringsDto;
import de.ptb.common.dcc.api.v1.dcc.RichContentDto;
import de.ptb.common.dcc.xjc.generated.ByteDataType;
import de.ptb.common.dcc.xjc.generated.FormulaType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.RichContentType;
import de.ptb.common.dcc.xjc.generated.StringWithLangType;
import de.ptb.common.dcc.xjc.generated.TextType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static de.ptb.common.dcc.api.v1.dcc.LanguageSpecificStringsDto.LANGUAGE_DE;
import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Component
public class RichContentMapper implements JaxbDtoBidirectionalMapper<RichContentType, RichContentDto> {

  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final ByteDataMapper byteDataMapper;
  private final FormulaMapper formulaMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public RichContentMapper(LanguageSpecificStringsMapper languageSpecificStringsMapper, ByteDataMapper byteDataMapper,
                           FormulaMapper formulaMapper, ObjectFactory objectFactory) {
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.byteDataMapper = byteDataMapper;
    this.formulaMapper = formulaMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public RichContentDto mapToDto(@Nonnull RichContentType jaxbObject) {
    RichContentDto target = new RichContentDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (jaxbObject.getName() != null) {
      target.setName(languageSpecificStringsMapper.mapToDto(jaxbObject.getName()));
    }
    for (Object content : jaxbObject.getContentAndFileAndFormula()) {
      if (content instanceof StringWithLangType) {
        LanguageSpecificStringsDto languageSpecificStrings = target.getTextContent();
        if (languageSpecificStrings == null) {
          languageSpecificStrings = new LanguageSpecificStringsDto();
        }
        languageSpecificStrings.setId(((StringWithLangType) content).getId());
        String key = ((StringWithLangType) content).getLang();
        if (StringUtils.isBlank(key)) {
          key = LANGUAGE_DE;
        }
        languageSpecificStrings.add(key, ((StringWithLangType) content).getValue());
        target.setTextContent(languageSpecificStrings);
      }
      if (content instanceof ByteDataType) {
        target.setByteDataContent(byteDataMapper.mapToDto((ByteDataType) content));
      }
      if (content instanceof FormulaType) {
        target.setFormulaContent(formulaMapper.mapToDto((FormulaType) content));
      }
    }
    return target;
  }

  @Override
  @Nonnull
  public RichContentType mapToJaxbObject(@Nonnull RichContentDto dto) {
    RichContentType target = objectFactory.createRichContentType();
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
    if (isNotEmpty(dto.getTextContent())) {
      TextType text = languageSpecificStringsMapper.mapToJaxbObject(dto.getTextContent());
      text.getContent().forEach(content -> target.getContentAndFileAndFormula().add(content));
    }
    if (isNotEmpty(dto.getByteDataContent())) {
      target.getContentAndFileAndFormula().add(byteDataMapper.mapToJaxbObject(dto.getByteDataContent()));
    }
    if (isNotEmpty(dto.getFormulaContent())) {
      target.getContentAndFileAndFormula().add(formulaMapper.mapToJaxbObject(dto.getFormulaContent()));
    }
    return target;
  }
}

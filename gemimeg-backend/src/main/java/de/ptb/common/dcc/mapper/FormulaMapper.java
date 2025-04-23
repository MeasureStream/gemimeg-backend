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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ptb.common.dcc.api.v1.dcc.FormulaDto;
import de.ptb.common.dcc.xjc.generated.FormulaType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.XmlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Base64;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Slf4j
@Component
public class FormulaMapper implements JaxbDtoBidirectionalMapper<FormulaType, FormulaDto> {

  private final ObjectFactory objectFactory;
  private final ObjectMapper objectMapper;

  @Autowired
  public FormulaMapper(ObjectFactory objectFactory, ObjectMapper objectMapper) {
    this.objectFactory = objectFactory;
    this.objectMapper = objectMapper;
  }

  @Override
  @Nonnull
  public FormulaDto mapToDto(@Nonnull FormulaType jaxbObject) {
    FormulaDto target = new FormulaDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (StringUtils.isNotBlank(jaxbObject.getLatex())) {
      target.setType(FormulaDto.FormulaType.LATEX);
      target.setContent(jaxbObject.getLatex());
    } else if (jaxbObject.getMathml() != null && jaxbObject.getMathml().getAny() != null) {
      target.setType(FormulaDto.FormulaType.MATHML);
      try {
        target.setContent(Base64.getEncoder().encodeToString(objectMapper.writeValueAsBytes(jaxbObject.getMathml())));
      } catch (JsonProcessingException e) {
        log.warn(e.getMessage());
      }
    }
    return target;
  }

  @Override
  @Nonnull
  public FormulaType mapToJaxbObject(@Nonnull FormulaDto dto) {
    FormulaType target = objectFactory.createFormulaType();
    if (StringUtils.isNotBlank(dto.getId())) {
      target.setId(dto.getId());
    }
    if (isNotEmpty(dto.getRefIds())) {
      target.getRefId().addAll(dto.getRefIds());
    }
    if (isNotEmpty(dto.getRefTypes())) {
      target.getRefType().addAll(dto.getRefTypes());
    }
    if (StringUtils.isNotBlank(dto.getContent())) {
      if (dto.getType().equals(FormulaDto.FormulaType.LATEX)) {
        target.setLatex(dto.getContent());
      } else if (dto.getType().equals(FormulaDto.FormulaType.MATHML)) {
        try {
          target.setMathml(objectMapper.readValue(Base64.getDecoder().decode(dto.getContent()), XmlType.class));
        } catch (IOException e) {
          log.warn(e.getMessage());
        }
      }
    }
    return target;
  }
}

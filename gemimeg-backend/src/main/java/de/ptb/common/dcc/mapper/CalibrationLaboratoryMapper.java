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

import de.ptb.common.dcc.api.v1.dcc.CalibrationLaboratoryDto;
import de.ptb.common.dcc.xjc.generated.CalibrationLaboratoryType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;

@Component
public class CalibrationLaboratoryMapper implements
    JaxbDtoBidirectionalMapper<CalibrationLaboratoryType, CalibrationLaboratoryDto> {

  private final ContactMapper contactMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public CalibrationLaboratoryMapper(ContactMapper contactMapper, ObjectFactory objectFactory) {
    this.contactMapper = contactMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public CalibrationLaboratoryDto mapToDto(@Nonnull CalibrationLaboratoryType jaxbObject) {
    CalibrationLaboratoryDto target = new CalibrationLaboratoryDto();
    target.setCalibrationLaboratoryCode(jaxbObject.getCalibrationLaboratoryCode());
    if (jaxbObject.getContact() != null) {
      target.setContact(contactMapper.mapToDto(jaxbObject.getContact()));
    }
    return target;
  }

  @Override
  @Nonnull
  public CalibrationLaboratoryType mapToJaxbObject(@Nonnull CalibrationLaboratoryDto dto) {
    CalibrationLaboratoryType target = objectFactory.createCalibrationLaboratoryType();
    target.setCalibrationLaboratoryCode(dto.getCalibrationLaboratoryCode());
    if (isNotEmpty(dto.getContact())) {
      target.setContact(contactMapper.mapToJaxbObject(dto.getContact()));
    }
    return target;
  }
}

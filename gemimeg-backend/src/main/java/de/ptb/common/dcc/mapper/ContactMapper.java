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

import de.ptb.common.dcc.api.v1.dcc.ContactDto;
import de.ptb.common.dcc.api.v1.dcc.LanguageSpecificStringsDto;
import de.ptb.common.dcc.xjc.generated.ContactType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.TextType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Component
public class ContactMapper implements JaxbDtoBidirectionalMapper<ContactType, ContactDto> {

  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final LocationMapper locationMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public ContactMapper(LanguageSpecificStringsMapper languageSpecificStringsMapper, LocationMapper locationMapper,
                       ObjectFactory objectFactory) {
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.locationMapper = locationMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public ContactDto mapToDto(@Nonnull ContactType jaxbObject) {
    ContactDto target = new ContactDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (jaxbObject.getName() != null) {
      LanguageSpecificStringsDto name = languageSpecificStringsMapper.mapToDto(jaxbObject.getName());
      if (StringUtils.isBlank(name.getContent().getFirst().getLang())) {
        name.getContent().getFirst().setLang("de");
      }
      target.setName(name);
    }
    if (StringUtils.isNotBlank(jaxbObject.getEMail())) {
      target.setEMailAddress(jaxbObject.getEMail());
    }
    if (StringUtils.isNotBlank(jaxbObject.getPhone())) {
      target.setPhoneNumber(jaxbObject.getPhone());
    }
    if (jaxbObject.getLocation() != null) {
      target.setLocation(locationMapper.mapToDto(jaxbObject.getLocation()));
    }
    return target;
  }

  @Override
  @Nonnull
  public ContactType mapToJaxbObject(@Nonnull ContactDto dto) {
    ContactType target = objectFactory.createContactType();
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
      TextType name = languageSpecificStringsMapper.mapToJaxbObject(dto.getName());
      if (StringUtils.isBlank(name.getContent().getFirst().getLang())) {
        name.getContent().getFirst().setLang("de");
      }
      target.setName(name);
    }
    if (StringUtils.isNotBlank(dto.getEMailAddress())) {
      target.setEMail(dto.getEMailAddress());
    }
    if (StringUtils.isNotBlank(dto.getPhoneNumber())) {
      target.setPhone(dto.getPhoneNumber());
    }
    if (isNotEmpty(dto.getLocation())) {
      target.setLocation(locationMapper.mapToJaxbObject(dto.getLocation()));
    }
    return target;
  }
}

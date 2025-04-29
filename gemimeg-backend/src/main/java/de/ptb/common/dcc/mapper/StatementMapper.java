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

import de.ptb.common.dcc.api.v1.dcc.StatementDto;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.StatementMetaDataType;
import de.ptb.common.dcc.xjc.generated.StringConformityStatementStatusType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Slf4j
@Component
public class StatementMapper implements JaxbDtoBidirectionalMapper<StatementMetaDataType, StatementDto> {

  private final LocationMapper locationMapper;
  private final RichContentMapper richContentMapper;
  private final DataMapper dataMapper;
  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final ContactMapper contactMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public StatementMapper(LocationMapper locationMapper, RichContentMapper richContentMapper,
                         DataMapper dataMapper, LanguageSpecificStringsMapper languageSpecificStringsMapper,
                         ContactMapper contactMapper, ObjectFactory objectFactory) {
    this.locationMapper = locationMapper;
    this.richContentMapper = richContentMapper;
    this.dataMapper = dataMapper;
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.contactMapper = contactMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public StatementDto mapToDto(@Nonnull StatementMetaDataType jaxbObject) {
    StatementDto target = new StatementDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (jaxbObject.getDate() != null) {
      target.setDate(convertDate(jaxbObject.getDate()));
    }
    if (jaxbObject.getName() != null) {
      target.setName(languageSpecificStringsMapper.mapToDto(jaxbObject.getName()));
    }
    if (isNotEmpty(jaxbObject.getCountryCodeISO31661())) {
      target.setCountryCodes(jaxbObject.getCountryCodeISO31661());
    }
    if (jaxbObject.getDescription() != null) {
      target.setDescription(richContentMapper.mapToDto(jaxbObject.getDescription()));
    }
    if (isNotEmpty(jaxbObject.getNorm())) {
      target.setNorms(jaxbObject.getNorm());
    }
    if (isNotEmpty(jaxbObject.getReference())) {
      target.setReferences(jaxbObject.getReference());
    }
    if (jaxbObject.getDeclaration() != null) {
      target.setDeclaration(richContentMapper.mapToDto(jaxbObject.getDeclaration()));
    }
    target.setConvention(jaxbObject.getConvention());
    target.setTraceable(jaxbObject.isTraceable());
    target.setNonSIDefinition(jaxbObject.getNonSIDefinition());
    target.setNonSIUnit(jaxbObject.getNonSIUnit());
    if (jaxbObject.getLocation() != null) {
      target.setLocation(locationMapper.mapToDto(jaxbObject.getLocation()));
    }
    if (jaxbObject.getData() != null) {
      target.setData(dataMapper.mapToDto(jaxbObject.getData()));
    }
    if (jaxbObject.getConformity() != null) {
      target.setConformity(jaxbObject.getConformity().value());
    }
    if (jaxbObject.getConformityXMLList() != null && !jaxbObject.getConformityXMLList().isEmpty()) {
      target.setConformityXMLList(jaxbObject.getConformityXMLList().stream()
          .map(StringConformityStatementStatusType::value)
          .toList());
    }
    if (jaxbObject.getRespAuthority() != null) {
      target.setResponsibleAuthority(contactMapper.mapToDto(jaxbObject.getRespAuthority()));
    }
    if (jaxbObject.getPeriod() != null) {
      target.setPeriod(jaxbObject.getPeriod().toString());
    }
    if (jaxbObject.isValid() != null) {
      target.setValid(jaxbObject.isValid());
    }
    if (jaxbObject.getValidXMLList() != null && !jaxbObject.getValidXMLList().isEmpty()) {
      target.setValidXMLList(jaxbObject.getValidXMLList());
    }
    return target;
  }

  @Override
  @Nonnull
  public StatementMetaDataType mapToJaxbObject(@Nonnull StatementDto dto) {
    StatementMetaDataType target = objectFactory.createStatementMetaDataType();
    if (StringUtils.isNotBlank(dto.getId())) {
      target.setId(dto.getId());
    }
    if (isNotEmpty(dto.getRefIds())) {
      target.getRefId().addAll(dto.getRefIds());
    }
    if (isNotEmpty(dto.getRefTypes())) {
      target.getRefType().addAll(dto.getRefTypes());
    }
    if (dto.getDate() != null) {
      target.setDate(convertDate(dto.getDate()));
    }
    if (isNotEmpty(dto.getName())) {
      target.setName(languageSpecificStringsMapper.mapToJaxbObject(dto.getName()));
    }
    if (isNotEmpty(dto.getCountryCodes())) {
      target.getCountryCodeISO31661().addAll(dto.getCountryCodes());
    }
    if (isNotEmpty(dto.getDescription())) {
      target.setDescription(richContentMapper.mapToJaxbObject(dto.getDescription()));
    }
    if (isNotEmpty(dto.getNorms())) {
      target.getNorm().addAll(dto.getNorms());
    }
    if (isNotEmpty(dto.getReferences())) {
      target.getReference().addAll(dto.getReferences());
    }
    if (isNotEmpty(dto.getDeclaration())) {
      target.setDeclaration(richContentMapper.mapToJaxbObject(dto.getDeclaration()));
    }
    target.setConvention(dto.getConvention());
    target.setTraceable(dto.getTraceable());
    target.setNonSIDefinition(dto.getNonSIDefinition());
    target.setNonSIUnit(dto.getNonSIUnit());
    if (isNotEmpty(dto.getLocation())) {
      target.setLocation(locationMapper.mapToJaxbObject(dto.getLocation()));
    }
    if (dto.getData() != null && !dto.getData().isEmpty()) {
      target.setData(dataMapper.mapToJaxbObject(dto.getData()));
    }
    if (StringUtils.isNotBlank(dto.getConformity())) {
      target.setConformity(StringConformityStatementStatusType.fromValue(dto.getConformity()));
    }
    if (dto.getConformityXMLList() != null && !dto.getConformityXMLList().isEmpty()) {
      target.getConformityXMLList().addAll(dto.getConformityXMLList().stream()
          .map(StringConformityStatementStatusType::fromValue)
          .toList());
    }
    if (isNotEmpty(dto.getResponsibleAuthority())) {
      target.setRespAuthority(contactMapper.mapToJaxbObject(dto.getResponsibleAuthority()));
    }
    if (StringUtils.isNotBlank(dto.getPeriod())) {
      try {
        target.setPeriod(DatatypeFactory.newInstance().newDuration(dto.getPeriod()));
      } catch (DatatypeConfigurationException | IllegalArgumentException e) {
        log.warn("Unable to convert period (time interval): {}", e.getMessage());
      }
    }
    if (dto.getValid() != null) {
      target.setValid(dto.getValid());
    }
    if (dto.getValidXMLList() != null && !dto.getValidXMLList().isEmpty()) {
      target.getValidXMLList().addAll(dto.getValidXMLList());
    }
    return target;
  }
}

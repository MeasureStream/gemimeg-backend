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

import de.ptb.common.dcc.api.v1.dcc.EquipmentDto;
import de.ptb.common.dcc.api.v1.dcc.IdentificationListDto;
import de.ptb.common.dcc.xjc.generated.EquipmentClassType;
import de.ptb.common.dcc.xjc.generated.HashType;
import de.ptb.common.dcc.xjc.generated.IdentificationListType;
import de.ptb.common.dcc.xjc.generated.MeasuringEquipmentType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Component
public class EquipmentMapper implements JaxbDtoBidirectionalMapper<MeasuringEquipmentType, EquipmentDto> {

  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final ContactNotStrictMapper contactMapper;
  private final IdentificationMapper identificationMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public EquipmentMapper(LanguageSpecificStringsMapper languageSpecificStringsMapper, ContactNotStrictMapper contactMapper,
                         IdentificationMapper identificationMapper, ObjectFactory objectFactory) {
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.contactMapper = contactMapper;
    this.identificationMapper = identificationMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  public EquipmentDto mapToDto(MeasuringEquipmentType jaxbObject) {
    EquipmentDto target = new EquipmentDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (jaxbObject.getName() != null) {
      target.setName(languageSpecificStringsMapper.mapToDto(jaxbObject.getName()));
    }
    target.setModel(jaxbObject.getModel());
    if (jaxbObject.getEquipmentClass() != null && !jaxbObject.getEquipmentClass().isEmpty()) {
      target.setClassId(jaxbObject.getEquipmentClass().getFirst().getClassID());
      target.setClassReference(jaxbObject.getEquipmentClass().getFirst().getReference());
    }
    if (jaxbObject.getManufacturer() != null) {
      target.setManufacturer(contactMapper.mapToDto(jaxbObject.getManufacturer()));
    }
    if (jaxbObject.getIdentifications() != null && jaxbObject.getIdentifications().getIdentification() != null) {
      IdentificationListDto identificationList = new IdentificationListDto();
      identificationList.addAll(jaxbObject.getIdentifications().getIdentification().stream()
          .map(identificationMapper::mapToDto)
          .toList());
      target.setIdentifications(identificationList);
    }
    if (jaxbObject.getCertificate() != null) {
      target.setCertificateValue(jaxbObject.getCertificate().getValue());
      target.setCertificateId(jaxbObject.getCertificate().getId());
      if (jaxbObject.getCertificate().getReferral() != null) {
        target.setCertificateReferral(languageSpecificStringsMapper.mapToDto(jaxbObject.getCertificate().getReferral()));
        target.setCertificateReferralId(jaxbObject.getCertificate().getReferralID());
      }
      target.setCertificateProcedure(jaxbObject.getCertificate().getProcedure());
    }
    return target;
  }

  @Override
  public MeasuringEquipmentType mapToJaxbObject(EquipmentDto dto) {
    MeasuringEquipmentType target = objectFactory.createMeasuringEquipmentType();
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
    target.setModel(dto.getModel());
    if (StringUtils.isNotBlank(dto.getClassId()) && StringUtils.isNotBlank(dto.getClassReference())) {
      EquipmentClassType equipmentClass = objectFactory.createEquipmentClassType();
      equipmentClass.setClassID(dto.getClassId());
      equipmentClass.setReference(dto.getClassReference());
      target.getEquipmentClass().add(equipmentClass);
    }
    if (isNotEmpty(dto.getManufacturer())) {
      target.setManufacturer(contactMapper.mapToJaxbObject(dto.getManufacturer()));
    }
    if (dto.getIdentifications() != null && !dto.getIdentifications().isEmpty()) {
      IdentificationListType identificationList = objectFactory.createIdentificationListType();
      identificationList.getIdentification().addAll(dto.getIdentifications().stream()
          .map(identificationMapper::mapToJaxbObject)
          .toList());
      target.setIdentifications(identificationList);
    }
    if (StringUtils.isNotBlank(dto.getCertificateValue()) || StringUtils.isNotBlank(dto.getCertificateId())) {
      HashType certificate = objectFactory.createHashType();
      certificate.setValue(dto.getCertificateValue());
      certificate.setId(dto.getCertificateId());
      if (dto.getCertificateReferral() != null) {
        certificate.setReferral(languageSpecificStringsMapper.mapToJaxbObject(dto.getCertificateReferral()));
        certificate.setReferralID(dto.getCertificateReferralId());
      }
      certificate.setProcedure(dto.getCertificateProcedure());
      target.setCertificate(certificate);
    }
    return target;
  }
}

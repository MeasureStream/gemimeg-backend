/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dto.ConditionDto;
import de.ptb.common.dcc.xjc.generated.ConditionType;
import de.ptb.common.dcc.xjc.generated.HashType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Component
public class ConditionMapper implements JaxbDtoBidirectionalMapper<ConditionType, ConditionDto> {

  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final RichContentMapper richContentMapper;
  private final DataMapper dataMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public ConditionMapper(LanguageSpecificStringsMapper languageSpecificStringsMapper, RichContentMapper richContentMapper,
                         DataMapper dataMapper, ObjectFactory objectFactory) {
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.richContentMapper = richContentMapper;
    this.dataMapper = dataMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public ConditionDto mapToDto(@Nonnull ConditionType jaxbObject) {
    ConditionDto target = new ConditionDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    target.setStatus(jaxbObject.getStatus());
    if (jaxbObject.getName() != null) {
      target.setName(languageSpecificStringsMapper.mapToDto(jaxbObject.getName()));
    }
    if (jaxbObject.getDescription() != null) {
      target.setDescription(richContentMapper.mapToDto(jaxbObject.getDescription()));
    }
    if (jaxbObject.getData() != null && !jaxbObject.getData().getTextOrFormulaOrByteData().isEmpty()) {
      target.setData(dataMapper.mapToDto(jaxbObject.getData()));
    }
    if (jaxbObject.getCertificate() != null) {
      target.setCertificateId(jaxbObject.getCertificate().getId());
      if (jaxbObject.getCertificate().getReferral() != null) {
        target.setCertificateReferral(languageSpecificStringsMapper.mapToDto(jaxbObject.getCertificate().getReferral()));
      }
      target.setReferralId(jaxbObject.getCertificate().getReferralID());
      target.setCertificateProcedure(jaxbObject.getCertificate().getProcedure());
      target.setCertificateValue(jaxbObject.getCertificate().getValue());
    }
    return target;
  }

  @Override
  @Nonnull
  public ConditionType mapToJaxbObject(@Nonnull ConditionDto dto) {
    ConditionType target = objectFactory.createConditionType();
    if (StringUtils.isNotBlank(dto.getId())) {
      target.setId(dto.getId());
    }
    if (isNotEmpty(dto.getRefIds())) {
      target.getRefId().addAll(dto.getRefIds());
    }
    if (isNotEmpty(dto.getRefTypes())) {
      target.getRefType().addAll(dto.getRefTypes());
    }
    target.setStatus(dto.getStatus());
    if (isNotEmpty(dto.getName())) {
      target.setName(languageSpecificStringsMapper.mapToJaxbObject(dto.getName()));
    }
    if (isNotEmpty(dto.getDescription())) {
      target.setDescription(richContentMapper.mapToJaxbObject(dto.getDescription()));
    }
    if (dto.getData() != null && !dto.getData().isEmpty()) {
      target.setData(dataMapper.mapToJaxbObject(dto.getData()));
    }
    if (StringUtils.isNotBlank(dto.getCertificateValue()) || StringUtils.isNotBlank(dto.getCertificateId())) {
      HashType certificate = new HashType();
      if (StringUtils.isNotBlank(dto.getCertificateId())) {
        certificate.setId(dto.getCertificateId());
      }
      if (isNotEmpty(dto.getCertificateReferral())) {
        certificate.setReferral(languageSpecificStringsMapper.mapToJaxbObject(dto.getCertificateReferral()));
      }
      if (StringUtils.isNotBlank(dto.getReferralId())) {
        certificate.setReferralID(dto.getReferralId());
      }
      if (StringUtils.isNotBlank(dto.getCertificateProcedure())) {
        certificate.setProcedure(dto.getCertificateProcedure());
      }
      if (StringUtils.isNotBlank(dto.getCertificateValue())) {
        certificate.setValue(dto.getCertificateValue());
      }
      target.setCertificate(certificate);
    }
    return target;
  }
}

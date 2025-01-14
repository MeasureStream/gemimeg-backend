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
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;
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

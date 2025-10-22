package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.ResponsiblePersonDto;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.RespPersonType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Component
public class ResponsiblePersonMapper implements JaxbDtoBidirectionalMapper<RespPersonType, ResponsiblePersonDto> {

  private final ContactNotStrictMapper contactNotStrictMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public ResponsiblePersonMapper(ContactNotStrictMapper contactNotStrictMapper, ObjectFactory objectFactory) {
    this.contactNotStrictMapper = contactNotStrictMapper;
    this.objectFactory = objectFactory;
  }
  @Override
  public ResponsiblePersonDto mapToDto(RespPersonType jaxbObject) {
    ResponsiblePersonDto target = new ResponsiblePersonDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (jaxbObject.getPerson() != null) {
      target.setContact(contactNotStrictMapper.mapToDto(jaxbObject.getPerson()));
    }
    if (StringUtils.isNotBlank(jaxbObject.getRole())) {
      target.setRole(jaxbObject.getRole());
    }
    if (jaxbObject.isMainSigner() != null) {
      target.setMainSigner(jaxbObject.isMainSigner());
    }
    if (jaxbObject.isCryptElectronicSeal() != null) {
      target.setMainSigner(jaxbObject.isCryptElectronicSeal());
    }
    if (jaxbObject.isCryptElectronicSignature() != null) {
      target.setMainSigner(jaxbObject.isCryptElectronicSignature());
    }
    if (jaxbObject.isCryptElectronicTimeStamp() != null) {
      target.setMainSigner(jaxbObject.isCryptElectronicTimeStamp());
    }
    return target;
  }

  @Override
  public RespPersonType mapToJaxbObject(ResponsiblePersonDto dto) {
    RespPersonType target = objectFactory.createRespPersonType();
    if (StringUtils.isNotBlank(dto.getId())) {
      target.setId(dto.getId());
    }
    if (isNotEmpty(dto.getRefIds())) {
      target.getRefId().addAll(dto.getRefIds());
    }
    if (isNotEmpty(dto.getRefTypes())) {
      target.getRefType().addAll(dto.getRefTypes());
    }
    if (isNotEmpty(dto.getContact())) {
      target.setPerson(contactNotStrictMapper.mapToJaxbObject(dto.getContact()));
    }
    if (StringUtils.isNotBlank(dto.getRole())) {
      target.setRole(dto.getRole());
    }
    if (dto.getMainSigner() != null) {
      target.setMainSigner(dto.getMainSigner());
    }
    if (dto.getCryptElectronicSeal() != null) {
      target.setMainSigner(dto.getCryptElectronicSeal());
    }
    if (dto.getCryptElectronicSignature() != null) {
      target.setMainSigner(dto.getCryptElectronicSignature());
    }
    if (dto.getCryptElectronicTimeStamp() != null) {
      target.setMainSigner(dto.getCryptElectronicTimeStamp());
    }
    return target;
  }
}

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

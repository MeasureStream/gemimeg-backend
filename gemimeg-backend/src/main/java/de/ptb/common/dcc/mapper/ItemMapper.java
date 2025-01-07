package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dto.IdentificationListDto;
import de.ptb.common.dcc.api.v1.dto.ItemDto;
import de.ptb.common.dcc.api.v1.dto.SoftwareListDto;
import de.ptb.common.dcc.xjc.generated.EquipmentClassType;
import de.ptb.common.dcc.xjc.generated.IdentificationListType;
import de.ptb.common.dcc.xjc.generated.ItemType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.SoftwareListType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Component
public class ItemMapper implements JaxbDtoBidirectionalMapper<ItemType, ItemDto> {

  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final RichContentMapper richContentMapper;
  private final SoftwareMapper softwareMapper;
  private final ContactNotStrictMapper contactMapper;
  private final IdentificationMapper identificationMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public ItemMapper(LanguageSpecificStringsMapper languageSpecificStringsMapper, RichContentMapper richContentMapper,
                    SoftwareMapper softwareMapper, ContactNotStrictMapper contactMapper,
                    IdentificationMapper identificationMapper, ObjectFactory objectFactory) {
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.richContentMapper = richContentMapper;
    this.softwareMapper = softwareMapper;
    this.contactMapper = contactMapper;
    this.identificationMapper = identificationMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  public ItemDto mapToDto(ItemType jaxbObject) {
    ItemDto target = new ItemDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (jaxbObject.getEquipmentClass() != null && !jaxbObject.getEquipmentClass().isEmpty()) {
      target.setClassId(jaxbObject.getEquipmentClass().getFirst().getClassID());
      target.setClassReference(jaxbObject.getEquipmentClass().getFirst().getReference());
    }
    if (jaxbObject.getName() != null) {
      target.setName(languageSpecificStringsMapper.mapToDto(jaxbObject.getName()));
    }
    target.setModel(jaxbObject.getModel());
    if (jaxbObject.getDescription() != null) {
      target.setDescription(richContentMapper.mapToDto(jaxbObject.getDescription()));
    }
    if (jaxbObject.getInstalledSoftwares() != null && !jaxbObject.getInstalledSoftwares().getSoftware().isEmpty()) {
      SoftwareListDto softwareList = new SoftwareListDto();
      softwareList.addAll(jaxbObject.getInstalledSoftwares().getSoftware().stream()
          .map(softwareMapper::mapToDto)
          .toList());
      target.setInstalledSoftwares(softwareList);
    }
    if (jaxbObject.getManufacturer() != null) {
      target.setManufacturer(contactMapper.mapToDto(jaxbObject.getManufacturer()));
    }
    if (jaxbObject.getIdentifications() != null && !jaxbObject.getIdentifications().getIdentification().isEmpty()) {
      IdentificationListDto identificationList = new IdentificationListDto();
      identificationList.addAll(jaxbObject.getIdentifications().getIdentification().stream()
          .map(identificationMapper::mapToDto)
          .toList());
      target.setIdentifications(identificationList);
    }
    return target;
  }

  @Override
  public ItemType mapToJaxbObject(ItemDto dto) {
    ItemType target = objectFactory.createItemType();
    if (StringUtils.isNotBlank(dto.getId())) {
      target.setId(dto.getId());
    }
    if (isNotEmpty(dto.getRefIds())) {
      target.getRefId().addAll(dto.getRefIds());
    }
    if (isNotEmpty(dto.getRefTypes())) {
      target.getRefType().addAll(dto.getRefTypes());
    }
    if (StringUtils.isNotBlank(dto.getClassId()) || StringUtils.isNotBlank(dto.getClassReference())) {
      EquipmentClassType equipmentClass = objectFactory.createEquipmentClassType();
      equipmentClass.setClassID(dto.getClassId());
      equipmentClass.setReference(dto.getClassReference());
      target.getEquipmentClass().add(equipmentClass);
    }
    if (isNotEmpty(dto.getName())) {
      target.setName(languageSpecificStringsMapper.mapToJaxbObject(dto.getName()));
    }
    target.setModel(dto.getModel());
    if (isNotEmpty(dto.getDescription())) {
      target.setDescription(richContentMapper.mapToJaxbObject(dto.getDescription()));
    }
    if (dto.getInstalledSoftwares() != null && !dto.getInstalledSoftwares().isEmpty()) {
      SoftwareListType softwareList = objectFactory.createSoftwareListType();
      softwareList.getSoftware().addAll(dto.getInstalledSoftwares().stream()
          .filter(software -> isNotEmpty(software.getName()))
          .map(softwareMapper::mapToJaxbObject)
          .toList());
      if (!softwareList.getSoftware().isEmpty()) {
        target.setInstalledSoftwares(softwareList);
      }
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
    return target;
  }
}

package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.IdentificationListDto;
import de.ptb.common.dcc.api.v1.dcc.ItemListDto;
import de.ptb.common.dcc.xjc.generated.EquipmentClassType;
import de.ptb.common.dcc.xjc.generated.IdentificationListType;
import de.ptb.common.dcc.xjc.generated.ItemListType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;

@Component
public class ItemListMapper implements JaxbDtoBidirectionalMapper<ItemListType, ItemListDto> {

  private final ItemMapper itemMapper;
  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final RichContentMapper richContentMapper;
  private final ContactMapper contactMapper;
  private final IdentificationMapper identificationMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public ItemListMapper(ItemMapper itemMapper, LanguageSpecificStringsMapper languageSpecificStringsMapper,
                        RichContentMapper richContentMapper, ContactMapper contactMapper,
                        IdentificationMapper identificationMapper, ObjectFactory objectFactory) {
    this.itemMapper = itemMapper;
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.richContentMapper = richContentMapper;
    this.contactMapper = contactMapper;
    this.identificationMapper = identificationMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  public ItemListDto mapToDto(ItemListType jaxbObject) {
    ItemListDto target = new ItemListDto();
    if (jaxbObject.getItem() != null && !jaxbObject.getItem().isEmpty()) {
      target.addAll(jaxbObject.getItem().stream().map(itemMapper::mapToDto).toList());
    }
    if (jaxbObject.getEquipmentClass() != null && !jaxbObject.getEquipmentClass().isEmpty()) {
      target.setClassId(jaxbObject.getEquipmentClass().getFirst().getClassID());
      target.setClassReference(jaxbObject.getEquipmentClass().getFirst().getReference());
    }
    if (jaxbObject.getName() != null) {
      target.setName(languageSpecificStringsMapper.mapToDto(jaxbObject.getName()));
    }
    if (jaxbObject.getDescription() != null) {
      target.setDescription(richContentMapper.mapToDto(jaxbObject.getDescription()));
    }
    if (jaxbObject.getOwner() != null) {
      target.setOwner(contactMapper.mapToDto(jaxbObject.getOwner()));
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
  public ItemListType mapToJaxbObject(ItemListDto dto) {
    ItemListType target = objectFactory.createItemListType();
    if (!dto.isEmpty()) {
      target.getItem().addAll(dto.stream()
          .map(itemMapper::mapToJaxbObject)
          .toList());
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
    if (isNotEmpty(dto.getDescription())) {
      target.setDescription(richContentMapper.mapToJaxbObject(dto.getDescription()));
    }
    if (isNotEmpty(dto.getOwner())) {
      target.setOwner(contactMapper.mapToJaxbObject(dto.getOwner()));
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

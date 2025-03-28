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

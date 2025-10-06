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

import de.ptb.common.dcc.api.v1.dcc.AdministrativeDataDto;
import de.ptb.common.dcc.api.v1.dcc.ContactListDto;
import de.ptb.common.dcc.api.v1.dcc.IdentificationListDto;
import de.ptb.common.dcc.api.v1.dcc.SoftwareListDto;
import de.ptb.common.dcc.api.v1.dcc.StatementListDto;
import de.ptb.common.dcc.util.DccServiceUtil;
import de.ptb.common.dcc.xjc.generated.AdministrativeDataType;
import de.ptb.common.dcc.xjc.generated.CoreDataType;
import de.ptb.common.dcc.xjc.generated.IdentificationListType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.PerformanceLocationType;
import de.ptb.common.dcc.xjc.generated.RespPersonListType;
import de.ptb.common.dcc.xjc.generated.RespPersonType;
import de.ptb.common.dcc.xjc.generated.SoftwareListType;
import de.ptb.common.dcc.xjc.generated.StatementListType;
import de.ptb.common.dcc.xjc.generated.StringPerformanceLocationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

import static de.ptb.common.dcc.util.DccServiceUtil.enumValue;
import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;

@Slf4j
@Component
public class AdministrativeDataMapper implements JaxbDtoBidirectionalMapper<AdministrativeDataType, AdministrativeDataDto> {

  private final SoftwareMapper softwareMapper;
  private final ContactMapper contactMapper;
  private final ContactNotStrictMapper contactNotStrictMapper;
  private final CalibrationLaboratoryMapper calibrationLaboratoryMapper;
  private final ItemListMapper itemListMapper;
  private final StatementMapper statementMapper;
  private final IdentificationMapper identificationMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public AdministrativeDataMapper(SoftwareMapper softwareMapper, ContactMapper contactMapper,
                                  ContactNotStrictMapper contactNotStrictMapper,
                                  CalibrationLaboratoryMapper calibrationLaboratoryMapper,
                                  ItemListMapper itemListMapper, StatementMapper statementMapper,
                                  IdentificationMapper identificationMapper,
                                  ObjectFactory objectFactory) {
    this.softwareMapper = softwareMapper;
    this.contactMapper = contactMapper;
    this.contactNotStrictMapper = contactNotStrictMapper;
    this.calibrationLaboratoryMapper = calibrationLaboratoryMapper;
    this.itemListMapper = itemListMapper;
    this.statementMapper = statementMapper;
    this.identificationMapper = identificationMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public AdministrativeDataDto mapToDto(@Nonnull AdministrativeDataType jaxbObject) {
    AdministrativeDataDto target = new AdministrativeDataDto();
    if (jaxbObject.getDccSoftware() != null) {
      SoftwareListDto dccSoftware = new SoftwareListDto();
      dccSoftware.addAll(jaxbObject.getDccSoftware().getSoftware().stream()
          .map(softwareMapper::mapToDto)
          .toList());
      target.setDccSoftware(dccSoftware);
    }
    if (jaxbObject.getCustomer() != null) {
      target.setCustomer(contactMapper.mapToDto(jaxbObject.getCustomer()));
    }
    if (jaxbObject.getCalibrationLaboratory() != null) {
      target.setCalibrationLaboratory(calibrationLaboratoryMapper.mapToDto(jaxbObject.getCalibrationLaboratory()));
    }
    if (jaxbObject.getItems() != null) {
      target.setItems(itemListMapper.mapToDto(jaxbObject.getItems()));
    }
    if (jaxbObject.getCoreData() != null) {
      CoreDataType coreData = jaxbObject.getCoreData();
      target.setUniqueIdentifier(coreData.getUniqueIdentifier());
      target.setCountryCode(coreData.getCountryCodeISO31661());
      if (isNotEmpty(coreData.getMandatoryLangCodeISO6391())) {
        Set<String> mandatoryLanguages = new HashSet<>(coreData.getMandatoryLangCodeISO6391());
        target.setMandatoryLanguageCodes(mandatoryLanguages);
      }
      if (isNotEmpty(coreData.getUsedLangCodeISO6391())) {
        Set<String> usedLanguages = new HashSet<>(coreData.getUsedLangCodeISO6391());
        target.setUsedLanguageCodes(usedLanguages);
      }
      if (coreData.getReceiptDate() != null) {
        target.setReceiptDate(convertDate(coreData.getReceiptDate()));
      }
      if (coreData.getBeginPerformanceDate() != null) {
        target.setStartDate(convertDate(coreData.getBeginPerformanceDate()));
      }
      if (coreData.getEndPerformanceDate() != null) {
        target.setEndDate(convertDate(coreData.getEndPerformanceDate()));
      }
      if (coreData.getPerformanceLocation() != null && coreData.getPerformanceLocation().getValue() != null &&
          StringUtils.isNotBlank(coreData.getPerformanceLocation().getValue().value())) {
        target.setPerformanceLocation(coreData.getPerformanceLocation().getValue().value());
      }
      if (coreData.getIdentifications() != null &&
          coreData.getIdentifications().getIdentification() != null &&
          !coreData.getIdentifications().getIdentification().isEmpty()) {
        IdentificationListDto identificationList = new IdentificationListDto();
        identificationList.addAll(coreData.getIdentifications().getIdentification().stream()
            .map(identificationMapper::mapToDto)
            .toList());
        target.setIdentifications(identificationList);
      }
    }
    if (jaxbObject.getRespPersons() != null && !jaxbObject.getRespPersons().getRespPerson().isEmpty()) {
      ContactListDto responsiblePersons = new ContactListDto();
      responsiblePersons.addAll(jaxbObject.getRespPersons().getRespPerson().stream()
          .map(RespPersonType::getPerson)
          .map(contactNotStrictMapper::mapToDto)
          .toList());
      target.setResponsiblePersons(responsiblePersons);
    }
    if (jaxbObject.getStatements() != null) {
      StatementListDto statements = new StatementListDto();
      statements.addAll(jaxbObject.getStatements().getStatement().stream()
          .map(statementMapper::mapToDto)
          .toList());
      target.setStatements(statements);
    }
    return target;
  }

  @Override
  @Nonnull
  public AdministrativeDataType mapToJaxbObject(@Nonnull AdministrativeDataDto dto) {
    AdministrativeDataType target = objectFactory.createAdministrativeDataType();
    if (dto.getDccSoftware() != null) {
      SoftwareListType softwareList = objectFactory.createSoftwareListType();
      softwareList.getSoftware().addAll(dto.getDccSoftware().stream()
          .filter(software -> isNotEmpty(software.getName()))
          .map(softwareMapper::mapToJaxbObject)
          .toList());
      target.setDccSoftware(softwareList);
    }
    if (isNotEmpty(dto.getCustomer())) {
      target.setCustomer(contactMapper.mapToJaxbObject(dto.getCustomer()));
    }
    if (dto.getCalibrationLaboratory() != null) {
      target.setCalibrationLaboratory(calibrationLaboratoryMapper.mapToJaxbObject(dto.getCalibrationLaboratory()));
    }
    if (dto.getItems() != null) {
      target.setItems(itemListMapper.mapToJaxbObject(dto.getItems()));
    }
    CoreDataType coreData = objectFactory.createCoreDataType();
    coreData.setUniqueIdentifier(dto.getUniqueIdentifier());
    coreData.setCountryCodeISO31661(dto.getCountryCode());
    if (isNotEmpty(dto.getMandatoryLanguageCodes())) {
      coreData.getMandatoryLangCodeISO6391().addAll(dto.getMandatoryLanguageCodes());
    }
    if (isNotEmpty(dto.getUsedLanguageCodes())) {
      coreData.getUsedLangCodeISO6391().addAll(dto.getUsedLanguageCodes());
    }
    if (dto.getReceiptDate() != null) {
      coreData.setReceiptDate(convertDate(dto.getReceiptDate()));
    }
    if (dto.getStartDate() != null) {
      coreData.setBeginPerformanceDate(convertDate(dto.getStartDate()));
    }
    if (dto.getEndDate() != null) {
      coreData.setEndPerformanceDate(convertDate(dto.getEndDate()));
    }
    PerformanceLocationType performanceLocation = objectFactory.createPerformanceLocationType();
    if (StringUtils.isNotBlank(dto.getPerformanceLocation())) {
      try {
        performanceLocation.setValue(StringPerformanceLocationType.valueOf(enumValue(dto.getPerformanceLocation())));
      } catch (Throwable t) {
        log.info("'" + dto.getPerformanceLocation() + "' is not permitted / not known. Using '" +
            StringPerformanceLocationType.LABORATORY.value() + "' instead.");
        log.warn(t.getMessage());
        performanceLocation.setValue(StringPerformanceLocationType.LABORATORY);
      }
    } else {
      performanceLocation.setValue(StringPerformanceLocationType.LABORATORY);
    }
    coreData.setPerformanceLocation(performanceLocation);
    if (dto.getIdentifications() != null && !dto.getIdentifications().isEmpty()) {
      if (coreData.getIdentifications() == null) {
        coreData.setIdentifications(objectFactory.createIdentificationListType());
      }
      coreData.getIdentifications().getIdentification().addAll(
          dto.getIdentifications().stream()
              .map(identificationMapper::mapToJaxbObject)
              .toList());
    }
    target.setCoreData(coreData);
    RespPersonListType respPersonList = objectFactory.createRespPersonListType();
    if (dto.getResponsiblePersons() != null) {
      dto.getResponsiblePersons().stream()
          .filter(DccServiceUtil::isNotEmpty)
          .map(contactNotStrictMapper::mapToJaxbObject)
          .forEach(person -> {
            RespPersonType respPerson = objectFactory.createRespPersonType();
            respPerson.setPerson(person);
            respPersonList.getRespPerson().add(respPerson);
          });
    }
    target.setRespPersons(respPersonList);
    if (dto.getStatements() != null) {
      StatementListType statementList = objectFactory.createStatementListType();
      statementList.getStatement().addAll(dto.getStatements().stream()
          .filter(DccServiceUtil::isNotEmpty)
          .map(statementMapper::mapToJaxbObject)
          .toList());
      if (!statementList.getStatement().isEmpty()) {
        target.setStatements(statementList);
      }
    }
    return target;
  }
}

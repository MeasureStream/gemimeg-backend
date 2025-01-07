package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dto.AdministrativeDataDto;
import de.ptb.common.dcc.api.v1.dto.ContactListDto;
import de.ptb.common.dcc.api.v1.dto.SoftwareListDto;
import de.ptb.common.dcc.api.v1.dto.StatementListDto;
import de.ptb.common.dcc.util.DccServiceUtil;
import de.ptb.common.dcc.xjc.generated.AdministrativeDataType;
import de.ptb.common.dcc.xjc.generated.CoreDataType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.PerformanceLocationType;
import de.ptb.common.dcc.xjc.generated.RespPersonListType;
import de.ptb.common.dcc.xjc.generated.RespPersonType;
import de.ptb.common.dcc.xjc.generated.SoftwareListType;
import de.ptb.common.dcc.xjc.generated.StatementListType;
import de.ptb.common.dcc.xjc.generated.StringPerformanceLocationType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

import static de.ptb.common.dcc.util.DccServiceUtil.enumValue;
import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;

@Component
public class AdministrativeDataMapper implements JaxbDtoBidirectionalMapper<AdministrativeDataType, AdministrativeDataDto> {

  private final SoftwareMapper softwareMapper;
  private final ContactMapper contactMapper;
  private final ContactNotStrictMapper contactNotStrictMapper;
  private final CalibrationLaboratoryMapper calibrationLaboratoryMapper;
  private final ItemListMapper itemListMapper;
  private final StatementMapper statementMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public AdministrativeDataMapper(SoftwareMapper softwareMapper, ContactMapper contactMapper,
                                  ContactNotStrictMapper contactNotStrictMapper,
                                  CalibrationLaboratoryMapper calibrationLaboratoryMapper,
                                  ItemListMapper itemListMapper, StatementMapper statementMapper,
                                  ObjectFactory objectFactory) {
    this.softwareMapper = softwareMapper;
    this.contactMapper = contactMapper;
    this.contactNotStrictMapper = contactNotStrictMapper;
    this.calibrationLaboratoryMapper = calibrationLaboratoryMapper;
    this.itemListMapper = itemListMapper;
    this.statementMapper = statementMapper;
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
      Set<String> languages = new HashSet<>(coreData.getMandatoryLangCodeISO6391());
      languages.addAll(coreData.getUsedLangCodeISO6391());
      target.setLanguageCodes(languages);
      if (coreData.getReceiptDate() != null) {
        target.setReceiptDate(convertDate(coreData.getReceiptDate()));
      }
      if (coreData.getBeginPerformanceDate() != null) {
        target.setStartDate(convertDate(coreData.getBeginPerformanceDate()));
      }
      if (coreData.getEndPerformanceDate() != null) {
        target.setEndDate(convertDate(coreData.getEndPerformanceDate()));
      }
      if (coreData.getPerformanceLocation() != null) {
        target.setPerformanceLocation(coreData.getPerformanceLocation().getValue().value());
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
    if (isNotEmpty(dto.getLanguageCodes())) {
      coreData.getMandatoryLangCodeISO6391().addAll(dto.getLanguageCodes());
      coreData.getUsedLangCodeISO6391().addAll(dto.getLanguageCodes());
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
      performanceLocation.setValue(StringPerformanceLocationType.valueOf(enumValue(dto.getPerformanceLocation())));
    } else {
      performanceLocation.setValue(StringPerformanceLocationType.LABORATORY);
    }
    coreData.setPerformanceLocation(performanceLocation);
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

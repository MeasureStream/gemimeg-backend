package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dto.StatementDto;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.StatementMetaDataType;
import de.ptb.common.dcc.xjc.generated.StringConformityStatementStatusType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Slf4j
@Component
public class StatementMapper implements JaxbDtoBidirectionalMapper<StatementMetaDataType, StatementDto> {

  private final LocationMapper locationMapper;
  private final RichContentMapper richContentMapper;
  private final DataMapper dataMapper;
  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final ContactMapper contactMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public StatementMapper(LocationMapper locationMapper, RichContentMapper richContentMapper,
                         DataMapper dataMapper, LanguageSpecificStringsMapper languageSpecificStringsMapper,
                         ContactMapper contactMapper, ObjectFactory objectFactory) {
    this.locationMapper = locationMapper;
    this.richContentMapper = richContentMapper;
    this.dataMapper = dataMapper;
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.contactMapper = contactMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public StatementDto mapToDto(@Nonnull StatementMetaDataType jaxbObject) {
    StatementDto target = new StatementDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (jaxbObject.getDate() != null) {
      target.setDate(convertDate(jaxbObject.getDate()));
    }
    if (jaxbObject.getName() != null) {
      target.setName(languageSpecificStringsMapper.mapToDto(jaxbObject.getName()));
    }
    if (jaxbObject.getCountryCodeISO31661() != null && !jaxbObject.getCountryCodeISO31661().isEmpty()) {
      target.setCountryCodes(jaxbObject.getCountryCodeISO31661());
    }
    if (jaxbObject.getDescription() != null) {
      target.setDescription(richContentMapper.mapToDto(jaxbObject.getDescription()));
    }
    target.setNorms(jaxbObject.getNorm());
    if (jaxbObject.getDeclaration() != null) {
      target.setDeclaration(richContentMapper.mapToDto(jaxbObject.getDeclaration()));
    }
    target.setConvention(jaxbObject.getConvention());
    target.setTraceable(jaxbObject.isTraceable());
    target.setNonSIDefinition(jaxbObject.getNonSIDefinition());
    target.setNonSIUnit(jaxbObject.getNonSIUnit());
    if (jaxbObject.getLocation() != null) {
      target.setLocation(locationMapper.mapToDto(jaxbObject.getLocation()));
    }
    if (jaxbObject.getData() != null) {
      target.setData(dataMapper.mapToDto(jaxbObject.getData()));
    }
    if (jaxbObject.getConformity() != null) {
      target.setConformity(jaxbObject.getConformity().value());
    }
    if (jaxbObject.getConformityXMLList() != null && !jaxbObject.getConformityXMLList().isEmpty()) {
      target.setConformityXMLList(jaxbObject.getConformityXMLList().stream()
          .map(StringConformityStatementStatusType::value)
          .toList());
    }
    if (jaxbObject.getRespAuthority() != null) {
      target.setResponsibleAuthority(contactMapper.mapToDto(jaxbObject.getRespAuthority()));
    }
    if (jaxbObject.getPeriod() != null) {
      target.setPeriod(jaxbObject.getPeriod().toString());
    }
    if (jaxbObject.isValid() != null) {
      target.setValid(jaxbObject.isValid());
    }
    if (jaxbObject.getValidXMLList() != null && !jaxbObject.getValidXMLList().isEmpty()) {
      target.setValidXMLList(jaxbObject.getValidXMLList());
    }
    return target;
  }

  @Override
  @Nonnull
  public StatementMetaDataType mapToJaxbObject(@Nonnull StatementDto dto) {
    StatementMetaDataType target = objectFactory.createStatementMetaDataType();
    if (StringUtils.isNotBlank(dto.getId())) {
      target.setId(dto.getId());
    }
    if (isNotEmpty(dto.getRefIds())) {
      target.getRefId().addAll(dto.getRefIds());
    }
    if (isNotEmpty(dto.getRefTypes())) {
      target.getRefType().addAll(dto.getRefTypes());
    }
    if (dto.getDate() != null) {
      target.setDate(convertDate(dto.getDate()));
    }
    if (isNotEmpty(dto.getName())) {
      target.setName(languageSpecificStringsMapper.mapToJaxbObject(dto.getName()));
    }
    if (isNotEmpty(dto.getCountryCodes())) {
      target.getCountryCodeISO31661().addAll(dto.getCountryCodes());
    }
    if (isNotEmpty(dto.getDescription())) {
      target.setDescription(richContentMapper.mapToJaxbObject(dto.getDescription()));
    }
    if (isNotEmpty(dto.getNorms())) {
      target.getNorm().addAll(dto.getNorms());
    }
    if (isNotEmpty(dto.getDeclaration())) {
      target.setDeclaration(richContentMapper.mapToJaxbObject(dto.getDeclaration()));
    }
    target.setConvention(dto.getConvention());
    target.setTraceable(dto.getTraceable());
    target.setNonSIDefinition(dto.getNonSIDefinition());
    target.setNonSIUnit(dto.getNonSIUnit());
    if (isNotEmpty(dto.getLocation())) {
      target.setLocation(locationMapper.mapToJaxbObject(dto.getLocation()));
    }
    if (dto.getData() != null && !dto.getData().isEmpty()) {
      target.setData(dataMapper.mapToJaxbObject(dto.getData()));
    }
    if (StringUtils.isNotBlank(dto.getConformity())) {
      target.setConformity(StringConformityStatementStatusType.fromValue(dto.getConformity()));
    }
    if (dto.getConformityXMLList() != null && !dto.getConformityXMLList().isEmpty()) {
      target.getConformityXMLList().addAll(dto.getConformityXMLList().stream()
          .map(StringConformityStatementStatusType::fromValue)
          .toList());
    }
    if (isNotEmpty(dto.getResponsibleAuthority())) {
      target.setRespAuthority(contactMapper.mapToJaxbObject(dto.getResponsibleAuthority()));
    }
    if (StringUtils.isNotBlank(dto.getPeriod())) {
      try {
        target.setPeriod(DatatypeFactory.newInstance().newDuration(dto.getPeriod()));
      } catch (DatatypeConfigurationException | IllegalArgumentException e) {
        log.warn("Unable to convert period (time interval): {}", e.getMessage());
      }
    }
    if (dto.getValid() != null) {
      target.setValid(dto.getValid());
    }
    if (dto.getValidXMLList() != null && !dto.getValidXMLList().isEmpty()) {
      target.getValidXMLList().addAll(dto.getValidXMLList());
    }
    return target;
  }
}

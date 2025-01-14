package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.DimensionDto;
import de.ptb.common.dcc.api.v1.dcc.MethodListDto;
import de.ptb.common.dcc.api.v1.dcc.QuantityDto;
import de.ptb.common.dcc.api.v1.dcc.UncertaintyDto;
import de.ptb.common.dcc.api.v1.dcc.XmlValuesDto;
import de.ptb.common.dcc.xjc.generated.ConstantQuantityType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.QuantityType;
import de.ptb.common.dcc.xjc.generated.RealListXMLListType;
import de.ptb.common.dcc.xjc.generated.RealQuantityType;
import de.ptb.common.dcc.xjc.generated.UsedMethodListType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static de.ptb.common.dcc.util.DccServiceUtil.createDimension;
import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.isValid;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Slf4j
@Component
public class QuantityMapper implements JaxbDtoBidirectionalMapper<QuantityType, QuantityDto> {

  private final static String REAL = "real";
  private final static String CONSTANT = "constant";
  private final static String HYBRID = "hybrid";
  private static final String REAL_LIST_XML_LIST = "realListXMLList";

  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final RichContentMapper richContentMapper;
  private final HybridValuesMapper hybridValuesMapper;
  private final MethodMapper methodMapper;
  private final UncertaintyMapper uncertaintyMapper;
  private final CoverageIntervalMapper coverageIntervalMapper;
  private final CoverageIntervalXmlListMapper coverageIntervalXmlListMapper;
  private final UncertaintyXmlListMapper uncertaintyXmlListMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public QuantityMapper(LanguageSpecificStringsMapper languageSpecificStringsMapper, RichContentMapper richContentMapper,
                        HybridValuesMapper hybridValuesMapper, MethodMapper methodMapper,
                        UncertaintyMapper uncertaintyMapper, CoverageIntervalMapper coverageIntervalMapper,
                        CoverageIntervalXmlListMapper coverageIntervalXmlListMapper,
                        UncertaintyXmlListMapper uncertaintyXmlListMapper, ObjectFactory objectFactory) {
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.richContentMapper = richContentMapper;
    this.hybridValuesMapper = hybridValuesMapper;
    this.methodMapper = methodMapper;
    this.uncertaintyMapper = uncertaintyMapper;
    this.coverageIntervalMapper = coverageIntervalMapper;
    this.coverageIntervalXmlListMapper = coverageIntervalXmlListMapper;
    this.uncertaintyXmlListMapper = uncertaintyXmlListMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public QuantityDto mapToDto(@Nonnull QuantityType jaxbObject) {
    QuantityDto target = new QuantityDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (jaxbObject.getName() != null && !jaxbObject.getName().getContent().isEmpty()) {
      target.setName(languageSpecificStringsMapper.mapToDto(jaxbObject.getName()));
    }
    if (jaxbObject.getReal() != null) {
      RealQuantityType realQuantity = jaxbObject.getReal();
      target.setQuantityTypeName(REAL);
      target.setDimension(createDimension(realQuantity.getValue(), realQuantity.getUnit()));
      target.setLabel(realQuantity.getLabel());
      if (realQuantity.getDateTime() != null) {
        target.setTimestamp(realQuantity.getDateTime().toGregorianCalendar().toZonedDateTime().toLocalDateTime());
      }
      if (realQuantity.getExpandedUnc() != null && isValid(realQuantity.getExpandedUnc())) {
        target.setUncertainty(uncertaintyMapper.mapToDto(realQuantity.getExpandedUnc()));
      }
      if (realQuantity.getCoverageInterval() != null && isValid(realQuantity.getCoverageInterval())) {
        target.setCoverageInterval(coverageIntervalMapper.mapToDto(realQuantity.getCoverageInterval()));
      }
    } else if (jaxbObject.getConstant() != null) {
      ConstantQuantityType constantQuantity = jaxbObject.getConstant();
      target.setQuantityTypeName(CONSTANT);
      target.setDimension(createDimension(constantQuantity.getValue(), constantQuantity.getUnit()));
      target.setLabel(constantQuantity.getLabel());
      if (constantQuantity.getDateTime() != null) {
        target.setTimestamp(constantQuantity.getDateTime().toGregorianCalendar().toZonedDateTime().toLocalDateTime());
      }
      if (constantQuantity.getUncertainty() != null || StringUtils.isNotBlank(constantQuantity.getDistribution())) {
        UncertaintyDto uncertainty = new UncertaintyDto();
        uncertainty.setUncertainty(constantQuantity.getUncertainty());
        uncertainty.setDistribution(constantQuantity.getDistribution());
        target.setUncertainty(uncertainty);
      }
    }
    if (jaxbObject.getRealListXMLList() != null) {
      target.setQuantityTypeName(REAL_LIST_XML_LIST);
      XmlValuesDto xmlValues = new XmlValuesDto();
      xmlValues.setValues(new ArrayList<>());
      List<Double> sourceValues = jaxbObject.getRealListXMLList().getValueXMLList();
      List<String> sourceUnits = jaxbObject.getRealListXMLList().getUnitXMLList();
      for (int i = 0; i < sourceValues.size(); i++) {
        xmlValues.getValues().add(createDimension(sourceValues.get(i),
            sourceUnits.size() == 1 ? sourceUnits.getFirst() : sourceUnits.get(i)));
      }
      if (jaxbObject.getRealListXMLList().getCoverageIntervalXMLList() != null) {
        xmlValues.setCoverageIntervals(coverageIntervalXmlListMapper
            .mapToDto(jaxbObject.getRealListXMLList().getCoverageIntervalXMLList()));
      }
      if (jaxbObject.getRealListXMLList().getExpandedUncXMLList() != null) {
        xmlValues.setUncertainties(uncertaintyXmlListMapper
            .mapToDto(jaxbObject.getRealListXMLList().getExpandedUncXMLList()));
      }
      target.setXmlValues(xmlValues);
    }
    if (jaxbObject.getHybrid() != null) {
      target.setQuantityTypeName(HYBRID);
      target.setHybridValues(hybridValuesMapper.mapToDto(jaxbObject.getHybrid()));
    }
    if (jaxbObject.getUsedMethods() != null) {
      UsedMethodListType usedMethods = jaxbObject.getUsedMethods();
      MethodListDto methodListDto = new MethodListDto();
      methodListDto.addAll(usedMethods.getUsedMethod().stream()
          .map(methodMapper::mapToDto)
          .toList());
      target.setUsedMethods(methodListDto);
    }
    if (jaxbObject.getNoQuantity() != null && !jaxbObject.getNoQuantity().getContentAndFileAndFormula().isEmpty()) {
      target.setNoQuantity(richContentMapper.mapToDto(jaxbObject.getNoQuantity()));
    }
    if (jaxbObject.getDescription() != null && !jaxbObject.getDescription().getContentAndFileAndFormula().isEmpty()) {
      target.setDescription(richContentMapper.mapToDto(jaxbObject.getDescription()));
    }
    return target;
  }

  @Override
  public QuantityType mapToJaxbObject(QuantityDto dto) {
    QuantityType target = objectFactory.createQuantityType();
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
    if (StringUtils.equalsIgnoreCase(dto.getQuantityTypeName(), REAL)) {
      RealQuantityType realQuantity = objectFactory.createRealQuantityType();
      realQuantity.setValue(dto.getDimension().getValue().doubleValue());
      realQuantity.setUnit(dto.getDimension().getUnit());
      realQuantity.setLabel(dto.getLabel());
      if (dto.getTimestamp() != null) {
        GregorianCalendar calendar = GregorianCalendar.from(dto.getTimestamp().atZone(ZoneId.systemDefault()));
        try {
          realQuantity.setDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
        } catch (DatatypeConfigurationException e) {
          log.error(e.getMessage());
        }
      }
      if (dto.getUncertainty() != null) {
        realQuantity.setExpandedUnc(uncertaintyMapper.mapToJaxbObject(dto.getUncertainty()));
      }
      if (dto.getCoverageInterval() != null) {
        realQuantity.setCoverageInterval(coverageIntervalMapper.mapToJaxbObject(dto.getCoverageInterval()));
      }
      target.setReal(realQuantity);
    } else if (StringUtils.equalsIgnoreCase(dto.getQuantityTypeName(), CONSTANT)) {
      ConstantQuantityType constantQuantity = objectFactory.createConstantQuantityType();
      constantQuantity.setValue(dto.getDimension().getValue().doubleValue());
      constantQuantity.setUnit(dto.getDimension().getUnit());
      constantQuantity.setLabel(dto.getLabel());
      if (dto.getTimestamp() != null) {
        GregorianCalendar calendar = GregorianCalendar.from(dto.getTimestamp().atZone(ZoneId.systemDefault()));
        try {
          constantQuantity.setDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
        } catch (DatatypeConfigurationException e) {
          log.error(e.getMessage());
        }
      }
      if (dto.getUncertainty() != null) {
        constantQuantity.setUncertainty(dto.getUncertainty().getUncertainty());
        constantQuantity.setDistribution(dto.getUncertainty().getDistribution());
      }
      target.setConstant(constantQuantity);
    } else if (StringUtils.equalsIgnoreCase(dto.getQuantityTypeName(), REAL_LIST_XML_LIST)) {
      XmlValuesDto xmlValues = dto.getXmlValues();
      RealListXMLListType realListXMLList = objectFactory.createRealListXMLListType();
      realListXMLList.getValueXMLList().addAll(xmlValues.getValues().stream()
          .map(DimensionDto::getValue)
          .map(Number::doubleValue)
          .toList());
      realListXMLList.getUnitXMLList().addAll(xmlValues.getValues().stream()
          .map(DimensionDto::getUnit)
          .distinct()
          .toList());
      if (xmlValues.getUncertainties() != null && !xmlValues.getUncertainties().isEmpty()) {
        realListXMLList.setExpandedUncXMLList(uncertaintyXmlListMapper
            .mapToJaxbObject(xmlValues.getUncertainties()));
      }
      if (xmlValues.getCoverageIntervals() != null && !xmlValues.getCoverageIntervals().isEmpty()) {
        realListXMLList.setCoverageIntervalXMLList(coverageIntervalXmlListMapper
            .mapToJaxbObject(xmlValues.getCoverageIntervals()));
      }
      target.setRealListXMLList(realListXMLList);
    } else if (StringUtils.equalsIgnoreCase(dto.getQuantityTypeName(), HYBRID)) {
      if (dto.getHybridValues() != null) {
        target.setHybrid(hybridValuesMapper.mapToJaxbObject(dto.getHybridValues()));
      }
    }
    if (dto.getUsedMethods() != null) {
      UsedMethodListType usedMethods = objectFactory.createUsedMethodListType();
      usedMethods.getUsedMethod().addAll(dto.getUsedMethods().stream()
          .map(methodMapper::mapToJaxbObject)
          .toList());
      target.setUsedMethods(usedMethods);
    }
    if (isNotEmpty(dto.getNoQuantity())) {
      target.setNoQuantity(richContentMapper.mapToJaxbObject(dto.getNoQuantity()));
    }
    if (isNotEmpty(dto.getDescription())) {
      target.setDescription(richContentMapper.mapToJaxbObject(dto.getDescription()));
    }
    return target;
  }
}

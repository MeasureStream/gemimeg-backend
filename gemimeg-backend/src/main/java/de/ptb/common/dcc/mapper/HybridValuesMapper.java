/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.CoverageIntervalDto;
import de.ptb.common.dcc.api.v1.dcc.CoverageIntervalListDto;
import de.ptb.common.dcc.api.v1.dcc.DimensionListDto;
import de.ptb.common.dcc.api.v1.dcc.QuantityDto;
import de.ptb.common.dcc.api.v1.dcc.UncertaintyDto;
import de.ptb.common.dcc.api.v1.dcc.UncertaintyListDto;
import de.ptb.common.dcc.xjc.generated.ConstantQuantityType;
import de.ptb.common.dcc.xjc.generated.HybridType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.RealListXMLListType;
import de.ptb.common.dcc.xjc.generated.RealQuantityType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static de.ptb.common.dcc.util.DccServiceUtil.createDimension;
import static de.ptb.common.dcc.util.DccServiceUtil.isValid;

@Component
public class HybridValuesMapper implements JaxbDtoBidirectionalMapper<HybridType, QuantityDto.HybridValues> {

  private static final String REAL = "real";
  private static final String REAL_LIST_XML_LIST = "realListXMLList";
  private static final String CONSTANT = "constant";

  private final UncertaintyMapper uncertaintyMapper;
  private final CoverageIntervalMapper coverageIntervalMapper;
  private final CoverageIntervalXmlListMapper coverageIntervalXmlListMapper;
  private final UncertaintyXmlListMapper uncertaintyXmlListMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public HybridValuesMapper(UncertaintyMapper uncertaintyMapper, CoverageIntervalMapper coverageIntervalMapper,
                            CoverageIntervalXmlListMapper coverageIntervalXmlListMapper,
                            UncertaintyXmlListMapper uncertaintyXmlListMapper, ObjectFactory objectFactory) {
    this.uncertaintyMapper = uncertaintyMapper;
    this.coverageIntervalMapper = coverageIntervalMapper;
    this.coverageIntervalXmlListMapper = coverageIntervalXmlListMapper;
    this.uncertaintyXmlListMapper = uncertaintyXmlListMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  public QuantityDto.HybridValues mapToDto(HybridType jaxbObject) {
    QuantityDto.HybridValues target = new QuantityDto.HybridValues();
    target.setDimensions(new DimensionListDto());
    target.setQuantitySubTypeNames(new ArrayList<>());
    target.setLabelList(new ArrayList<>());
    if (jaxbObject.getReal() != null && !jaxbObject.getReal().isEmpty()) {
      jaxbObject.getReal().forEach(value -> {
        target.getQuantitySubTypeNames().add(REAL);
        target.getDimensions().add(createDimension(value.getValue(), value.getUnit()));
        if (StringUtils.isNotBlank(value.getLabel())) {
          target.getLabelList().add(value.getLabel());
        }
        if (value.getExpandedUnc() != null && isValid(value.getExpandedUnc())) {
          if (target.getUncertaintyList() == null) {
            target.setUncertaintyList(new UncertaintyListDto());
          }
          target.getUncertaintyList().add(uncertaintyMapper.mapToDto(value.getExpandedUnc()));
        }
        if (value.getCoverageInterval() != null && isValid(value.getCoverageInterval())) {
          if (target.getCoverageIntervalList() == null) {
            target.setCoverageIntervalList(new CoverageIntervalListDto());
          }
          target.getCoverageIntervalList().add(coverageIntervalMapper.mapToDto(value.getCoverageInterval()));
        }
      });
    }
    if (jaxbObject.getConstant() != null && !jaxbObject.getConstant().isEmpty()) {
      jaxbObject.getConstant().forEach(value -> {
        target.getQuantitySubTypeNames().add(CONSTANT);
        target.getDimensions().add(createDimension(value.getValue(), value.getUnit()));
        if (StringUtils.isNotBlank(value.getLabel())) {
          target.getLabelList().add(value.getLabel());
        }
        if (value.getUncertainty() != null || value.getDistribution() != null) {
          UncertaintyDto uncertaintyDto = new UncertaintyDto();
          uncertaintyDto.setUncertainty(value.getUncertainty());
          uncertaintyDto.setDistribution(value.getDistribution());
          if (target.getUncertaintyList() == null) {
            target.setUncertaintyList(new UncertaintyListDto());
          }
          target.getUncertaintyList().add(uncertaintyDto);
        }
      });
    }
    if (jaxbObject.getRealListXMLList() != null && !jaxbObject.getRealListXMLList().isEmpty()) {
      jaxbObject.getRealListXMLList().forEach(value -> {
        for (int i = 0; i < value.getValueXMLList().size(); i++) {
          target.getQuantitySubTypeNames().add(REAL_LIST_XML_LIST);
          if (value.getUnitXMLList().size() == 1) {
            target.getDimensions().add(createDimension(value.getValueXMLList().get(i), value.getUnitXMLList().getFirst()));
          } else {
            target.getDimensions().add(createDimension(value.getValueXMLList().get(i), value.getUnitXMLList().get(i)));
          }
          if (!value.getLabelXMLList().isEmpty()) {
            if (value.getLabelXMLList().size() == 1) {
              target.getLabelList().add(value.getLabelXMLList().getFirst());
            } else {
              target.getLabelList().add(value.getLabelXMLList().get(i));
            }
          }
          CoverageIntervalDto singleCoverageInterval = coverageIntervalXmlListMapper
              .mapSingleToDto(value.getCoverageIntervalXMLList(), i);
          if (isValid(singleCoverageInterval)) {
            if (target.getCoverageIntervalList() == null) {
              target.setCoverageIntervalList(new CoverageIntervalListDto());
            }
            target.getCoverageIntervalList().add(singleCoverageInterval);
          }
          UncertaintyDto singleUncertainty = uncertaintyXmlListMapper.mapSingleToDto(value.getExpandedUncXMLList(), i);
          if (isValid(singleUncertainty)) {
            if (target.getUncertaintyList() == null) {
              target.setUncertaintyList(new UncertaintyListDto());
            }
            target.getUncertaintyList().add(singleUncertainty);
          }
        }
      });
    }
    return target;
  }

  @Override
  public HybridType mapToJaxbObject(QuantityDto.HybridValues dto) {
    HybridType target = objectFactory.createHybridType();
    RealListXMLListType realListXMLList = objectFactory.createRealListXMLListType();
    for (int i = 0; i < dto.getDimensions().size(); i++) {
      if (StringUtils.equalsIgnoreCase(dto.getQuantitySubTypeNames().get(i), REAL)) {
        RealQuantityType realQuantity = objectFactory.createRealQuantityType();
        realQuantity.setValue(dto.getDimensions().get(i).getValue().doubleValue());
        realQuantity.setUnit(dto.getDimensions().get(i).getUnit());
        if (dto.getLabelList() != null && dto.getLabelList().size() > i) {
          realQuantity.setLabel(dto.getLabelList().get(i));
        }
        if (dto.getUncertaintyList() != null && dto.getUncertaintyList().size() > i) {
          realQuantity.setExpandedUnc(uncertaintyMapper.mapToJaxbObject(dto.getUncertaintyList().get(i)));
        }
        if (dto.getCoverageIntervalList() != null && dto.getCoverageIntervalList().size() > i) {
          realQuantity.setCoverageInterval(coverageIntervalMapper.mapToJaxbObject(dto.getCoverageIntervalList().get(i)));
        }
        target.getReal().add(realQuantity);
      }
      if (StringUtils.equalsIgnoreCase(dto.getQuantitySubTypeNames().get(i), CONSTANT)) {
        ConstantQuantityType constantQuantity = objectFactory.createConstantQuantityType();
        constantQuantity.setValue(dto.getDimensions().get(i).getValue().doubleValue());
        constantQuantity.setUnit(dto.getDimensions().get(i).getUnit());
        if (dto.getLabelList() != null && dto.getLabelList().size() > i) {
          constantQuantity.setLabel(dto.getLabelList().get(i));
        }
        if (dto.getUncertaintyList() != null && dto.getUncertaintyList().size() > i) {
          constantQuantity.setUncertainty(dto.getUncertaintyList().get(i).getUncertainty());
          constantQuantity.setDistribution(dto.getUncertaintyList().get(i).getDistribution());
        }
        target.getConstant().add(constantQuantity);
      }
      if (StringUtils.equalsIgnoreCase(dto.getQuantitySubTypeNames().get(i), REAL_LIST_XML_LIST)) {
        realListXMLList.getValueXMLList().add(dto.getDimensions().get(i).getValue().doubleValue());
        realListXMLList.getUnitXMLList().add(dto.getDimensions().get(i).getUnit());
        if (dto.getLabelList() != null && dto.getLabelList().size() > i) {
          realListXMLList.getLabelXMLList().add(dto.getLabelList().get(i));
        }
      }
    }
    if (!realListXMLList.getValueXMLList().isEmpty()) {
      if (dto.getCoverageIntervalList() != null && !dto.getCoverageIntervalList().isEmpty()) {
        realListXMLList.setCoverageIntervalXMLList(coverageIntervalXmlListMapper
            .mapToJaxbObject(dto.getCoverageIntervalList()));
      }
      if (dto.getUncertaintyList() != null && !dto.getUncertaintyList().isEmpty()) {
        realListXMLList.setExpandedUncXMLList(uncertaintyXmlListMapper.mapToJaxbObject(dto.getUncertaintyList()));
      }
      target.getRealListXMLList().add(realListXMLList);
    }
    return target;
  }
}

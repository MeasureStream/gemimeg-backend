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

import de.ptb.common.dcc.api.v1.dcc.CoverageIntervalDto;
import de.ptb.common.dcc.api.v1.dcc.CoverageIntervalListDto;
import de.ptb.common.dcc.api.v1.dcc.DimensionListDto;
import de.ptb.common.dcc.api.v1.dcc.ExpandedMUDto;
import de.ptb.common.dcc.api.v1.dcc.ExpandedMUListDto;
import de.ptb.common.dcc.api.v1.dcc.ExpandedUncDto;
import de.ptb.common.dcc.api.v1.dcc.ExpandedUncListDto;
import de.ptb.common.dcc.api.v1.dcc.QuantityDto;
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

  private final ExpandedMUMapper expandedMUMapper;
  @Deprecated
  private final ExpandedUncMapper expandedUncMapper;
  private final CoverageIntervalMapper coverageIntervalMapper;
  private final CoverageIntervalXmlListMapper coverageIntervalXmlListMapper;
  private final ExpandedMUXmlListMapper expandedMUXmlListMapper;
  @Deprecated
  private final ExpandedUncXmlListMapper expandedUncXmlListMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public HybridValuesMapper(ExpandedMUMapper expandedMUMapper, ExpandedUncMapper expandedUncMapper,
                            CoverageIntervalMapper coverageIntervalMapper,
                            CoverageIntervalXmlListMapper coverageIntervalXmlListMapper,
                            ExpandedMUXmlListMapper expandedMUXmlListMapper,
                            ExpandedUncXmlListMapper expandedUncXmlListMapper, ObjectFactory objectFactory) {
    this.expandedMUMapper = expandedMUMapper;
    this.expandedUncMapper = expandedUncMapper;
    this.coverageIntervalMapper = coverageIntervalMapper;
    this.coverageIntervalXmlListMapper = coverageIntervalXmlListMapper;
    this.expandedMUXmlListMapper = expandedMUXmlListMapper;
    this.expandedUncXmlListMapper = expandedUncXmlListMapper;
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
          if (target.getExpandedUncList() == null) {
            target.setExpandedUncList(new ExpandedUncListDto());
          }
          target.getExpandedUncList().add(expandedUncMapper.mapToDto(value.getExpandedUnc()));
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
          ExpandedMUDto expandedMUDto = new ExpandedMUDto();
          expandedMUDto.setUncertainty(value.getUncertainty());
          expandedMUDto.setDistribution(value.getDistribution());
          if (target.getExpandedMUList() == null) {
            target.setExpandedMUList(new ExpandedMUListDto());
          }
          target.getExpandedMUList().add(expandedMUDto);
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
          ExpandedUncDto singleUncertainty = expandedUncXmlListMapper.mapSingleToDto(value.getExpandedUncXMLList(), i);
          if (isValid(singleUncertainty)) {
            if (target.getExpandedUncList() == null) {
              target.setExpandedUncList(new ExpandedUncListDto());
            }
            target.getExpandedUncList().add(singleUncertainty);
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
        if (dto.getExpandedUncList() != null && dto.getExpandedUncList().size() > i) {
          realQuantity.setExpandedUnc(expandedUncMapper.mapToJaxbObject(dto.getExpandedUncList().get(i)));
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
        if (dto.getExpandedMUList() != null && dto.getExpandedMUList().size() > i) {
          constantQuantity.setUncertainty(dto.getExpandedMUList().get(i).getUncertainty());
          constantQuantity.setDistribution(dto.getExpandedMUList().get(i).getDistribution());
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
      if (dto.getExpandedUncList() != null && !dto.getExpandedUncList().isEmpty()) {
        realListXMLList.setExpandedUncXMLList(expandedUncXmlListMapper.mapToJaxbObject(dto.getExpandedUncList()));
      }
      target.getRealListXMLList().add(realListXMLList);
    }
    return target;
  }
}

/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dto.UncertaintyDto;
import de.ptb.common.dcc.api.v1.dto.UncertaintyListDto;
import de.ptb.common.dcc.xjc.generated.ExpandedUncXMLListType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static de.ptb.common.dcc.util.DccServiceUtil.isValid;

@Component
public class UncertaintyXmlListMapper implements JaxbDtoBidirectionalMapper<ExpandedUncXMLListType, UncertaintyListDto> {

  private final ObjectFactory objectFactory;

  @Autowired
  public UncertaintyXmlListMapper(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public UncertaintyListDto mapToDto(@Nonnull ExpandedUncXMLListType jaxbObject) {
    UncertaintyListDto target = new UncertaintyListDto();
    for (int i = 0; i < jaxbObject.getUncertaintyXMLList().size(); i++) {
      if (isValid(jaxbObject, i)) {
        target.add(mapSingleToDto(jaxbObject, i));
      }
    }
    return target;
  }

  @Nonnull
  public UncertaintyDto mapSingleToDto(@Nullable ExpandedUncXMLListType jaxbObject, @Nonnegative int index) {
    UncertaintyDto target = new UncertaintyDto();
    if (jaxbObject != null) {
      int currentSize = jaxbObject.getUncertaintyXMLList().size();
      int maxIndex = index >= currentSize ? currentSize - 1 : index;
      target.setUncertainty(jaxbObject.getUncertaintyXMLList().get(maxIndex));
      if (jaxbObject.getCoverageProbabilityXMLList().size() == 1) {
        target.setCoverageProbability(jaxbObject.getCoverageProbabilityXMLList().getFirst());
      } else {
        target.setCoverageProbability(jaxbObject.getCoverageProbabilityXMLList().get(maxIndex));
      }
      if (jaxbObject.getDistributionXMLList().size() == 1) {
        if (StringUtils.isNotBlank(jaxbObject.getDistributionXMLList().getFirst())) {
          target.setDistribution(jaxbObject.getDistributionXMLList().getFirst());
        }
      }
      if (jaxbObject.getDistributionXMLList().size() > 1) {
        if (StringUtils.isNotBlank(jaxbObject.getDistributionXMLList().get(maxIndex))) {
          target.setDistribution(jaxbObject.getDistributionXMLList().get(maxIndex));
        }
      }
      if (jaxbObject.getCoverageFactorXMLList().size() == 1) {
        target.setCoverageFactor(jaxbObject.getCoverageFactorXMLList().getFirst());
      } else {
        target.setCoverageFactor(jaxbObject.getCoverageFactorXMLList().get(maxIndex));
      }
    }
    return target;
  }

  @Override
  @Nonnull
  public ExpandedUncXMLListType mapToJaxbObject(@Nonnull UncertaintyListDto dto) {
    ExpandedUncXMLListType target = objectFactory.createExpandedUncXMLListType();
    for (UncertaintyDto uncertainty : dto) {
      double uncertaintyValue = uncertainty.getUncertainty();
      if (uncertaintyValue != 0.0) {
        target.getUncertaintyXMLList().add(uncertaintyValue);
      }
      double coverageFactorValue = uncertainty.getCoverageFactor();
      if (coverageFactorValue != 0.0) {
        target.getCoverageFactorXMLList().add(coverageFactorValue);
      }
      if (StringUtils.isNotBlank(uncertainty.getDistribution())) {
        target.getDistributionXMLList().add(uncertainty.getDistribution());
      }
      double coverageProbabilityValue = uncertainty.getCoverageProbability();
      if (coverageProbabilityValue != 0.0) {
        target.getCoverageProbabilityXMLList().add(coverageProbabilityValue);
      }
    }
    return target;
  }
}

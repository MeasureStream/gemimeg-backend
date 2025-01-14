/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.CoverageIntervalDto;
import de.ptb.common.dcc.api.v1.dcc.CoverageIntervalListDto;
import de.ptb.common.dcc.xjc.generated.CoverageIntervalXMLListType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static de.ptb.common.dcc.util.DccServiceUtil.isValid;

@Component
public class CoverageIntervalXmlListMapper
    implements JaxbDtoBidirectionalMapper<CoverageIntervalXMLListType, CoverageIntervalListDto> {

  private final ObjectFactory objectFactory;

  @Autowired
  public CoverageIntervalXmlListMapper(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public CoverageIntervalListDto mapToDto(@Nonnull CoverageIntervalXMLListType jaxbObject) {
    CoverageIntervalListDto target = new CoverageIntervalListDto();
    for (int i = 0; i < jaxbObject.getIntervalMinXMLList().size(); i++) {
      if (isValid(jaxbObject, i)) {
        target.add(mapSingleToDto(jaxbObject, i));
      }
    }
    return target;
  }

  @Nonnull
  public CoverageIntervalDto mapSingleToDto(@Nullable CoverageIntervalXMLListType jaxbObject, @Nonnegative int index) {
    CoverageIntervalDto target = new CoverageIntervalDto();
    if (jaxbObject != null) {
      if (StringUtils.isNotBlank(jaxbObject.getDistributionXMLList().get(index))) {
        target.setDistribution(jaxbObject.getDistributionXMLList().get(index));
      }
      double intervalMin = jaxbObject.getIntervalMinXMLList().get(index);
      double intervalMax = jaxbObject.getIntervalMaxXMLList().get(index);
      if (intervalMax != 0.0 || intervalMin < intervalMax) {
        target.setIntervalMaximum(intervalMax);
      }
      if (intervalMin != 0.0 || intervalMin < intervalMax) {
        target.setIntervalMinimum(intervalMin);
      }
      if (jaxbObject.getCoverageProbabilityXMLList().get(index) != 0) {
        target.setCoverageProbability(jaxbObject.getCoverageProbabilityXMLList().get(index));
      }
      if (jaxbObject.getStandardUncXMLList().get(index) != 0.0) {
        target.setStandardUncertainty(jaxbObject.getStandardUncXMLList().get(index));
      }
    }
    return target;
  }

  @Override
  @Nonnull
  public CoverageIntervalXMLListType mapToJaxbObject(@Nonnull CoverageIntervalListDto dto) {
    CoverageIntervalXMLListType target = objectFactory.createCoverageIntervalXMLListType();
    for (CoverageIntervalDto coverageInterval : dto) {
      if (StringUtils.isNotBlank(coverageInterval.getDistribution())) {
        target.getDistributionXMLList().add(coverageInterval.getDistribution());
      }
      if (coverageInterval.getIntervalMaximum() != 0.0 ||
          coverageInterval.getIntervalMinimum() < coverageInterval.getIntervalMaximum()) {
        target.getIntervalMaxXMLList().add(coverageInterval.getIntervalMaximum());
      }
      if (coverageInterval.getIntervalMinimum() != 0.0 ||
          coverageInterval.getIntervalMinimum() < coverageInterval.getIntervalMaximum()) {
        target.getIntervalMinXMLList().add(coverageInterval.getIntervalMinimum());
      }
      if (coverageInterval.getCoverageProbability() != 0.0) {
        target.getCoverageProbabilityXMLList().add(coverageInterval.getCoverageProbability());
      }
      if (coverageInterval.getStandardUncertainty() != 0.0) {
        target.getStandardUncXMLList().add(coverageInterval.getStandardUncertainty());
      }
    }
    return target;
  }
}

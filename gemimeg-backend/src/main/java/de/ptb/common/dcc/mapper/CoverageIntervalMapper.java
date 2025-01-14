/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.CoverageIntervalDto;
import de.ptb.common.dcc.xjc.generated.CoverageIntervalType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class CoverageIntervalMapper implements JaxbDtoBidirectionalMapper<CoverageIntervalType, CoverageIntervalDto> {

  private final ObjectFactory objectFactory;

  @Autowired
  public CoverageIntervalMapper(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public CoverageIntervalDto mapToDto(@Nonnull CoverageIntervalType jaxbObject) {
    CoverageIntervalDto target = new CoverageIntervalDto();
    target.setDistribution(jaxbObject.getDistribution());
    target.setIntervalMaximum(jaxbObject.getIntervalMax());
    target.setIntervalMinimum(jaxbObject.getIntervalMin());
    target.setCoverageProbability(jaxbObject.getCoverageProbability());
    target.setStandardUncertainty(jaxbObject.getStandardUnc());
    return target;
  }

  @Override
  @Nonnull
  public CoverageIntervalType mapToJaxbObject(@Nonnull CoverageIntervalDto dto) {
    CoverageIntervalType target = objectFactory.createCoverageIntervalType();
    target.setDistribution(dto.getDistribution());
    target.setIntervalMax(dto.getIntervalMaximum());
    target.setIntervalMin(dto.getIntervalMinimum());
    target.setCoverageProbability(dto.getCoverageProbability());
    target.setStandardUnc(dto.getStandardUncertainty());
    return target;
  }
}

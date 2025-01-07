/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dto.UncertaintyDto;
import de.ptb.common.dcc.xjc.generated.ExpandedUncType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class UncertaintyMapper implements JaxbDtoBidirectionalMapper<ExpandedUncType, UncertaintyDto> {

  private final ObjectFactory objectFactory;

  @Autowired
  public UncertaintyMapper(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public UncertaintyDto mapToDto(@Nonnull ExpandedUncType jaxbObject) {
    UncertaintyDto target = new UncertaintyDto();
    target.setUncertainty(jaxbObject.getUncertainty());
    target.setCoverageProbability(jaxbObject.getCoverageProbability());
    target.setDistribution(jaxbObject.getDistribution());
    target.setCoverageFactor(jaxbObject.getCoverageFactor());
    return target;
  }

  @Override
  @Nonnull
  public ExpandedUncType mapToJaxbObject(@Nonnull UncertaintyDto dto) {
    ExpandedUncType target = objectFactory.createExpandedUncType();
    target.setUncertainty(dto.getUncertainty());
    target.setCoverageProbability(dto.getCoverageProbability());
    target.setDistribution(dto.getDistribution());
    target.setCoverageFactor(dto.getCoverageFactor());
    return target;
  }
}

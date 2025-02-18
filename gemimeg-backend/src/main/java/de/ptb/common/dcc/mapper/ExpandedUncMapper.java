/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.ExpandedUncDto;
import de.ptb.common.dcc.xjc.generated.ExpandedUncType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Deprecated
@Component
public class ExpandedUncMapper implements JaxbDtoBidirectionalMapper<ExpandedUncType, ExpandedUncDto> {

  private final ObjectFactory objectFactory;

  @Autowired
  public ExpandedUncMapper(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public ExpandedUncDto mapToDto(@Nonnull ExpandedUncType jaxbObject) {
    ExpandedUncDto target = new ExpandedUncDto();
    target.setUncertainty(jaxbObject.getUncertainty());
    target.setCoverageProbability(jaxbObject.getCoverageProbability());
    target.setDistribution(jaxbObject.getDistribution());
    target.setCoverageFactor(jaxbObject.getCoverageFactor());
    return target;
  }

  @Override
  @Nonnull
  public ExpandedUncType mapToJaxbObject(@Nonnull ExpandedUncDto dto) {
    ExpandedUncType target = objectFactory.createExpandedUncType();
    target.setUncertainty(dto.getUncertainty());
    target.setCoverageProbability(dto.getCoverageProbability());
    target.setDistribution(dto.getDistribution());
    target.setCoverageFactor(dto.getCoverageFactor());
    return target;
  }
}

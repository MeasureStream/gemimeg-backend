/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.ExpandedMUDto;
import de.ptb.common.dcc.xjc.generated.ExpandedMUType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class ExpandedMUMapper implements JaxbDtoBidirectionalMapper<ExpandedMUType, ExpandedMUDto> {

  private final ObjectFactory objectFactory;

  @Autowired
  public ExpandedMUMapper(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public ExpandedMUDto mapToDto(@Nonnull ExpandedMUType jaxbObject) {
    ExpandedMUDto target = new ExpandedMUDto();
    target.setUncertainty(jaxbObject.getValueExpandedMU());
    target.setCoverageProbability(jaxbObject.getCoverageProbability());
    target.setDistribution(jaxbObject.getDistribution());
    target.setCoverageFactor(jaxbObject.getCoverageFactor());
    return target;
  }

  @Override
  @Nonnull
  public ExpandedMUType mapToJaxbObject(@Nonnull ExpandedMUDto dto) {
    ExpandedMUType target = objectFactory.createExpandedMUType();
    target.setValueExpandedMU(dto.getUncertainty());
    target.setCoverageProbability(dto.getCoverageProbability());
    target.setDistribution(dto.getDistribution());
    target.setCoverageFactor(dto.getCoverageFactor());
    return target;
  }
}

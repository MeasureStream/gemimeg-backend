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

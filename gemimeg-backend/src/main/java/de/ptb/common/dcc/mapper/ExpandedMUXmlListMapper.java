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

import de.ptb.common.dcc.api.v1.dcc.ExpandedMUDto;
import de.ptb.common.dcc.api.v1.dcc.ExpandedMUListDto;
import de.ptb.common.dcc.xjc.generated.ExpandedMUXMLListType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static de.ptb.common.dcc.util.DccServiceUtil.isValid;

@Component
public class ExpandedMUXmlListMapper implements JaxbDtoBidirectionalMapper<ExpandedMUXMLListType, ExpandedMUListDto> {

  private final ObjectFactory objectFactory;

  @Autowired
  public ExpandedMUXmlListMapper(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public ExpandedMUListDto mapToDto(@Nonnull ExpandedMUXMLListType jaxbObject) {
    ExpandedMUListDto target = new ExpandedMUListDto();
    for (int i = 0; i < jaxbObject.getValueExpandedMUXMLList().size(); i++) {
      if (isValid(jaxbObject, i)) {
        target.add(mapSingleToDto(jaxbObject, i));
      }
    }
    return target;
  }

  @Nonnull
  public ExpandedMUDto mapSingleToDto(@Nullable ExpandedMUXMLListType jaxbObject, @Nonnegative int index) {
    ExpandedMUDto target = new ExpandedMUDto();
    if (jaxbObject != null) {
      int currentSize = jaxbObject.getValueExpandedMUXMLList().size();
      int maxIndex = index >= currentSize ? currentSize - 1 : index;
      target.setUncertainty(jaxbObject.getValueExpandedMUXMLList().get(maxIndex));
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
  public ExpandedMUXMLListType mapToJaxbObject(@Nonnull ExpandedMUListDto dto) {
    ExpandedMUXMLListType target = objectFactory.createExpandedMUXMLListType();
    for (ExpandedMUDto uncertainty : dto) {
      double uncertaintyValue = uncertainty.getUncertainty();
      if (uncertaintyValue != 0.0) {
        target.getValueExpandedMUXMLList().add(uncertaintyValue);
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

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

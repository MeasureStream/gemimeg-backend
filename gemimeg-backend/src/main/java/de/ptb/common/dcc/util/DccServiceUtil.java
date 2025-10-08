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
package de.ptb.common.dcc.util;

import de.ptb.common.dcc.api.v1.dcc.ByteDataDto;
import de.ptb.common.dcc.api.v1.dcc.ContactDto;
import de.ptb.common.dcc.api.v1.dcc.CoverageIntervalDto;
import de.ptb.common.dcc.api.v1.dcc.DimensionDto;
import de.ptb.common.dcc.api.v1.dcc.EquipmentDto;
import de.ptb.common.dcc.api.v1.dcc.ExpandedMUDto;
import de.ptb.common.dcc.api.v1.dcc.ExpandedUncDto;
import de.ptb.common.dcc.api.v1.dcc.FormulaDto;
import de.ptb.common.dcc.api.v1.dcc.HasId;
import de.ptb.common.dcc.api.v1.dcc.HasRefIds;
import de.ptb.common.dcc.api.v1.dcc.HasRefTypes;
import de.ptb.common.dcc.api.v1.dcc.LanguageSpecificStringsDto;
import de.ptb.common.dcc.api.v1.dcc.LocationDto;
import de.ptb.common.dcc.api.v1.dcc.RichContentDto;
import de.ptb.common.dcc.api.v1.dcc.SignatureDto;
import de.ptb.common.dcc.api.v1.dcc.StatementDto;
import de.ptb.common.dcc.xjc.generated.CoverageIntervalType;
import de.ptb.common.dcc.xjc.generated.CoverageIntervalXMLListType;
import de.ptb.common.dcc.xjc.generated.ExpandedMUType;
import de.ptb.common.dcc.xjc.generated.ExpandedMUXMLListType;
import de.ptb.common.dcc.xjc.generated.ExpandedUncType;
import de.ptb.common.dcc.xjc.generated.ExpandedUncXMLListType;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class DccServiceUtil {

  public static final String NS_PREFIX_MAPPER = "org.glassfish.jaxb.namespacePrefixMapper";
  public static final String NS_DCC = "https://ptb.de/dcc";
  public static final String NS_SI = "https://ptb.de/si";
  public static final String NS_DS = "http://www.w3.org/2000/09/xmldsig#";
  public static final String NS_M = "http://www.w3.org/1998/Math/MathML";
  public static final String NS_XADES = "http://uri.etsi.org/01903/v1.3.2#";
  public static final Set<String> NAMESPACES = Set.of(NS_DCC + "|dcc", NS_SI + "|si", NS_DS + "|ds", NS_M + "|m",
      NS_XADES + "|xades");

  @Getter
  private static final NamespacePrefixMapper namespacePrefixMapper;

  static {
    namespacePrefixMapper = new NamespacePrefixMapper() {
      @Override
      public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        for (String namespace : NAMESPACES) {
          String currentUri = StringUtils.substringBefore(namespace, "|");
          String prefix = StringUtils.substringAfter(namespace, "|");
          if (Strings.CI.equals(currentUri, namespaceUri)) {
            return prefix;
          }
        }
        return suggestion;
      }
    };
  }

  private DccServiceUtil() {
    // Utility class
  }

  @Nonnull
  public static String createLogEntry(@Nonnull Throwable t, String... suffixes) {
    String logEntry = t.getClass().getSimpleName() + ": " + t.getMessage();
    if (t.getCause() != null) {
      logEntry += ": " + t.getCause().getMessage();
    }
    if (suffixes != null && suffixes.length > 0) {
      logEntry += " - " + String.join("; ", suffixes);
    }
    return logEntry;
  }

  @Nonnull
  public static DimensionDto createDimension(@Nonnull Number value, @Nonnull String unit) {
    DimensionDto dimension = new DimensionDto();
    dimension.setValue(value);
    dimension.setUnit(unit);
    return dimension;
  }

  public static boolean isValid(@Nonnull ExpandedUncType expandedUnc) {
    return expandedUnc.getUncertainty() != 0 || expandedUnc.getCoverageFactor() != 0 ||
        expandedUnc.getCoverageProbability() != 0;
  }

  public static boolean isValid(@Nonnull ExpandedMUType expandedMUType) {
    return expandedMUType.getValueExpandedMU() != 0 || expandedMUType.getCoverageFactor() != 0 ||
        expandedMUType.getCoverageProbability() != 0;
  }

  public static boolean isValid(@Nonnull CoverageIntervalType coverageInterval) {
    return (coverageInterval.getCoverageProbability() != 0 || coverageInterval.getStandardUnc() != 0) &&
        (coverageInterval.getIntervalMin() < coverageInterval.getIntervalMax());
  }

  public static boolean isValid(@Nonnull ExpandedMUDto expandedMU) {
    return expandedMU.getUncertainty() != 0 || expandedMU.getCoverageFactor() != 0 ||
        expandedMU.getCoverageProbability() != 0;
  }

  public static boolean isValid(@Nonnull ExpandedUncDto expandedUnc) {
    return expandedUnc.getUncertainty() != 0 || expandedUnc.getCoverageFactor() != 0 ||
        expandedUnc.getCoverageProbability() != 0;
  }

  public static boolean isValid(@Nonnull CoverageIntervalDto coverageInterval) {
    return (coverageInterval.getCoverageProbability() != 0 || coverageInterval.getStandardUncertainty() != 0) &&
        (coverageInterval.getIntervalMinimum() < coverageInterval.getIntervalMaximum());
  }

  public static boolean isValid(@Nonnull CoverageIntervalXMLListType coverageIntervalXMLList,
                                @Nonnegative int index) {
    double coverageProbability = coverageIntervalXMLList.getCoverageProbabilityXMLList().get(index);
    double standardUnc = coverageIntervalXMLList.getStandardUncXMLList().get(index);
    double intervalMin = coverageIntervalXMLList.getIntervalMinXMLList().get(index);
    double intervalMax = coverageIntervalXMLList.getIntervalMaxXMLList().get(index);
    return (coverageProbability != 0 || standardUnc != 0) && (intervalMin < intervalMax);
  }

  public static boolean isValid(@Nonnull ExpandedUncXMLListType expandedUncXMLList,
                                @Nonnegative int index) {
    double uncertainty = expandedUncXMLList.getUncertaintyXMLList().get(index);
    double coverageFactor = 0.0;
    if (expandedUncXMLList.getCoverageFactorXMLList().size() > index) {
      coverageFactor = expandedUncXMLList.getCoverageFactorXMLList().get(index);
    }
    double coverageProbability = 0.0;
    if (expandedUncXMLList.getCoverageProbabilityXMLList().size() > index) {
      coverageProbability = expandedUncXMLList.getCoverageProbabilityXMLList().get(index);
    }
    return uncertainty != 0 || coverageFactor != 0 || coverageProbability != 0;
  }

  public static boolean isValid(@Nonnull ExpandedMUXMLListType expandedMUXMLListType,
                                @Nonnegative int index) {
    double uncertainty = expandedMUXMLListType.getValueExpandedMUXMLList().get(index);
    double coverageFactor = 0.0;
    if (expandedMUXMLListType.getCoverageFactorXMLList().size() > index) {
      coverageFactor = expandedMUXMLListType.getCoverageFactorXMLList().get(index);
    }
    double coverageProbability = 0.0;
    if (expandedMUXMLListType.getCoverageProbabilityXMLList().size() > index) {
      coverageProbability = expandedMUXMLListType.getCoverageProbabilityXMLList().get(index);
    }
    return uncertainty != 0 || coverageFactor != 0 || coverageProbability != 0;
  }

  public static String enumValue(String source) {
    String regex = "([a-z])([A-Z]+)";
    String replacement = "$1_$2";
    return source.replaceAll(regex, replacement).toUpperCase();
  }

  public static boolean isNotEmpty(@Nullable LanguageSpecificStringsDto sample) {
    if (sample == null) {
      return false;
    }
    return sample.getContent() != null && sample.getContent().getFirst() != null &&
        io.micrometer.common.util.StringUtils.isNotBlank(sample.getContent().getFirst().getText());
  }

  public static boolean isNotEmpty(@Nullable EquipmentDto sample) {
    if (sample == null) {
      return false;
    }
    return isNotEmpty(sample.getName()) || isNotEmpty(sample.getManufacturer()) ||
        StringUtils.isNotBlank(sample.getModel()) || !(sample.getIdentifications() == null ||
        sample.getIdentifications().isEmpty());
  }

  public static boolean isNotEmpty(@Nullable RichContentDto sample) {
    if (sample == null) {
      return false;
    }
    return isNotEmpty(sample.getTextContent()) || isNotEmpty(sample.getByteDataContent()) ||
        isNotEmpty(sample.getFormulaContent());
  }

  public static boolean isNotEmpty(@Nullable ByteDataDto sample) {
    if (sample == null) {
      return false;
    }
    return StringUtils.isNoneBlank(sample.getFileName(), sample.getMimeType()) && sample.getContent() != null &&
        sample.getContent().length > 0;
  }

  public static boolean isNotEmpty(@Nullable LocationDto sample) {
    if (sample == null) {
      return false;
    }
    return StringUtils.isNoneBlank(sample.getCity(), sample.getCountryCode());
  }

  public static boolean isNotEmpty(@Nullable ContactDto sample) {
    if (sample == null) {
      return false;
    }
    return isNotEmpty(sample.getName()) ||
        StringUtils.isNotBlank(sample.getEMailAddress()) ||
        StringUtils.isNotBlank(sample.getPhoneNumber());
  }

  public static boolean isNotEmpty(@Nullable FormulaDto sample) {
    if (sample == null) {
      return false;
    }
    return sample.getType() != null && StringUtils.isNotBlank(sample.getContent());
  }

  public static boolean isNotEmpty(@Nullable SignatureDto sample) {
    if (sample == null) {
      return false;
    }
    return sample.getValue() != null && sample.getValue().getValue() != null && sample.getValue().getValue().length > 0;
  }

  public static boolean isNotEmpty(@Nullable Collection<String> sample) {
    if (sample == null) {
      return false;
    }
    return !sample.isEmpty() && sample.iterator().hasNext() && StringUtils.isNotBlank(sample.iterator().next());
  }

  public static boolean isNotEmpty(@Nullable StatementDto sample) {
    return sample != null;
  }

  public static <T extends HasId> void setId(T target, @Nullable String id) {
    if (StringUtils.isNotBlank(id)) {
      target.setId(id);
    }
  }

  public static <T extends HasRefIds> void setRefIds(T target, @Nullable String... refIds) {
    if (refIds != null && refIds.length > 0 && StringUtils.isNoneBlank(refIds)) {
      target.setRefIds(List.of(refIds));
    }
  }

  public static <T extends HasRefIds> void setRefIds(T target, @Nullable List<Object> refIds) {
    if (refIds != null && !refIds.isEmpty()) {
      target.setRefIds(refIds.stream()
          .map(Object::toString)
          .filter(StringUtils::isNotBlank)
          .toList());
    }
  }

  public static <T extends HasRefTypes> void setRefTypes(T target, @Nullable List<String> refTypes) {
    if (refTypes != null && !refTypes.isEmpty()) {
      target.setRefTypes(refTypes.stream()
          .filter(StringUtils::isNotBlank)
          .toList());
    }
  }
}

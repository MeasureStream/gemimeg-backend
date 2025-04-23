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

import de.ptb.common.dcc.api.v1.dcc.ConditionListDto;
import de.ptb.common.dcc.api.v1.dcc.EquipmentListDto;
import de.ptb.common.dcc.api.v1.dcc.MeasurementResultDto;
import de.ptb.common.dcc.api.v1.dcc.MethodListDto;
import de.ptb.common.dcc.api.v1.dcc.ResultListDto;
import de.ptb.common.dcc.api.v1.dcc.SoftwareListDto;
import de.ptb.common.dcc.api.v1.dcc.StatementListDto;
import de.ptb.common.dcc.util.DccServiceUtil;
import de.ptb.common.dcc.xjc.generated.InfluenceConditionListType;
import de.ptb.common.dcc.xjc.generated.MeasurementMetaDataListType;
import de.ptb.common.dcc.xjc.generated.MeasurementResultType;
import de.ptb.common.dcc.xjc.generated.MeasuringEquipmentListType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.ResultListType;
import de.ptb.common.dcc.xjc.generated.SoftwareListType;
import de.ptb.common.dcc.xjc.generated.UsedMethodListType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Component
public class MeasurementResultMapper implements JaxbDtoBidirectionalMapper<MeasurementResultType, MeasurementResultDto> {

  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final ResultMapper resultMapper;
  private final MethodMapper methodMapper;
  private final EquipmentMapper equipmentMapper;
  private final ConditionMapper conditionMapper;
  private final SoftwareMapper softwareMapper;
  private final RichContentMapper richContentMapper;
  private final StatementMapper statementMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public MeasurementResultMapper(LanguageSpecificStringsMapper languageSpecificStringsMapper, ResultMapper resultMapper,
                                 MethodMapper methodMapper, EquipmentMapper equipmentMapper,
                                 ConditionMapper conditionMapper, SoftwareMapper softwareMapper,
                                 RichContentMapper richContentMapper, StatementMapper statementMapper,
                                 ObjectFactory objectFactory) {
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.resultMapper = resultMapper;
    this.methodMapper = methodMapper;
    this.equipmentMapper = equipmentMapper;
    this.conditionMapper = conditionMapper;
    this.softwareMapper = softwareMapper;
    this.richContentMapper = richContentMapper;
    this.statementMapper = statementMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public MeasurementResultDto mapToDto(@Nonnull MeasurementResultType jaxbObject) {
    MeasurementResultDto target = new MeasurementResultDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (jaxbObject.getName() != null) {
      target.setName(languageSpecificStringsMapper.mapToDto(jaxbObject.getName()));
    }
    if (jaxbObject.getDescription() != null) {
      target.setDescription(richContentMapper.mapToDto(jaxbObject.getDescription()));
    }
    if (jaxbObject.getResults() != null && !jaxbObject.getResults().getResult().isEmpty()) {
      ResultListDto results = new ResultListDto();
      results.addAll(jaxbObject.getResults().getResult().stream()
          .map(resultMapper::mapToDto)
          .toList());
      target.setResults(results);
    }
    if (jaxbObject.getUsedMethods() != null && !jaxbObject.getUsedMethods().getUsedMethod().isEmpty()) {
      MethodListDto methods = new MethodListDto();
      methods.addAll(jaxbObject.getUsedMethods().getUsedMethod().stream()
          .map(methodMapper::mapToDto)
          .toList());
      target.setUsedMethods(methods);
    }
    if (jaxbObject.getInfluenceConditions() != null &&
        !jaxbObject.getInfluenceConditions().getInfluenceCondition().isEmpty()) {
      ConditionListDto conditions = new ConditionListDto();
      conditions.addAll(jaxbObject.getInfluenceConditions().getInfluenceCondition().stream()
          .map(conditionMapper::mapToDto)
          .toList());
      target.setInfluenceConditions(conditions);
    }
    if (jaxbObject.getMeasuringEquipments() != null &&
        !jaxbObject.getMeasuringEquipments().getMeasuringEquipment().isEmpty()) {
      EquipmentListDto equipment = new EquipmentListDto();
      equipment.addAll(jaxbObject.getMeasuringEquipments().getMeasuringEquipment().stream()
          .map(equipmentMapper::mapToDto)
          .toList());
      target.setEquipment(equipment);
    }
    if (jaxbObject.getMeasurementMetaData() != null && !jaxbObject.getMeasurementMetaData().getMetaData().isEmpty()) {
      StatementListDto statementList = new StatementListDto();
      statementList.addAll(jaxbObject.getMeasurementMetaData().getMetaData().stream()
          .map(statementMapper::mapToDto)
          .toList());
      target.setStatements(statementList);
    }
    if (jaxbObject.getUsedSoftware() != null && !jaxbObject.getUsedSoftware().getSoftware().isEmpty()) {
      SoftwareListDto software = new SoftwareListDto();
      software.addAll(jaxbObject.getUsedSoftware().getSoftware().stream()
          .map(softwareMapper::mapToDto)
          .toList());
      target.setUsedSoftware(software);
    }
    return target;
  }

  @Override
  @Nonnull
  public MeasurementResultType mapToJaxbObject(@Nonnull MeasurementResultDto dto) {
    MeasurementResultType target = objectFactory.createMeasurementResultType();
    if (StringUtils.isNotBlank(dto.getId())) {
      target.setId(dto.getId());
    }
    if (isNotEmpty(dto.getRefIds())) {
      target.getRefId().addAll(dto.getRefIds());
    }
    if (isNotEmpty(dto.getRefTypes())) {
      target.getRefType().addAll(dto.getRefTypes());
    }
    if (isNotEmpty(dto.getName())) {
      target.setName(languageSpecificStringsMapper.mapToJaxbObject(dto.getName()));
    }
    if (isNotEmpty(dto.getDescription())) {
      target.setDescription(richContentMapper.mapToJaxbObject(dto.getDescription()));
    }
    if (dto.getResults() != null && !dto.getResults().isEmpty()) {
      ResultListType resultList = objectFactory.createResultListType();
      resultList.getResult().addAll(dto.getResults().stream()
          .map(resultMapper::mapToJaxbObject)
          .toList());
      target.setResults(resultList);
    }
    if (dto.getUsedMethods() != null && !dto.getUsedMethods().isEmpty()) {
      UsedMethodListType usedMethodList = objectFactory.createUsedMethodListType();
      usedMethodList.getUsedMethod().addAll(dto.getUsedMethods().stream()
          .map(methodMapper::mapToJaxbObject)
          .toList());
      target.setUsedMethods(usedMethodList);
    }
    if (dto.getInfluenceConditions() != null && !dto.getInfluenceConditions().isEmpty()) {
      InfluenceConditionListType conditionList = objectFactory.createInfluenceConditionListType();
      conditionList.getInfluenceCondition().addAll(dto.getInfluenceConditions().stream()
          .map(conditionMapper::mapToJaxbObject)
          .toList());
      target.setInfluenceConditions(conditionList);
    }
    if (dto.getEquipment() != null && !dto.getEquipment().isEmpty()) {
      MeasuringEquipmentListType equipmentList = objectFactory.createMeasuringEquipmentListType();
      equipmentList.getMeasuringEquipment().addAll(dto.getEquipment().stream()
          .filter(DccServiceUtil::isNotEmpty)
          .map(equipmentMapper::mapToJaxbObject)
          .toList());
      if (!equipmentList.getMeasuringEquipment().isEmpty()) {
        target.setMeasuringEquipments(equipmentList);
      }
    }
    if (dto.getStatements() != null && !dto.getStatements().isEmpty()) {
      MeasurementMetaDataListType metaDataList = objectFactory.createMeasurementMetaDataListType();
      metaDataList.getMetaData().addAll(dto.getStatements().stream()
          .filter(DccServiceUtil::isNotEmpty)
          .map(statementMapper::mapToJaxbObject)
          .toList());
      if (!metaDataList.getMetaData().isEmpty()) {
        target.setMeasurementMetaData(metaDataList);
      }
    }
    if (dto.getUsedSoftware() != null && !dto.getUsedSoftware().isEmpty()) {
      SoftwareListType softwareList = objectFactory.createSoftwareListType();
      softwareList.getSoftware().addAll(dto.getUsedSoftware().stream()
          .filter(software -> isNotEmpty(software.getName()))
          .map(softwareMapper::mapToJaxbObject)
          .toList());
      if (!softwareList.getSoftware().isEmpty()) {
        target.setUsedSoftware(softwareList);
      }
    }
    return target;
  }
}

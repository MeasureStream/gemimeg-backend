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

import de.ptb.common.dcc.api.v1.dcc.DataDto;
import de.ptb.common.dcc.api.v1.dcc.DataListDto;
import de.ptb.common.dcc.xjc.generated.ByteDataType;
import de.ptb.common.dcc.xjc.generated.DataType;
import de.ptb.common.dcc.xjc.generated.FormulaType;
import de.ptb.common.dcc.xjc.generated.ListType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.QuantityType;
import de.ptb.common.dcc.xjc.generated.RichContentType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Component
public class DataMapper implements JaxbDtoBidirectionalMapper<DataType, DataListDto> {

  private final ByteDataMapper byteDataMapper;
  private final FormulaMapper formulaMapper;
  private final ListMapper listMapper;
  private final QuantityMapper quantityMapper;
  private final RichContentMapper richContentMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public DataMapper(ByteDataMapper byteDataMapper, FormulaMapper formulaMapper, ListMapper listMapper,
                    QuantityMapper quantityMapper, RichContentMapper richContentMapper, ObjectFactory objectFactory) {
    this.byteDataMapper = byteDataMapper;
    this.formulaMapper = formulaMapper;
    this.listMapper = listMapper;
    this.quantityMapper = quantityMapper;
    this.richContentMapper = richContentMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public DataListDto mapToDto(@Nonnull DataType jaxbObject) {
    DataListDto target = new DataListDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (jaxbObject.getTextOrFormulaOrByteData() != null && !jaxbObject.getTextOrFormulaOrByteData().isEmpty()) {
      jaxbObject.getTextOrFormulaOrByteData().forEach(item -> {
        DataDto dataDto = new DataDto();
        if (item instanceof ByteDataType) {
          dataDto.setByteData(byteDataMapper.mapToDto((ByteDataType) item));
        }
        if (item instanceof FormulaType) {
          dataDto.setFormula(formulaMapper.mapToDto((FormulaType) item));
        }
        if (item instanceof ListType) {
          dataDto.setList(listMapper.mapToDto((ListType) item));
        }
        if (item instanceof QuantityType) {
          dataDto.setQuantity(quantityMapper.mapToDto((QuantityType) item));
        }
        if (item instanceof RichContentType) {
          dataDto.setRichContent(richContentMapper.mapToDto((RichContentType) item));
        }
        target.add(dataDto);
      });
    }
    return target;
  }

  @Override
  public DataType mapToJaxbObject(DataListDto dto) {
    DataType target = objectFactory.createDataType();
    if (StringUtils.isNotBlank(dto.getId())) {
      target.setId(dto.getId());
    }
    if (isNotEmpty(dto.getRefIds())) {
      target.getRefId().addAll(dto.getRefIds());
    }
    if (isNotEmpty(dto.getRefTypes())) {
      target.getRefType().addAll(dto.getRefTypes());
    }

    dto.forEach(item -> {
      if (item.getByteData() != null) {
        target.getTextOrFormulaOrByteData().add(byteDataMapper.mapToJaxbObject(item.getByteData()));
      }
      if (isNotEmpty(item.getFormula())) {
        target.getTextOrFormulaOrByteData().add(formulaMapper.mapToJaxbObject(item.getFormula()));
      }
      if (item.getQuantity() != null) {
        target.getTextOrFormulaOrByteData().add(quantityMapper.mapToJaxbObject(item.getQuantity()));
      }
      if (item.getList() != null) {
        target.getTextOrFormulaOrByteData().add(listMapper.mapToJaxbObject(item.getList()));
      }
      if (isNotEmpty(item.getRichContent())) {
        target.getTextOrFormulaOrByteData().add(richContentMapper.mapToJaxbObject(item.getRichContent()));
      }
    });
    return target;
  }
}

package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.ListDto;
import de.ptb.common.dcc.api.v1.dcc.MethodListDto;
import de.ptb.common.dcc.api.v1.dcc.QuantityListDto;
import de.ptb.common.dcc.xjc.generated.ListType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.QuantityType;
import de.ptb.common.dcc.xjc.generated.UsedMethodListType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefTypes;

@Component
public class ListMapper implements JaxbDtoBidirectionalMapper<ListType, ListDto> {

  private final LanguageSpecificStringsMapper languageSpecificStringsMapper;
  private final RichContentMapper richContentMapper;
  private final QuantityMapper quantityMapper;
  private final MethodMapper methodMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public ListMapper(LanguageSpecificStringsMapper languageSpecificStringsMapper, RichContentMapper richContentMapper,
                    QuantityMapper quantityMapper, MethodMapper methodMapper, ObjectFactory objectFactory) {
    this.languageSpecificStringsMapper = languageSpecificStringsMapper;
    this.richContentMapper = richContentMapper;
    this.quantityMapper = quantityMapper;
    this.methodMapper = methodMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public ListDto mapToDto(@Nonnull ListType jaxbObject) {
    ListDto target = new ListDto();
    setId(target, jaxbObject.getId());
    setRefIds(target, jaxbObject.getRefId());
    setRefTypes(target, jaxbObject.getRefType());
    if (jaxbObject.getName() != null) {
      target.setName(languageSpecificStringsMapper.mapToDto(jaxbObject.getName()));
    }
    if (jaxbObject.getDescription() != null) {
      target.setDescription(richContentMapper.mapToDto(jaxbObject.getDescription()));
    }
    if (jaxbObject.getDateTime() != null) {
      target.setDateTime(convertDateTime(jaxbObject.getDateTime()));
    }
    if (jaxbObject.getListOrQuantity() != null && !jaxbObject.getListOrQuantity().isEmpty()) {
      QuantityListDto quantityList = new QuantityListDto();
      quantityList.addAll(jaxbObject.getListOrQuantity().stream()
          .filter(object -> object instanceof QuantityType)
          .map(object -> quantityMapper.mapToDto((QuantityType) object)).toList());
      if (!quantityList.isEmpty()) {
        target.setQuantities(quantityList);
      }
      List<ListDto> targetListList = jaxbObject.getListOrQuantity().stream()
          .filter(object -> object instanceof ListType)
          .map(object -> this.mapToDto((ListType) object)).toList();
      if (!targetListList.isEmpty()) {
        target.setList(targetListList);
      }
    }
    if (jaxbObject.getUsedMethods() != null && !jaxbObject.getUsedMethods().getUsedMethod().isEmpty()) {
      MethodListDto methods = new MethodListDto();
      methods.addAll(jaxbObject.getUsedMethods().getUsedMethod().stream()
          .map(methodMapper::mapToDto)
          .toList());
      target.setUsedMethods(methods);
    }
    return target;
  }

  @Override
  @Nonnull
  public ListType mapToJaxbObject(@Nonnull ListDto dto) {
    ListType target = objectFactory.createListType();
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
    if (dto.getDateTime() != null) {
      target.setDateTime(convertDateTime(dto.getDateTime()));
    }
    if (dto.getQuantities() != null && !dto.getQuantities().isEmpty()) {
      target.getListOrQuantity().addAll(dto.getQuantities().stream()
          .map(quantityMapper::mapToJaxbObject)
          .toList());
    }
    if (dto.getUsedMethods() != null) {
      UsedMethodListType usedMethods = objectFactory.createUsedMethodListType();
      usedMethods.getUsedMethod().addAll(dto.getUsedMethods().stream()
          .map(methodMapper::mapToJaxbObject)
          .toList());
      target.setUsedMethods(usedMethods);
    }
    if (dto.getList() != null && !dto.getList().isEmpty()) {
      target.getListOrQuantity().addAll(dto.getList().stream()
          .map(this::mapToJaxbObject)
          .toList());
    }
    return target;
  }
}

/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dto.CalibrationCertificateDto;
import de.ptb.common.dcc.api.v1.dto.MeasurementResultListDto;
import de.ptb.common.dcc.api.v1.dto.SignatureListDto;
import de.ptb.common.dcc.util.DccServiceUtil;
import de.ptb.common.dcc.xjc.generated.DigitalCalibrationCertificateType;
import de.ptb.common.dcc.xjc.generated.MeasurementResultListType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import io.micrometer.common.util.StringUtils;
import jakarta.xml.bind.JAXBElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;
import java.util.stream.Collectors;

import static de.ptb.common.dcc.api.v1.CalibrationCertificateConstants.DEFAULT_SCHEMA_VERSION;
import static de.ptb.common.dcc.util.DccServiceUtil.isNotEmpty;

@Component
public class CalibrationCertificateMapper
    implements JaxbDtoBidirectionalMapper<DigitalCalibrationCertificateType, CalibrationCertificateDto> {

  private final AdministrativeDataMapper administrativeDataMapper;
  private final MeasurementResultMapper measurementResultMapper;
  private final ByteDataMapper byteDataMapper;
  private final SignatureMapper signatureMapper;
  private final ObjectFactory objectFactory;

  @Autowired
  public CalibrationCertificateMapper(AdministrativeDataMapper administrativeDataMapper,
                                      MeasurementResultMapper measurementResultMapper,
                                      ByteDataMapper byteDataMapper, SignatureMapper signatureMapper,
                                      ObjectFactory objectFactory) {
    this.administrativeDataMapper = administrativeDataMapper;
    this.measurementResultMapper = measurementResultMapper;
    this.byteDataMapper = byteDataMapper;
    this.signatureMapper = signatureMapper;
    this.objectFactory = objectFactory;
  }

  @Override
  @Nonnull
  public CalibrationCertificateDto mapToDto(@Nonnull DigitalCalibrationCertificateType jaxbObject) {
    CalibrationCertificateDto target = new CalibrationCertificateDto();
    if (jaxbObject.getAdministrativeData() != null) {
      target.setAdministrativeData(administrativeDataMapper.mapToDto(jaxbObject.getAdministrativeData()));
    }
    if (jaxbObject.getMeasurementResults() != null &&
        !jaxbObject.getMeasurementResults().getMeasurementResult().isEmpty()) {
      MeasurementResultListDto measurementResults = new MeasurementResultListDto();
      measurementResults.addAll(jaxbObject.getMeasurementResults().getMeasurementResult().stream()
          .map(measurementResultMapper::mapToDto)
          .toList());
      target.setMeasurementResults(measurementResults);
    }
    if (jaxbObject.getDocument() != null) {
      target.setDocument(byteDataMapper.mapToDto(jaxbObject.getDocument()));
    }
    if (jaxbObject.getComment() != null && !jaxbObject.getComment().getAny().isEmpty()) {
      target.setComments(jaxbObject.getComment().getAny().stream()
          .map(Object::toString)
          .collect(Collectors.toList()));
    }
    if (jaxbObject.getSignature() != null && !jaxbObject.getSignature().isEmpty()) {
      SignatureListDto signatures = new SignatureListDto();
      signatures.addAll(jaxbObject.getSignature().stream()
          .map(signatureMapper::mapToDto)
          .toList());
      target.setSignatures(signatures);
    }
    target.setSchemaVersion(jaxbObject.getSchemaVersion());
    return target;
  }

  @Override
  @Nonnull
  public DigitalCalibrationCertificateType mapToJaxbObject(@Nonnull CalibrationCertificateDto dto) {
    DigitalCalibrationCertificateType target = objectFactory.createDigitalCalibrationCertificateType();
    if (dto.getAdministrativeData() != null) {
      target.setAdministrativeData(administrativeDataMapper.mapToJaxbObject(dto.getAdministrativeData()));
    }
    if (dto.getMeasurementResults() != null && !dto.getMeasurementResults().isEmpty()) {
      MeasurementResultListType measurementResultList = objectFactory.createMeasurementResultListType();
      measurementResultList.getMeasurementResult().addAll(dto.getMeasurementResults().stream()
          .map(measurementResultMapper::mapToJaxbObject)
          .toList());
      target.setMeasurementResults(measurementResultList);
    }
    if (dto.getDocument() != null) {
      target.setDocument(byteDataMapper.mapToJaxbObject(dto.getDocument()));
    }
    if (dto.getComments() != null && !dto.getComments().isEmpty()) {
      DigitalCalibrationCertificateType.Comment comment = objectFactory.createDigitalCalibrationCertificateTypeComment();
      comment.getAny().addAll(dto.getComments());
      target.setComment(comment);
    }
    if (dto.getSignatures() != null && !dto.getSignatures().isEmpty() && isNotEmpty(dto.getSignatures().getFirst())) {
      target.getSignature().addAll(dto.getSignatures().stream()
          .map(signatureMapper::mapToJaxbObject)
          .toList());
    }
    target.setSchemaVersion(StringUtils.isNotBlank(dto.getSchemaVersion()) ? dto.getSchemaVersion() : DEFAULT_SCHEMA_VERSION);
    return target;
  }

  public JAXBElement<DigitalCalibrationCertificateType> mapToJAXBElement(@Nonnull CalibrationCertificateDto dto) {
    QName qName = new QName(DccServiceUtil.NS_DCC, "digitalCalibrationCertificate");
    return new JAXBElement<>(qName, DigitalCalibrationCertificateType.class, mapToJaxbObject(dto));
  }
}

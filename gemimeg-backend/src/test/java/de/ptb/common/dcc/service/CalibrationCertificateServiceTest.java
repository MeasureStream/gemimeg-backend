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
package de.ptb.common.dcc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ptb.common.dcc.api.v1.CalibrationCertificateBuilder;
import de.ptb.common.dcc.api.v1.dcc.CalibrationCertificateDto;
import de.ptb.common.dcc.api.v1.dcc.SignatureDto;
import de.ptb.common.dcc.config.CalibrationCertificateConfiguration;
import de.ptb.common.dcc.data.CalibrationCertificateRepository;
import de.ptb.common.dcc.mapper.CalibrationCertificateMapper;
import de.ptb.common.dcc.model.CalibrationCertificate;
import jakarta.xml.bind.JAXBException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static de.ptb.common.dcc.api.v1.CalibrationCertificateConstants.DEFAULT_SCHEMA_VERSION;
import static de.ptb.common.dcc.service.CalibrationCertificateService.SELF_SIGNED_ID_PREFIX;
import static de.ptb.common.dcc.service.CalibrationCertificateService.SELF_SIGNING_HASH_ALGORITHM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CalibrationCertificateServiceTest {

  @MockitoBean
  private CalibrationCertificateConfiguration configuration;

  @MockitoBean
  private CalibrationCertificateRepository repository;

  @Autowired
  private CalibrationCertificateMapper calibrationCertificateMapper;

  @Autowired
  private ObjectMapper objectMapper;

  @Captor
  private ArgumentCaptor<Date> dateCaptor;

  private CalibrationCertificateService service;

  private CalibrationCertificateDto calibrationCertificateDto;
  private CalibrationCertificate calibrationCertificate;

  @BeforeEach
  void setUp() throws JAXBException, JsonProcessingException, DatatypeConfigurationException {
    when(configuration.getNamespaceUri()).thenReturn("https://ptb.de/dcc");
    when(configuration.getPersistEnabled()).thenReturn(false);
    when(configuration.getPersistLifespan()).thenReturn(600);
    service = new CalibrationCertificateService(configuration, calibrationCertificateMapper,
        objectMapper, repository);
    createDigitalCalibrationCertificate();
  }

  @Test
  void convert_Ok() throws JAXBException, IOException, SAXException {
    when(repository.save(any())).thenReturn(calibrationCertificate);
    String actual = service.validate(service.convert(calibrationCertificateDto));
    assertNotNull(actual);
    assertFalse(StringUtils.isEmpty(actual));
  }

  @Test
  void validateAndConvert_Ok() throws IOException, JAXBException, SAXException {
    when(repository.save(any())).thenReturn(calibrationCertificate);
    String xml = IOUtils.toString(Objects.requireNonNull(this.getClass()
        .getResourceAsStream("/examples/test-dcc.xml")), StandardCharsets.UTF_8);
    CalibrationCertificateDto actual = service.validateAndConvert(xml);
    assertNotNull(actual);
  }

  @Test
  void validateAndProduceHtml_Ok() throws JAXBException, IOException, TransformerException, SAXException {
    when(repository.save(any())).thenReturn(calibrationCertificate);
    String actual = service.validateAndProduceHtml(calibrationCertificateDto);
    assertNotNull(actual);
  }

  @Test
  void selfSign_Ok() throws NoSuchAlgorithmException, JsonProcessingException {
    assertNull(calibrationCertificateDto.getSignatures());
    CalibrationCertificateDto actual = service.selfSign(calibrationCertificateDto);
    assertNotNull(actual.getSignatures());
    assertEquals(1, actual.getSignatures().size());
    SignatureDto actualSignature = actual.getSignatures().getFirst();
    assertNotNull(actualSignature);
    assertEquals(SELF_SIGNED_ID_PREFIX, actualSignature.getId());
    assertNotNull(actualSignature.getValue());
    assertNotNull(actualSignature.getValue().getValue());
    assertNotNull(actualSignature.getSignedInfo());
    assertEquals(SELF_SIGNING_HASH_ALGORITHM, actualSignature.getSignedInfo().getMethodAlgorithm());
  }

  @Test
  void deleteExpiredCertificates_Ok() throws NoSuchMethodException {
    when(repository.findByCreatedAtLessThan(any(Date.class))).thenReturn(List.of(calibrationCertificate));
    Method deleteExpiredCertificates = service.getClass().getMethod("deleteExpiredCertificates");
    assertEquals("0 */5 * ? * *", deleteExpiredCertificates.getAnnotation(Scheduled.class).cron());
    Date now = new Date(System.currentTimeMillis());
    service.deleteExpiredCertificates();
    verify(repository).findByCreatedAtLessThan(dateCaptor.capture());
    assertEquals((now.getTime() - configuration.getPersistLifespan() * 1000L) / 100L,
        dateCaptor.getValue().getTime() / 100L);
    verify(repository).delete(calibrationCertificate);
    verifyNoMoreInteractions(repository);
  }

  private void createDigitalCalibrationCertificate() throws JsonProcessingException, DatatypeConfigurationException {
    calibrationCertificateDto = CalibrationCertificateBuilder.getInstance()
        .withCertificateCreationSoftware("GEMIMEG Tool powered by OP-Layer", "1.0.0")
        .withReceiptDate(LocalDate.now())
        .withBeginDate(LocalDate.now().plusDays(1))
        .withEndDate(LocalDate.now().plusDays(2))
        .withLaboratoryName("PTB-9.4")
        .withResponsiblePerson("Verantwortlicher")
        .withItem("Gegenstand", "Hersteller")
        .withLaboratoryContact("Toni Tester", "toni.tester@ptb.de", "+49303481-9876",
            "DE", "BE", "10587", "Berlin", "Abbestr.", "2-12")
        .withManufacturerName("Pfreundt GmbH")
        .withDeviceName("RAD-8620")
        .withDeviceType("Waage")
        .withDeviceDescription("Eine tolle Beschreibung")
        .withCustomer("Kunibert Kunde", "kunibert.kunde@mail.de", "+4930123456",
            "DE", "BE", "10627", "Berlin", "Testgasse", "123")
        .withMeasurementResult("NI LabView", "2021 SP1", "Weighing_for_Calibration 0..1 kgs", 0.01, "kg")
        .withMeasurementResult("NI LabView", "2021 SP1", "Weighing_for_Calibration 0..1 kgs", 0.05, "kg")
        .withMeasurementResult("NI LabView", "2021 SP1", "Weighing_for_Calibration 0..1 kgs", 0.1, "kg")
        .withMeasurementResult("NI LabView", "2021 SP1", "Weighing_for_Calibration 0..1 kgs", 0.5, "kg")
        .withMeasurementResult("NI LabView", "2021 SP1", "Weighing_for_Calibration 0..1 kgs", 1.0, "kg")
        .withSchemaVersion(DEFAULT_SCHEMA_VERSION)
        .build();
    calibrationCertificateDto.getMeasurementResults()
        .getFirst().getEquipment()
        .getFirst().setClassReference("test");
    calibrationCertificate = new CalibrationCertificate();
    calibrationCertificate.setId(UUID.randomUUID().toString());
    calibrationCertificate.setDccJson(objectMapper.writeValueAsString(calibrationCertificateDto));
  }
}
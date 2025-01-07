package de.ptb.common.dcc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ptb.common.dcc.TestConfiguration;
import de.ptb.common.dcc.api.v1.CalibrationCertificateBuilder;
import de.ptb.common.dcc.api.v1.dto.CalibrationCertificateDto;
import de.ptb.common.dcc.config.CalibrationCertificateConfiguration;
import de.ptb.common.dcc.service.CalibrationCertificateService;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Objects;

import static de.ptb.common.dcc.api.v1.CalibrationCertificateConstants.DEFAULT_CHARSET;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_HTML_PATH;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_JSON_PATH;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_XML_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = {CalibrationCertificateController.class})
@ContextConfiguration(classes = {TestConfiguration.class})
class CalibrationCertificateControllerTest {

  private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
  private static final MediaType APPLICATION_XML_UTF8 = new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8);

  private static String testDccXml;
  private static String testDccHtml;

  @MockBean
  private CalibrationCertificateConfiguration calibrationCertificateConfiguration;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CalibrationCertificateService service;

  private MockMvc mockMvc;

  private CalibrationCertificateDto dcc;

  @Captor
  private ArgumentCaptor<CalibrationCertificateDto> dccCaptor;

  @BeforeAll
  static void setUpBeforeAll() throws IOException {
    testDccXml = IOUtils.toString(Objects.requireNonNull(CalibrationCertificateControllerTest.class
        .getResourceAsStream("/examples/test-dcc.xml")), StandardCharsets.UTF_8);
    testDccHtml = IOUtils.toString(Objects.requireNonNull(CalibrationCertificateControllerTest.class
        .getResourceAsStream("/examples/test-dcc.html")), StandardCharsets.UTF_8);
  }

  @BeforeEach
  void setUpBeforeEach() throws JAXBException, IOException, SAXException, TransformerException,
      DatatypeConfigurationException {
    when(calibrationCertificateConfiguration.getNamespaceUri()).thenReturn("https://ptb.de/dcc");
    dcc = createDcc();
    when(service.convert(any(CalibrationCertificateDto.class))).thenReturn(testDccXml);
    when(service.validate(testDccXml)).thenReturn(testDccXml);
    when(service.validateAndConvert(testDccXml)).thenReturn(dcc);
    when(service.validateAndConvert(testDccXml, DEFAULT_CHARSET)).thenReturn(dcc);
    when(service.validateAndProduceHtml(any(CalibrationCertificateDto.class))).thenReturn(testDccHtml);
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();//apply(springSecurity()).build();
  }

  // FIXME: Jakarta Servlet API 5.0 vs 6.0 with Spring Boot 3 Issue. See https://op.ptb.de/documents/83.
  @Test
  @Disabled("Jakarta Servlet API 5.0 vs 6.0 with Spring Boot 3 Issue. See https://op.ptb.de/documents/83")
  @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
  void convertAndValidate_Ok() throws Exception {
    mockMvc.perform(post(DCC_XML_PATH)
            .contentType(APPLICATION_JSON_UTF8)
            .characterEncoding(DEFAULT_CHARSET)
            .accept(APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(dcc)))
        .andExpect(status().isOk())
        .andExpect(content().xml(testDccXml));
    verify(service).convert(dccCaptor.capture());
    verify(service).validate(testDccXml);
    JSONAssert.assertEquals(
        objectMapper.writeValueAsString(dcc),
        objectMapper.writeValueAsString(dccCaptor.getValue()), true);
  }

  @Test
  @Disabled("Jakarta Servlet API 5.0 vs 6.0 with Spring Boot 3 Issue. See https://op.ptb.de/documents/83")
  @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
  void validateAndConvert_Ok() throws Exception {
    mockMvc.perform(post(DCC_JSON_PATH)
            .contentType(APPLICATION_XML_UTF8)
            .characterEncoding(DEFAULT_CHARSET)
            .accept(APPLICATION_XML_UTF8)
            .content(testDccXml))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON_UTF8))
        .andExpect(content().json(objectMapper.writeValueAsString(dcc)));
    verify(service).validateAndConvert(testDccXml);
  }

  @Test
  @Disabled("Jakarta Servlet API 5.0 vs 6.0 with Spring Boot 3 Issue. See https://op.ptb.de/documents/83")
  @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
  void validateAndProduceHtml_Ok() throws Exception {
    mockMvc.perform(post(DCC_HTML_PATH)
            .contentType(APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(dcc)))
        .andExpect(status().isOk())
        .andExpect(content().string(testDccHtml));
    verify(service).validateAndProduceHtml(dccCaptor.capture());
    JSONAssert.assertEquals(
        objectMapper.writeValueAsString(dcc),
        objectMapper.writeValueAsString(dccCaptor.getValue()), true);
  }

  private CalibrationCertificateDto createDcc() throws DatatypeConfigurationException, JsonProcessingException {
    CalibrationCertificateDto result = CalibrationCertificateBuilder.getInstance()
        .withCertificateCreationSoftware("GEMIMEG Tool powered by OP-Layer", "1.0.0")
        .withReceiptDate(LocalDate.now())
        .withBeginDate(LocalDate.now().plusDays(1))
        .withEndDate(LocalDate.now().plusDays(2))
        .withLaboratoryName("PTB-9.4")
        .withResponsiblePerson("Verantwortlicher")
        .withLaboratoryContact("Toni Tester", "toni.tester@ptb.de", "+49303481-9876",
            "DE", "BE", "10587", "Berlin", "Abbestr.", "2-12")
        .withManufacturerName("Pfreundt GmbH")
        .withDeviceName("RAD-8620")
        .withDeviceType("Waage")
        .withDeviceDescription("Eine tolle Beschreibung")
        .withCustomer("Kunibert Kunde", "kunibert.kunde@mail.de", "+4930123456",
            "DE", "BE", "10627", "Berlin", "Testgasse", "123")
        .withMeasurementResult("NI LabView", "2021 SP1",
            "Weighing_for_Calibration 0..1 kgs", 0.01, "kg")
        .withMeasurementResult("NI LabView", "2021 SP1",
            "Weighing_for_Calibration 0..1 kgs", 0.05, "kg")
        .withMeasurementResult("NI LabView", "2021 SP1",
            "Weighing_for_Calibration 0..1 kgs", 0.1, "kg")
        .withMeasurementResult("NI LabView", "2021 SP1",
            "Weighing_for_Calibration 0..1 kgs", 0.5, "kg")
        .withMeasurementResult("NI LabView", "2021 SP1",
            "Weighing_for_Calibration 0..1 kgs", 1.0, "kg")
        .withSchemaVersion("3.2.1")
        .build();
    result.getMeasurementResults().getFirst()
        .getEquipment().getFirst()
        .setClassReference("test");
    return result;
  }
}
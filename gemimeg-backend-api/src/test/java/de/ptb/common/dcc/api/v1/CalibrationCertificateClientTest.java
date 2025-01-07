package de.ptb.common.dcc.api.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ptb.common.dcc.api.v1.dto.CalibrationCertificateDto;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Objects;

import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_HTML_PATH;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_JSON_PATH;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_XML_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(SpringExtension.class)
@RestClientTest(CalibrationCertificateClient.class)
@AutoConfigureWebClient(registerRestTemplate = true)
public class CalibrationCertificateClientTest {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private MockRestServiceServer server;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ICalibrationCertificateClientConfiguration clientConfiguration;

  private CalibrationCertificateClient client;
  private CalibrationCertificateDto dcc;

  @BeforeEach
  void setUp() throws DatatypeConfigurationException, JsonProcessingException {
    client = new CalibrationCertificateClient(restTemplate, clientConfiguration);
    createDigitalCalibrationCertificate();
  }

  @Test
  void convertAndValidate_Ok() throws IOException {
    String url = clientConfiguration.getBaseUrl() + DCC_XML_PATH;
    String xml = IOUtils.toString(Objects.requireNonNull(this.getClass()
        .getResourceAsStream("/examples/test-dcc.xml")), StandardCharsets.UTF_8);
    server.expect(requestTo(url))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().json(objectMapper.writeValueAsString(dcc)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andRespond(withSuccess(xml, MediaType.APPLICATION_XML));
    String actual = client.convertAndValidate(dcc);
    assertTrue(StringUtils.equalsIgnoreCase(xml, actual));
    server.verify();

  }

  @Test
  void validateAndConvert_Ok() throws IOException {
    String url = clientConfiguration.getBaseUrl() + DCC_JSON_PATH;
    String xml = IOUtils.toString(Objects.requireNonNull(this.getClass()
        .getResourceAsStream("/examples/test-dcc.xml")), StandardCharsets.UTF_8);
    server.expect(requestTo(url))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().string(xml))
        .andExpect(content().contentType(MediaType.APPLICATION_XML))
        .andRespond(withSuccess(objectMapper.writeValueAsString(dcc),
            MediaType.APPLICATION_JSON));
    CalibrationCertificateDto actual = client.validateAndConvert(xml);
    server.verify();
    assertEquals(dcc.getAdministrativeData().getCountryCode(),
        actual.getAdministrativeData().getCountryCode());
    assertEquals(dcc.getAdministrativeData().getUniqueIdentifier(),
        actual.getAdministrativeData().getUniqueIdentifier());
    assertNotNull(actual.getAdministrativeData().getReceiptDate());
  }

  @Test
  void validateAndProduceHtml_Ok() throws IOException {
    String url = clientConfiguration.getBaseUrl() + DCC_HTML_PATH;
    String html = IOUtils.toString(Objects.requireNonNull(this.getClass()
        .getResourceAsStream("/examples/test-dcc.html")), StandardCharsets.UTF_8);
    server.expect(requestTo(url))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().json(objectMapper.writeValueAsString(dcc)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andRespond(withSuccess(html, MediaType.TEXT_HTML));
    String actual = client.validateAndProduceHtml(dcc);
    assertTrue(StringUtils.equalsIgnoreCase(html, actual));
    server.verify();
  }

  private void createDigitalCalibrationCertificate() throws DatatypeConfigurationException, JsonProcessingException {
    dcc = CalibrationCertificateBuilder.getInstance()
        .withCertificateCreationSoftware("gemimeg-backend-api-test", "1.0.0")
        .withReceiptDate(LocalDate.now())
        .withBeginDate(LocalDate.now().plusDays(1))
        .withEndDate(LocalDate.now().plusDays(2))
        .withLaboratoryName("PTB-9.4")
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
        .withSchemaVersion("3.1.2")
        .build();
  }
}
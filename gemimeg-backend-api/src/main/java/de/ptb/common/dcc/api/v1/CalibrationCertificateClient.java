package de.ptb.common.dcc.api.v1;

import de.ptb.common.dcc.api.v1.dcc.CalibrationCertificateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;

import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_HTML_PATH;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_JSON_PATH;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_SELF_SIGN_PATH;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_XML_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;

@Component
public class CalibrationCertificateClient {

  private final RestTemplate restTemplate;
  private final ICalibrationCertificateClientConfiguration clientConfiguration;

  @Autowired
  public CalibrationCertificateClient(RestTemplate restTemplate,
                                      ICalibrationCertificateClientConfiguration clientConfiguration) {
    this.restTemplate = restTemplate;
    this.clientConfiguration = clientConfiguration;
  }

  public String convertAndValidate(@Nonnull CalibrationCertificateDto dcc) {
    String url = clientConfiguration.getBaseUrl() + DCC_XML_PATH;
    return restTemplate.postForObject(url, dcc, String.class);
  }

  public CalibrationCertificateDto validateAndConvert(@Nonnull String xml) {
    String url = clientConfiguration.getBaseUrl() + DCC_JSON_PATH;
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_XML);
    return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(xml, headers),
        CalibrationCertificateDto.class).getBody();
  }

  public CalibrationCertificateDto selfSign(@Nonnull CalibrationCertificateDto dcc) {
    String url = clientConfiguration.getBaseUrl() + DCC_SELF_SIGN_PATH;
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_JSON);
    return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(dcc, headers),
        CalibrationCertificateDto.class).getBody();
  }

  public String validateAndProduceHtml(@Nonnull CalibrationCertificateDto dcc) {
    String url = clientConfiguration.getBaseUrl() + DCC_HTML_PATH;
    return restTemplate.postForObject(url, dcc, String.class);
  }
}

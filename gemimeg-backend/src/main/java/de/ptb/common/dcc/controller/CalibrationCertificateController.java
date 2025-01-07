package de.ptb.common.dcc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.ptb.common.dcc.api.v1.dto.CalibrationCertificateDto;
import de.ptb.common.dcc.service.CalibrationCertificateService;
import de.ptb.common.http.BadRequestStatus;
import de.ptb.common.http.NotFoundStatus;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;

import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_HTML_PATH;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_JSON_PATH;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_PATH_ID;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_SELF_SIGN_PATH;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_XML_PATH;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.DCC_XML_PATH_ID;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateControllerRoutes.ID_PARAM_NAME;
import static de.ptb.common.dcc.service.CalibrationCertificateService.SELF_SIGNING_HASH_ALGORITHM;
import static de.ptb.common.dcc.util.DccServiceUtil.createLogEntry;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@Slf4j
@RestController
@Tag(name = "CalibrationCertificateController")
@OpenAPIDefinition(servers = {
    @Server(url = "https://oplayer.berlin.ptb.de", description = "production system"),
    @Server(url = "https://staging-oplayer.berlin.ptb.de", description = "staging system"),
    @Server(url = "https://test-oplayer.berlin.ptb.de", description = "testing system"),
    @Server(url = "http://localhost:10013", description = "local testing api")
})
public class CalibrationCertificateController {

  private final CalibrationCertificateService service;

  @Autowired
  public CalibrationCertificateController(CalibrationCertificateService service) {
    this.service = service;
  }

  @Operation(description = "Convert a DCC DTO as JSON to XML")
  @PostMapping(path = DCC_XML_PATH, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_XML_VALUE)
  public String convertAndValidate(@RequestBody CalibrationCertificateDto dcc) {
    String xml = null;
    try {
      xml = service.convert(dcc);
      return service.validate(xml);
    } catch (JAXBException | SAXException | IOException e) {
      throw new BadRequestStatus(getDetailedXmlError(xml, e)) {
      };
    }
  }

  @Operation(description = "Convert a valid DCC XML to JSON")
  @PostMapping(path = DCC_JSON_PATH, consumes = APPLICATION_XML_VALUE, produces = APPLICATION_JSON_VALUE)
  public CalibrationCertificateDto validateAndConvert(@RequestBody String xml) {
    try {
      return service.validateAndConvert(xml);
    } catch (JAXBException | SAXException | MalformedURLException e) {
      throw new BadRequestStatus(getDetailedXmlError(xml, e));
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
    }
  }

  @Operation(description = "Self-signs a CalibrationCertificate (JSON) using " + SELF_SIGNING_HASH_ALGORITHM)
  @PostMapping(path = DCC_SELF_SIGN_PATH, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public CalibrationCertificateDto selfSign(@RequestBody CalibrationCertificateDto dcc) {
    try {
      return service.selfSign(dcc);
    } catch (NoSuchAlgorithmException | JsonProcessingException e) {
      throw new BadRequestStatus(createLogEntry(e));
    }
  }

  @Operation(description = "Convert a DCC DTO as JSON to a human-readable HTML page")
  @PostMapping(path = DCC_HTML_PATH, consumes = APPLICATION_JSON_VALUE, produces = TEXT_HTML_VALUE)
  public String validateAndProduceHtml(@RequestBody CalibrationCertificateDto dcc) {
    try {
      return service.validateAndProduceHtml(dcc);
    } catch (JAXBException | SAXException | TransformerException | IOException | IllegalArgumentException e) {
      throw new BadRequestStatus(createLogEntry(e));
    }
  }

  @Operation(description = "Retrieves a DCC as JSON from the internal storage by its ID.")
  @GetMapping(path = DCC_PATH_ID, produces = APPLICATION_JSON_VALUE)
  public CalibrationCertificateDto findById(@PathVariable(name = ID_PARAM_NAME) String id) {
    return service.findById(id)
        .orElseThrow(() -> new NotFoundStatus(CalibrationCertificateDto.class, id));
  }

  @Operation(description = "Retrieves a DCC as XML from the internal storage by its ID.")
  @GetMapping(path = DCC_XML_PATH_ID, produces = APPLICATION_XML_VALUE)
  public String findByIdAsXml(@PathVariable(name = ID_PARAM_NAME) String id) {
    try {
      return service.convert(findById(id));
    } catch (JAXBException | IOException e) {
      throw new BadRequestStatus(createLogEntry(e));
    }
  }

  private static String getDetailedXmlError(String xml, Exception e) {
    StringBuilder details = new StringBuilder(createLogEntry(e));
    if (e instanceof JAXBException && (((JAXBException) e).getLinkedException() instanceof SAXParseException)) {
      int line = ((SAXParseException) ((JAXBException) e).getLinkedException()).getLineNumber();
      details.append(" Invalid xml: (line ").append(line).append(") :").append(System.lineSeparator());
      Object[] xmlLines = xml.lines().toArray();
      for (int i = Math.max(0, line - 3); i <= Math.min(xmlLines.length - 1, line + 3); i++) {
        details.append(i).append(": ").append(xmlLines[i].toString()).append(System.lineSeparator());
      }
    }
    String detailsMessage = details.toString();
    log.error("Problem detected with provided XML: " + detailsMessage);
    log.error("Problematic XML: " + xml);
    return detailsMessage;
  }
}

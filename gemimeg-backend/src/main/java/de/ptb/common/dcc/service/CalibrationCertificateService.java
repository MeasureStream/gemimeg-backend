package de.ptb.common.dcc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ptb.common.dcc.api.v1.dto.AddSignatureDto;
import de.ptb.common.dcc.api.v1.dto.AdministrativeDataDto;
import de.ptb.common.dcc.api.v1.dto.CalibrationCertificateDto;
import de.ptb.common.dcc.api.v1.dto.LanguageSpecificStringsDto;
import de.ptb.common.dcc.api.v1.dto.SignatureDto;
import de.ptb.common.dcc.api.v1.dto.SignatureListDto;
import de.ptb.common.dcc.api.v1.dto.SoftwareDto;
import de.ptb.common.dcc.api.v1.dto.SoftwareListDto;
import de.ptb.common.dcc.data.CalibrationCertificateRepository;
import de.ptb.common.dcc.mapper.CalibrationCertificateMapper;
import de.ptb.common.dcc.model.CalibrationCertificate;
import de.ptb.common.dcc.util.DccServiceUtil;
import de.ptb.common.dcc.xjc.generated.DigitalCalibrationCertificateType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static de.ptb.common.dcc.api.v1.CalibrationCertificateConstants.DEFAULT_CHARSET;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateConstants.LOCAL_DCC_XSD_PATH;
import static de.ptb.common.dcc.util.DccServiceUtil.NS_PREFIX_MAPPER;

@Slf4j
@Service
public class CalibrationCertificateService {

  public static final String SELF_SIGNING_HASH_ALGORITHM = "SHA-256";
  public static final String SELF_SIGNED_ID_PREFIX = "self-signed_DCC";
  private static final String DCC_XSL_PATH = "/xsl/dcc/dcc.xsl";
  private static String BLANK_HTML_PAGE;

  static {
    try {
      BLANK_HTML_PAGE = IOUtils.resourceToString("/html/blank.html", StandardCharsets.UTF_8);
    } catch (IOException e) {
      log.warn(e.getMessage());
    }
  }

  private final JAXBContext context;
  private final CalibrationCertificateMapper calibrationCertificateMapper;
  private final ObjectMapper objectMapper;
  private final CalibrationCertificateRepository dccRepository;

  @Autowired
  public CalibrationCertificateService(CalibrationCertificateMapper calibrationCertificateMapper,
                                       ObjectMapper objectMapper,
                                       CalibrationCertificateRepository dccRepository) throws JAXBException {
    context = JAXBContext.newInstance(DigitalCalibrationCertificateType.class);
    this.calibrationCertificateMapper = calibrationCertificateMapper;
    this.objectMapper = objectMapper;
    this.dccRepository = dccRepository;
  }

  @Nonnull
  public String convert(@Nonnull CalibrationCertificateDto dcc)
      throws JAXBException, IOException {
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.setProperty(Marshaller.JAXB_ENCODING, DEFAULT_CHARSET.toString());
    marshaller.setProperty(NS_PREFIX_MAPPER, DccServiceUtil.getNamespacePrefixMapper());
    amendSoftwareInfo(dcc);
    save(dcc);
    try (Writer xmlWriter = new StringWriter()) {
      log.info("Provided JSON for conversion to XML: " + objectMapper.writeValueAsString(dcc));
      marshaller.marshal(calibrationCertificateMapper.mapToJAXBElement(dcc), xmlWriter);
      String resultingXml = xmlWriter.toString();
      log.info("Generated XML: " + resultingXml);
      return resultingXml;
    }
  }

  @Nonnull
  public CalibrationCertificateDto validateAndConvert(@Nonnull String xml, Charset charset)
      throws JAXBException, SAXException, IOException {
    Unmarshaller unmarshaller = createUnmarshaller();
    //noinspection unchecked
    CalibrationCertificateDto result = calibrationCertificateMapper
        .mapToDto(((JAXBElement<DigitalCalibrationCertificateType>) unmarshaller.unmarshal(IOUtils
            .toInputStream(xml, charset))).getValue());
    amendSoftwareInfo(result);
    save(result);
    return result;
  }

  public CalibrationCertificateDto validateAndConvert(@Nonnull String xml)
      throws JAXBException, SAXException, IOException {
    return validateAndConvert(xml, DEFAULT_CHARSET);
  }

  @Nonnull
  public String validateAndProduceHtml(@Nonnull CalibrationCertificateDto dcc)
      throws JAXBException, IOException, SAXException, TransformerException {
    String xml = validate(convert(dcc));
    if (StringUtils.isNotBlank(xml)) {
      StreamSource xmlResource = new StreamSource(IOUtils.toInputStream(xml, DEFAULT_CHARSET));
      StringWriter writer = new StringWriter();
      StreamResult xmlTransformResult = new StreamResult(writer);
      try (InputStream xslInputStream = this.getClass().getResourceAsStream(DCC_XSL_PATH)) {
        StreamSource xslResource = new StreamSource(xslInputStream);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xslResource);
        transformer.transform(xmlResource, xmlTransformResult);
        return writer.getBuffer().toString();
      }
    } else {
      return BLANK_HTML_PAGE;
    }
  }

  @Nonnull
  public CalibrationCertificateDto addSignature(@Nonnull AddSignatureDto addSignature) {
    CalibrationCertificateDto result = addSignature.getOriginalDto();
    SignatureListDto signatureList = result.getSignatures();
    if (signatureList == null) {
      signatureList = new SignatureListDto();
    }
    signatureList.add(addSignature.getSignatureDto());
    result.setSignatures(signatureList);
    return result;
  }

  @Nonnull
  public CalibrationCertificateDto selfSign(@Nonnull CalibrationCertificateDto dto) throws NoSuchAlgorithmException,
      JsonProcessingException {
    SignatureDto signature = new SignatureDto();
    signature.setId(SELF_SIGNED_ID_PREFIX);
    SignatureDto.SignatureValue signatureValue = new SignatureDto.SignatureValue();
    signatureValue.setId(SELF_SIGNED_ID_PREFIX + "_value");
    MessageDigest digest = MessageDigest.getInstance(SELF_SIGNING_HASH_ALGORITHM);
    signatureValue.setValue(digest.digest(objectMapper.writeValueAsBytes(dto)));
    signature.setValue(signatureValue);
    SignatureDto.SignedInfo signedInfo = new SignatureDto.SignedInfo();
    signedInfo.setId(SELF_SIGNED_ID_PREFIX + "_info");
    signedInfo.setMethodAlgorithm(SELF_SIGNING_HASH_ALGORITHM);
    signature.setSignedInfo(signedInfo);
    SignatureListDto signatureList = dto.getSignatures();
    if (signatureList == null) {
      signatureList = new SignatureListDto();
    }
    signatureList.add(signature);
    dto.setSignatures(signatureList);
    return dto;
  }

  @Nullable
  public String validate(@Nullable String xml) throws JAXBException, SAXException, IOException {
    if (StringUtils.isNotBlank(xml)) {
      Unmarshaller unmarshaller = createUnmarshaller();
      unmarshaller.unmarshal(IOUtils.toInputStream(xml, DEFAULT_CHARSET));
    }
    return xml;
  }

  @Nonnull
  public Optional<CalibrationCertificateDto> findById(@Nonnull String id) {
    return dccRepository.findById(id)
        .map(CalibrationCertificate::getDccJson)
        .map(this::extractDto);
  }

  @Nonnull
  private Unmarshaller createUnmarshaller() throws JAXBException, SAXException, IOException {
    Unmarshaller unmarshaller = context.createUnmarshaller();
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    URL schemaUrl = IOUtils.resourceToURL(LOCAL_DCC_XSD_PATH);
    Schema dccSchema = schemaFactory.newSchema(schemaUrl);
    unmarshaller.setSchema(dccSchema);
    return unmarshaller;
  }

  private void amendSoftwareInfo(@Nonnull CalibrationCertificateDto dcc) {
    if (dcc.getAdministrativeData() == null) {
      dcc.setAdministrativeData(new AdministrativeDataDto());
    }
    if (dcc.getAdministrativeData().getDccSoftware() == null) {
      dcc.getAdministrativeData().setDccSoftware(new SoftwareListDto());
    }
    if (dcc.getAdministrativeData().getDccSoftware().isEmpty()) {
      SoftwareDto software = new SoftwareDto();
      LanguageSpecificStringsDto languageSpecificStrings = new LanguageSpecificStringsDto();
      software.setName(languageSpecificStrings);
      dcc.getAdministrativeData().getDccSoftware().add(software);
    }
  }

  private void save(@Nonnull CalibrationCertificateDto dcc) {
    String uniqueIdentifierOrId = null;
    if (dcc.getAdministrativeData() != null && StringUtils.isNotBlank(dcc.getAdministrativeData().getUniqueIdentifier())) {
      uniqueIdentifierOrId = dcc.getAdministrativeData().getUniqueIdentifier();
    }
    if (StringUtils.isNotBlank(dcc.getId())) {
      uniqueIdentifierOrId = dcc.getId();
    }
    if (StringUtils.isNotBlank(uniqueIdentifierOrId)) {
      CalibrationCertificate entity = new CalibrationCertificate();
      entity.setId(uniqueIdentifierOrId);
      try {
        entity.setDccJson(objectMapper.writeValueAsString(dcc));
        log.info("Given DCC stored successfully with ID: " + dccRepository.saveAndFlush(entity).getId());
      } catch (JsonProcessingException e) {
        log.error(e.getMessage());
      }
    } else {
      log.warn("Given DCC cannot be stored, because it doesn't have either an ID, or a unique identifier.");
    }
  }

  @Nullable
  private CalibrationCertificateDto extractDto(@Nonnull String json) {
    try {
      return objectMapper.readValue(json, CalibrationCertificateDto.class);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
    }
    return null;
  }
}

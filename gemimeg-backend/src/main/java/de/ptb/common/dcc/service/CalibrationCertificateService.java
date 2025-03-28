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
import de.ptb.common.dcc.api.v1.dcc.AdministrativeDataDto;
import de.ptb.common.dcc.api.v1.dcc.CalibrationCertificateDto;
import de.ptb.common.dcc.api.v1.dcc.LanguageSpecificStringsDto;
import de.ptb.common.dcc.api.v1.dcc.SignatureDto;
import de.ptb.common.dcc.api.v1.dcc.SignatureListDto;
import de.ptb.common.dcc.api.v1.dcc.SoftwareDto;
import de.ptb.common.dcc.api.v1.dcc.SoftwareListDto;
import de.ptb.common.dcc.config.CalibrationCertificateConfiguration;
import de.ptb.common.dcc.data.CalibrationCertificateRepository;
import de.ptb.common.dcc.mapper.CalibrationCertificateMapper;
import de.ptb.common.dcc.model.CalibrationCertificate;
import de.ptb.common.dcc.util.DccServiceUtil;
import de.ptb.common.dcc.xjc.generated.DigitalCalibrationCertificateType;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.util.Date;
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

  private final CalibrationCertificateConfiguration configuration;
  private final JAXBContext context;
  private final CalibrationCertificateMapper calibrationCertificateMapper;
  private final ObjectMapper objectMapper;
  private final CalibrationCertificateRepository repository;

  @Autowired
  public CalibrationCertificateService(CalibrationCertificateConfiguration configuration,
                                       CalibrationCertificateMapper calibrationCertificateMapper,
                                       ObjectMapper objectMapper,
                                       CalibrationCertificateRepository repository) throws JAXBException {
    this.configuration = configuration;
    context = JAXBContext.newInstance(DigitalCalibrationCertificateType.class);
    this.calibrationCertificateMapper = calibrationCertificateMapper;
    this.objectMapper = objectMapper;
    this.repository = repository;
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
      marshaller.marshal(calibrationCertificateMapper.mapToJAXBElement(dcc), xmlWriter);
      return xmlWriter.toString();
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
    return repository.findById(id)
        .map(CalibrationCertificate::getDccJson)
        .map(this::extractDto);
  }

  @Scheduled(cron = "0 */5 * ? * *")
  public void deleteExpiredCertificates() {
    final long milliseconds = configuration.getPersistLifespan().longValue() * 1000L;
    final Date expirationDate = new Date(System.currentTimeMillis() - milliseconds);
    repository.findByCreatedAtLessThan(expirationDate)
        .forEach(repository::delete);
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

  @Transactional
  private void save(@Nonnull CalibrationCertificateDto dcc) {
    if (configuration.getPersistEnabled()) {
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
        entity.setCreatedAt(new Date(System.currentTimeMillis()));
        try {
          entity.setDccJson(objectMapper.writeValueAsString(dcc));
          repository.save(entity);
        } catch (JsonProcessingException e) {
          log.error(e.getMessage());
        }
      } else {
        log.warn("DCC cannot be stored, because it doesn't have either an ID, or a unique identifier.");
      }
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

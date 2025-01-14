/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ptb.common.dcc.api.v1.dcc.CalibrationCertificateDto;
import de.ptb.common.dcc.util.DccServiceUtil;
import de.ptb.common.dcc.xjc.generated.DigitalCalibrationCertificateType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static de.ptb.common.dcc.api.v1.CalibrationCertificateConstants.DEFAULT_CHARSET;
import static de.ptb.common.dcc.api.v1.CalibrationCertificateConstants.LOCAL_DCC_XSD_PATH;
import static de.ptb.common.dcc.api.v1.json.SerializationUtils.createObjectMapper;
import static de.ptb.common.dcc.util.DccServiceUtil.NS_PREFIX_MAPPER;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
public class DccGoodPracticeRoundTripTest {

  @Autowired
  private CalibrationCertificateMapper calibrationCertificateMapper;

  @Test
  public void roundTrip_AllAvailableGoodPractices_Ok() throws URISyntaxException, IOException, JAXBException, SAXException {

    // prepare
    JAXBContext context = JAXBContext.newInstance(DigitalCalibrationCertificateType.class);
    Unmarshaller unmarshaller = createUnmarshaller(context);
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.setProperty(Marshaller.JAXB_ENCODING, DEFAULT_CHARSET.toString());
    marshaller.setProperty(NS_PREFIX_MAPPER, DccServiceUtil.getNamespacePrefixMapper());
    ObjectMapper objectMapper = createObjectMapper();
    Set<String> messages = new HashSet<>();

    // test
    availableExampleXmlFiles().forEach(file -> {
      try (Writer xmlWriter = new StringWriter()) {
        String xmlOrig = Files.readString(file.toPath());
        //noinspection unchecked
        JAXBElement<DigitalCalibrationCertificateType> unmarshalledFile =
            (JAXBElement<DigitalCalibrationCertificateType>) unmarshaller.unmarshal(new StringReader(xmlOrig));
        CalibrationCertificateDto calibrationCertificateDto =
            calibrationCertificateMapper.mapToDto(unmarshalledFile.getValue());
        String json = objectMapper.writeValueAsString(calibrationCertificateDto);
        calibrationCertificateDto = objectMapper.readValue(new StringReader(json), CalibrationCertificateDto.class);
        JAXBElement<?> jaxbElement = calibrationCertificateMapper.mapToJAXBElement(calibrationCertificateDto);
        marshaller.marshal(jaxbElement, xmlWriter);
        unmarshaller.unmarshal(new StringReader(xmlWriter.toString()));
        log.info("Round trip successfully completed on file '" + file.getName() + "'.");
      } catch (JAXBException e) {
        String message = file.getName() + ": " + e.getLinkedException().getMessage() + " [Error Code: " +
            e.getErrorCode() + "]";
        messages.add(message);
        log.warn(message);
      } catch (IOException e) {
        log.warn(e.getMessage());
      }
    });

    // verify
    assertTrue(messages.isEmpty(), "There are " + messages.size() + " error messages, check log!");
  }

  @Test
  public void roundTrip_JsonExamples_Ok() throws JAXBException, SAXException, URISyntaxException, IOException {

    // prepare
    JAXBContext context = JAXBContext.newInstance(DigitalCalibrationCertificateType.class);
    Unmarshaller unmarshaller = createUnmarshaller(context);
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.setProperty(Marshaller.JAXB_ENCODING, DEFAULT_CHARSET.toString());
    marshaller.setProperty(NS_PREFIX_MAPPER, DccServiceUtil.getNamespacePrefixMapper());
    ObjectMapper objectMapper = createObjectMapper();
    Set<String> messages = new HashSet<>();

    // test
    availableExampleJsonFiles().forEach(file -> {
      try (Writer xmlWriter = new StringWriter()) {
        String json = Files.readString(file.toPath());
        CalibrationCertificateDto calibrationCertificateDto = objectMapper.readValue(new StringReader(json),
            CalibrationCertificateDto.class);
        JAXBElement<?> jaxbElement = calibrationCertificateMapper.mapToJAXBElement(calibrationCertificateDto);
        marshaller.marshal(jaxbElement, xmlWriter);
        unmarshaller.unmarshal(new StringReader(xmlWriter.toString()));
      } catch (JAXBException e) {
        String message = file.getName() + ": " + e.getLinkedException().getMessage() + " [Error Code: " +
            e.getErrorCode() + "]";
        messages.add(message);
        log.warn(message);
      } catch (IOException e) {
        log.warn(e.getMessage());
      }
    });

    // verify
    assertTrue(messages.isEmpty(), "There are " + messages.size() + " error messages, check log!");
  }

  @Nonnull
  private List<File> availableExampleXmlFiles() throws URISyntaxException, IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    List<File> exampleFiles = new ArrayList<>();
    loadFilesFromResource(classLoader.getResource("examples/GP"), ".xml", exampleFiles);
    loadFilesFromResource(classLoader.getResource("examples/Thementag"), ".xml", exampleFiles);
    loadFilesFromResource(classLoader.getResource("examples/FB1.7"), ".xml", exampleFiles);
    loadFilesFromResource(classLoader.getResource("examples/DKD"), ".xml", exampleFiles);
    return exampleFiles;
  }

  @Nonnull
  private List<File> availableExampleJsonFiles() throws URISyntaxException, IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    List<File> exampleFiles = new ArrayList<>();
    loadFilesFromResource(classLoader.getResource("examples/statements"), ".json", exampleFiles);
    return exampleFiles;
  }

  private void loadFilesFromResource(URL resource, String suffix, List<File> targetFiles) throws URISyntaxException, IOException {
    if (resource != null) {
      try (Stream<Path> paths = Files.walk(Paths.get(resource.toURI()))) {
        targetFiles.addAll(paths
            .filter(Files::isRegularFile)
            .map(Path::toFile)
            .filter(file -> file.getName().endsWith(suffix))
            .toList());
      }
      targetFiles.forEach(file -> log.info("Using test resource " + file.getAbsolutePath()));
    }
  }

  @Nonnull
  private Unmarshaller createUnmarshaller(JAXBContext context) throws JAXBException, SAXException {
    Unmarshaller unmarshaller = context.createUnmarshaller();
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema dccSchema = schemaFactory.newSchema(getClass().getResource(LOCAL_DCC_XSD_PATH));
    unmarshaller.setSchema(dccSchema);
    return unmarshaller;
  }
}
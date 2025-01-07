package de.ptb.common.dcc.api.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ptb.common.dcc.api.v1.dto.AdministrativeDataDto;
import de.ptb.common.dcc.api.v1.dto.CalibrationCertificateDto;
import de.ptb.common.dcc.api.v1.dto.CalibrationLaboratoryDto;
import de.ptb.common.dcc.api.v1.dto.ContactDto;
import de.ptb.common.dcc.api.v1.dto.ContactListDto;
import de.ptb.common.dcc.api.v1.dto.DataDto;
import de.ptb.common.dcc.api.v1.dto.DataListDto;
import de.ptb.common.dcc.api.v1.dto.DimensionDto;
import de.ptb.common.dcc.api.v1.dto.EquipmentDto;
import de.ptb.common.dcc.api.v1.dto.EquipmentListDto;
import de.ptb.common.dcc.api.v1.dto.IdentificationDto;
import de.ptb.common.dcc.api.v1.dto.IdentificationListDto;
import de.ptb.common.dcc.api.v1.dto.ItemDto;
import de.ptb.common.dcc.api.v1.dto.ItemListDto;
import de.ptb.common.dcc.api.v1.dto.LanguageSpecificStringsDto;
import de.ptb.common.dcc.api.v1.dto.LocationDto;
import de.ptb.common.dcc.api.v1.dto.MeasurementResultDto;
import de.ptb.common.dcc.api.v1.dto.MeasurementResultListDto;
import de.ptb.common.dcc.api.v1.dto.MethodDto;
import de.ptb.common.dcc.api.v1.dto.MethodListDto;
import de.ptb.common.dcc.api.v1.dto.QuantityDto;
import de.ptb.common.dcc.api.v1.dto.ResultDto;
import de.ptb.common.dcc.api.v1.dto.ResultListDto;
import de.ptb.common.dcc.api.v1.dto.SignatureDto;
import de.ptb.common.dcc.api.v1.dto.SignatureListDto;
import de.ptb.common.dcc.api.v1.dto.SoftwareDto;
import de.ptb.common.dcc.api.v1.dto.SoftwareListDto;
import de.ptb.common.dcc.api.v1.dto.StatementDto;
import de.ptb.common.dcc.api.v1.dto.StatementListDto;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static de.ptb.common.dcc.api.v1.json.SerializationUtils.createObjectMapper;

@Slf4j
public class CalibrationCertificateBuilder {

  private final ObjectMapper objectMapper;
  private String laboratoryName;
  private ContactDto laboratoryContact;
  private ContactDto customer;
  private String manufacturerName;
  private String deviceType;
  private String deviceName;
  private String deviceDescription;
  private LocalDate receiptDate;
  private LocalDate beginDate;
  private LocalDate endDate;
  private ContactListDto respPersons;
  private MeasurementResultListDto measurementResults;
  private SoftwareDto certificateCreationSoftware;
  private ItemListDto items;
  private SignatureListDto signatures;
  private String schemaVersion;

  private CalibrationCertificateBuilder() {
    objectMapper = createObjectMapper();
  }

  public static CalibrationCertificateBuilder getInstance() {
    return new CalibrationCertificateBuilder();
  }

  @Nonnull
  public CalibrationCertificateBuilder withLaboratoryName(String laboratoryName) {
    this.laboratoryName = laboratoryName;
    return this;
  }

  @Nonnull
  public CalibrationCertificateBuilder withLaboratoryContact(String name, String email, String phone,
                                                             String countryCode, String state, String postalCode,
                                                             String city, String street, String streetNo) {
    laboratoryContact = createContact(name, email, phone, countryCode, state, postalCode, city, street, streetNo);
    return this;
  }

  @Nonnull
  public CalibrationCertificateBuilder withCustomer(String name, String email, String phone,
                                                    String countryCode, String state, String postalCode, String city,
                                                    String street, String streetNo) {
    customer = createContact(name, email, phone, countryCode, state, postalCode, city, street, streetNo);
    return this;
  }

  @Nonnull
  public CalibrationCertificateBuilder withManufacturerName(String manufacturerName) {
    this.manufacturerName = manufacturerName;
    return this;
  }

  @Nonnull
  public CalibrationCertificateBuilder withDeviceType(String deviceType) {
    this.deviceType = deviceType;
    return this;
  }

  @Nonnull
  public CalibrationCertificateBuilder withDeviceName(String deviceName) {
    this.deviceName = deviceName;
    return this;
  }

  public CalibrationCertificateBuilder withDeviceDescription(String deviceDescription) {
    this.deviceDescription = deviceDescription;
    return this;
  }

  @Nonnull
  public CalibrationCertificateBuilder withReceiptDate(LocalDate receiptDate) {
    this.receiptDate = receiptDate;
    return this;
  }

  @Nonnull
  public CalibrationCertificateBuilder withBeginDate(LocalDate beginDate) {
    this.beginDate = beginDate;
    return this;
  }

  @Nonnull
  public CalibrationCertificateBuilder withEndDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }

  @Nonnull
  public CalibrationCertificateBuilder withMeasurementResult(String softwareName, String softwareRelease,
                                                             String method, double value, String unit) {
    if (measurementResults == null) {
      measurementResults = new MeasurementResultListDto();
    }
    MeasurementResultDto measurementResult = new MeasurementResultDto();
    measurementResult.setName(createLanguageSpecificStrings(method));
    MethodListDto usedMethodList = new MethodListDto();
    MethodDto usedMethod = new MethodDto();
    usedMethod.setName(createLanguageSpecificStrings(method));
    usedMethodList.add(usedMethod);
    measurementResult.setUsedMethods(usedMethodList);
    SoftwareListDto softwareList = new SoftwareListDto();
    SoftwareDto software = new SoftwareDto();
    software.setName(createLanguageSpecificStrings(softwareName));
    software.setVersion(softwareRelease);
    softwareList.add(software);
    measurementResult.setUsedSoftware(softwareList);
    ResultListDto resultList = new ResultListDto();
    ResultDto result = new ResultDto();
    result.setName(createLanguageSpecificStrings(method));
    DataListDto dataList = new DataListDto();
    QuantityDto quantity = new QuantityDto();
    quantity.setDimension(createDimension(value, unit));
    quantity.setQuantityTypeName("real");
    DataDto dataDto = new DataDto();
    dataDto.setQuantity(quantity);
    dataList.add(dataDto);
    result.setData(dataList);
    resultList.add(result);
    measurementResult.setResults(resultList);
    measurementResults.add(measurementResult);
    return this;
  }

  @Nonnull
  public CalibrationCertificateBuilder withCertificateCreationSoftware(String softwareName,
                                                                       String softwareRelease) {
    certificateCreationSoftware = new SoftwareDto();
    certificateCreationSoftware.setName(createLanguageSpecificStrings(softwareName));
    certificateCreationSoftware.setVersion(softwareRelease);
    return this;
  }

  @Nonnull
  public CalibrationCertificateBuilder withSchemaVersion(String schemaVersion) {
    this.schemaVersion = schemaVersion;
    return this;
  }

  @Nonnull
  public CalibrationCertificateBuilder withResponsiblePerson(String name) {
    if (this.respPersons == null) {
      this.respPersons = new ContactListDto();
    }
    ContactDto contact = new ContactDto();
    contact.setName(createLanguageSpecificStrings(name));
    this.respPersons.add(contact);
    return this;
  }

  @Nonnull
  public CalibrationCertificateBuilder withItem(String itemName, String manufacturerName) {
    if (this.items == null) {
      this.items = new ItemListDto();
    }
    ItemDto item = new ItemDto();
    item.setName(createLanguageSpecificStrings(itemName));
    ContactDto manufacturer = new ContactDto();
    manufacturer.setName(createLanguageSpecificStrings(manufacturerName));
    item.setManufacturer(manufacturer);
    IdentificationListDto ids = new IdentificationListDto();
    IdentificationDto id = new IdentificationDto();
    id.setValue("manufacturer");
    ids.add(id);
    item.setIdentifications(ids);
    this.items.add(item);
    return this;
  }

  @Nonnull
  public CalibrationCertificateBuilder withSignature(byte[] value, String encoding, String mimeType) {
    if (signatures == null) {
      signatures = new SignatureListDto();
    }
    SignatureDto signature = new SignatureDto();
    signature.setId(UUID.randomUUID().toString());
    SignatureDto.SignatureValue signatureValue = new SignatureDto.SignatureValue();
    signatureValue.setId(UUID.randomUUID().toString());
    signatureValue.setValue(value);
    signature.setValue(signatureValue);
    List<SignatureDto.Content> contentList = new ArrayList<>();
    SignatureDto.Content content = new SignatureDto.Content();
    content.setId(UUID.randomUUID().toString());
    content.setEncoding(encoding);
    content.setMimeType(mimeType);
    contentList.add(content);
    signature.setContentList(contentList);
    signatures.add(signature);
    return this;
  }

  public CalibrationCertificateDto build() throws JsonProcessingException, DatatypeConfigurationException {
    CalibrationCertificateDto digitalCalibrationCertificate = new CalibrationCertificateDto();
    AdministrativeDataDto administrativeData = new AdministrativeDataDto();
    administrativeData.setCountryCode("DE");
    administrativeData.setLanguageCodes(Set.of("de"));
    administrativeData.setUniqueIdentifier(UUID.randomUUID().toString());
    CalibrationLaboratoryDto calibrationLaboratory = new CalibrationLaboratoryDto();
    calibrationLaboratory.setCalibrationLaboratoryCode(laboratoryName);
    calibrationLaboratory.setContact(laboratoryContact);
    administrativeData.setCalibrationLaboratory(calibrationLaboratory);
    SoftwareListDto dccSoftwareList = new SoftwareListDto();
    dccSoftwareList.add(certificateCreationSoftware);
    administrativeData.setDccSoftware(dccSoftwareList);
    administrativeData.setReceiptDate(receiptDate);
    administrativeData.setStartDate(beginDate);
    administrativeData.setEndDate(endDate);
    administrativeData.setResponsiblePersons(respPersons);
    EquipmentListDto equipmentList = new EquipmentListDto();
    EquipmentDto equipment = new EquipmentDto();
    equipment.setName(createLanguageSpecificStrings(deviceName));
    equipment.setClassId(deviceType);
    equipment.setManufacturer(createContact(manufacturerName, "manufacturer@ptb.de", "+493034819876",
        "DE", "BE", "10587", "Berlin", "Abbestr.", "2-12"));
    equipment.setModel(deviceName);
    SoftwareListDto deviceSoftwareList = new SoftwareListDto();
    SoftwareDto deviceSoftware = new SoftwareDto();
    deviceSoftware.setName(createLanguageSpecificStrings(deviceName));
    deviceSoftware.setVersion("1.0.0");
    deviceSoftwareList.add(deviceSoftware);
    equipment.setSoftware(deviceSoftwareList);
    equipmentList.add(equipment);
    measurementResults.forEach(result -> result.setEquipment(equipmentList));
    administrativeData.setCustomer(customer);
    StatementListDto statementList = new StatementListDto();
    StatementDto statementMetaData = new StatementDto();
    statementMetaData.setResponsibleAuthority(createContact("PTB", "info@ptb.de", "+493034819875",
        "DE", "BE", "10587", "Berlin", "Abbestr.", "2-12"));
    statementMetaData.setCountryCodes(List.of("DE"));
    statementMetaData.setDate(LocalDate.now());
    statementMetaData.setValidXMLList(List.of(true, false));
    Duration statementDuration = DatatypeFactory.newInstance().newDuration(true, 1, 2, 3,
        10, 30, 0);
    statementMetaData.setPeriod(statementDuration.toString());
    statementList.add(statementMetaData);
    administrativeData.setStatements(statementList);
    administrativeData.setItems(this.items);
    digitalCalibrationCertificate.setAdministrativeData(administrativeData);
    MeasurementResultListDto measurementResultList = new MeasurementResultListDto();
    measurementResults.forEach(result -> result.setStatements(statementList));
    measurementResultList.addAll(measurementResults);
    digitalCalibrationCertificate.setMeasurementResults(measurementResultList);
    digitalCalibrationCertificate.setSignatures(signatures);
    digitalCalibrationCertificate.setSchemaVersion(schemaVersion);
    log.info("Created DCC as JSON: " + objectMapper.writeValueAsString(digitalCalibrationCertificate));
    return digitalCalibrationCertificate;
  }

  private ContactDto createContact(String name, String email, String phone, String countryCode, String state,
                                   String postalCode, String city, String street, String streetNo) {
    ContactDto contact = new ContactDto();
    contact.setName(createLanguageSpecificStrings(name));
    contact.setEMailAddress(email);
    contact.setPhoneNumber(phone);
    LocationDto location = new LocationDto();
    location.setCity(city);
    location.setStreet(street);
    location.setPostalCode(postalCode);
    location.setHouseNumber(streetNo);
    location.setCountryCode(countryCode);
    location.setStateCode(state);
    contact.setLocation(location);
    return contact;
  }

  private LanguageSpecificStringsDto createLanguageSpecificStrings(String value) {
    LanguageSpecificStringsDto text = new LanguageSpecificStringsDto();
    text.add(value);
    return text;
  }

  @Nonnull
  private DimensionDto createDimension(@Nonnull Number value, @Nonnull String unit) {
    DimensionDto dimension = new DimensionDto();
    dimension.setValue(value);
    dimension.setUnit(unit);
    return dimension;
  }
}

package de.ptb.common.dcc.api.v1;

public class CalibrationCertificateControllerRoutes {

  public static final String ID_PARAM_NAME = "id";
  public static final String ID_PLACEHOLDER = "{" + ID_PARAM_NAME + "}";
  public static final String ID_PATH_PART = "/" + ID_PLACEHOLDER;
  public static final String BASE_PATH = "/api/v1/dcc";
  public static final String DCC_PATH = BASE_PATH + "/xsd/dcc";
  public static final String DCC_PATH_ID = DCC_PATH + ID_PATH_PART;
  public static final String DCC_JSON_PATH = DCC_PATH + "/json";
  public static final String DCC_XML_PATH = DCC_PATH + "/xml";
  public static final String DCC_XML_PATH_ID = DCC_PATH_ID + "/xml";
  public static final String DCC_HTML_PATH = DCC_PATH + "/html";
  public static final String DCC_SELF_SIGN_PATH = DCC_PATH + "/selfSign";

  private CalibrationCertificateControllerRoutes() {
    // Konstantenklasse
  }
}

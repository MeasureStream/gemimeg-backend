package de.ptb.common.dcc.api.v1;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CalibrationCertificateConstants {

  public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
  public static final String DEFAULT_SCHEMA_VERSION = "3.3.0";
  public static final String LOCAL_DCC_XSD_PATH = "/xsd/dcc/" + DEFAULT_SCHEMA_VERSION + "/dcc.xsd";
  public static final String LOCAL_DCC_XSL_PATH = "/xsl/dcc/dcc.xsl";

  private CalibrationCertificateConstants() {
    // Konstantenklasse
  }
}

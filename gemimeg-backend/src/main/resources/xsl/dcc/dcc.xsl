<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dcc="https://ptb.de/dcc"
                xmlns:si="https://ptb.de/si" version="1.0">

  <xsl:output method="html" encoding="UTF-8" indent="yes"/>
  <xsl:variable name="base" select="'/'"/>
  <xsl:variable name="primaryLang"
                select="dcc:digitalCalibrationCertificate/dcc:administrativeData/dcc:coreData/dcc:mandatoryLangCodeISO639_1"/>

  <xsl:variable name="secondaryLang"
                select="dcc:digitalCalibrationCertificate/dcc:administrativeData/dcc:coreData/dcc:usedLangCodeISO639_1[text() != $primaryLang]"/>

  <xsl:template match="dcc:digitalCalibrationCertificate">
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="{$base}assets/css/humanReadable.css"/>
      </head>
      <body>
        <div class="dcc-wrapper">
          <div class="dcc-content p-l">
            <h1>Kalibrierzertifikat</h1>
            <p class="english-title">Digital Calibration Certificate</p>
            <xsl:apply-templates select="dcc:administrativeData"/>
          </div>
        </div>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="dcc:administrativeData">
    <div class="administrative-data p-l">
      <h2>Verwaltungsdaten</h2>
      <p class="english-label">Administrative Data</p>

      <div class="software-container p-l">
        <h3>DCC Software</h3>
        <p class="english-label">DCC Software</p>
        <xsl:apply-templates select="dcc:dccSoftware"/>
      </div>
      <div class="coreData-container p-l">
        <h3>Kerndaten</h3>
        <p class="english-label">Coredata</p>
        <xsl:apply-templates select="dcc:coreData" />
      </div>
      <div class="customer-container p-l">
        <h3>Kunde</h3>
        <p class="english-label">Customer</p>
        <xsl:apply-templates select="dcc:customer"/>
      </div>
      <div class="calibrationLaboratory-container p-l">
        <h3>Kalibrierlabor</h3>
        <p class="english-label">Calibration Laboratory</p>
        <xsl:apply-templates select="dcc:calibrationLaboratory"/>
      </div>
      <div class="responsiblePerson-container p-l">
        <h3>Verantwortliche Person</h3>
        <p class="english-label">ResponsablePerson</p>
        <xsl:apply-templates select="dcc:respPersons"/>
      </div>
      <div class="items-container p-l">
        <h3>Kalibriergegenstände</h3>
        <p class="english-label">items</p>
        <xsl:apply-templates select="dcc:items"/>
      </div>
      <div class="statments-container p-l">
        <h3>Statements</h3>
        <p class="english-label">Statements</p>
        <xsl:apply-templates select="dcc:statements"/>
      </div>
    </div>

  </xsl:template>

  <!-- template used software -->
  <xsl:template match="dcc:dccSoftware | dcc:installedSoftwares">

    <xsl:for-each select="dcc:software">
      <table>
        <thead>
          <tr>
            <th colspan="2">Software
              <xsl:value-of select="position()"/>
            </th>
          </tr>
        </thead>
        <tbody>
          <xsl:apply-templates select="dcc:name"/>
          <tr>
            <td><p>Version:</p><p class="english-label">Release</p></td>
            <td><p><xsl:value-of select="dcc:release"/></p><p> </p></td>
          </tr>
        </tbody>
      </table>
    </xsl:for-each>

  </xsl:template>
  <!--  template coreData  -->
  <xsl:template match="dcc:coreData">

    <table>

      <tr><td><p>LänderCode</p><p class="english-label">Country Code</p></td>
        <td><p><xsl:value-of select="dcc:countryCodeISO3166_1"/></p><p> </p></td>
      </tr>
      <tr>
        <td><p>Verwendete Sprachen</p><p class="english-label">Used Language(s)</p></td>
        <td><p><xsl:for-each select="dcc:usedLangCodeISO639_1">
          <xsl:value-of select="."/>
          <xsl:if test="position() !=last()">, </xsl:if>
        </xsl:for-each></p><p> </p>
        </td>
      </tr>
      <tr><td><p>Pflichtsprache</p><p class="english-label">Mandatory Language(s)</p></td>
        <td><p><xsl:for-each select="dcc:mandatoryLangCodeISO639_1">
          <xsl:value-of select="."/>
          <xsl:if test="position() !=last()">, </xsl:if>
        </xsl:for-each></p><p> </p>
        </td>
      </tr>
      <tr><td><p>Kalibrierzeichen</p><p class="english-label">Unique Identdifier</p></td>
        <td><p><xsl:value-of select="dcc:uniqueIdentifier"/></p><p> </p></td>
      </tr>

      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Eingangsdatum'"/>
        <xsl:with-param name="english-label" select="'Unique Identdifier'"/>
        <xsl:with-param name="value" select="dcc:receiptDate"/>
        <xsl:with-param name="isDate" select="'true'"/>
      </xsl:call-template>

      <tr><td><p>Beginn der Labortätigkeit</p></td>
        <td><p><xsl:value-of select="substring-before(dcc:beginPerformanceDate, '+')"/></p></td>
      </tr>
      <tr><td><p>Ende der Labortätigkeit</p></td>
        <td><p><xsl:value-of select="substring-before(dcc:endPerformanceDate, '+')"> </xsl:value-of></p></td>
      </tr>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Ausführungsort'"/>
        <xsl:with-param name="english-label" select="'Place of Calibration'"/>
        <xsl:with-param name="value" select="dcc:performanceLocation"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>
    </table>
  </xsl:template>
  <!--  template customer -->
  <xsl:template match="dcc:customer">
    <table>
      <xsl:apply-templates select="dcc:name"/>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Telefon'"/>
        <xsl:with-param name="english-label" select="'Phone'"/>
        <xsl:with-param name="value" select="dcc:phone"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>

      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'E-mail'"/>
        <xsl:with-param name="english-label" select="'E-mail'"/>
        <xsl:with-param name="value" select="dcc:eMail"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>

      <xsl:apply-templates select="dcc:location"/>

    </table>
  </xsl:template>
  <!--  template location -->
  <xsl:template match="dcc:location">
    <table>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Stadt'"/>
        <xsl:with-param name="english-label" select="'City'"/>
        <xsl:with-param name="value" select="dcc:city"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>

      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Ländercode'"/>
        <xsl:with-param name="english-label" select="'CountryCode'"/>
        <xsl:with-param name="value" select="dcc:countryCode"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>

      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'PLZ'"/>
        <xsl:with-param name="english-label" select="'Postal Code'"/>
        <xsl:with-param name="value" select="dcc:postCode"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>

      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Bundesland'"/>
        <xsl:with-param name="english-label" select="'State'"/>
        <xsl:with-param name="value" select="dcc:state"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>

      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Straße'"/>
        <xsl:with-param name="english-label" select="'Street'"/>
        <xsl:with-param name="value" select="dcc:street"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>

      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Hausnr'"/>
        <xsl:with-param name="english-label" select="'Street No.'"/>
        <xsl:with-param name="value" select="dcc:streetNo"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>

      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Postfach'"/>
        <xsl:with-param name="english-label" select="'PostOfficeBox'"/>
        <xsl:with-param name="value" select="dcc:postOfficeBox"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>

    </table>
    <xsl:call-template  name="richContent"/>
  </xsl:template>
  <!--  template name -->
  <xsl:template match="dcc:name">
      <xsl:variable name="count" select="count(dcc:content)"/>
      <xsl:for-each select="dcc:content">
        <xsl:variable name="lang" select="@lang"/>
        <xsl:variable name="pos" select="position()"/>

        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="concat('Name',$pos)"/>
          <xsl:with-param name="english-label" select="concat('Name',$pos)"/>
          <xsl:with-param name="value" select="."/>
          <xsl:with-param name="isDate" select="'false'"/>
          <xsl:with-param name="lang" select="$lang"/>
        </xsl:call-template>
      </xsl:for-each>

  </xsl:template>
  <!-- template calibrationLaboratory-->
  <xsl:template match="dcc:calibrationLaboratory">
    <table>
      <tr><td><p>Kennung</p><p class="english-label">Calibration Laboratory Code</p></td><td><p><xsl:value-of select="dcc:calibrationLaboratoryCode"/></p><p> </p></td></tr>
      <xsl:apply-templates select="dcc:contact/dcc:name"/>
      <tr><td>
        <p>Telefon</p><p class="english-label">Phone</p></td>
        <td><p>
          <xsl:value-of select="dcc:contact/dcc:phone"/>
        </p>
          <p> </p>
        </td>
      </tr>
      <tr><td>
        <p>E-mail</p><p class="english-label">E-mail</p></td>
        <td><p>
          <xsl:value-of select="dcc:contact/dcc:eMail"/>
        </p>
          <p> </p>
        </td>
      </tr>
      <xsl:apply-templates select="dcc:contact/dcc:location"/>
    </table>
  </xsl:template>
  <!-- template respPersons-->
  <xsl:template match="dcc:respPersons">

    <table>
      <xsl:for-each select="dcc:respPerson/dcc:person">
        <table>
          <thead>
            <tr>
              <th colspan="2">Person
                <xsl:value-of select="position()"/>
              </th>
            </tr>
          </thead>
          <tbody>
            <xsl:apply-templates select="dcc:name" />
            <xsl:call-template name="showIfNotEmpty">
              <xsl:with-param name="label" select="'Telefon'"/>
              <xsl:with-param name="english-label" select="'Phone'"/>
              <xsl:with-param name="value" select="dcc:phone"/>
              <xsl:with-param name="isDate" select="'false'"/>
            </xsl:call-template>

            <xsl:call-template name="showIfNotEmpty">
              <xsl:with-param name="label" select="'E-mail'"/>
              <xsl:with-param name="english-label" select="'E-mail'"/>
              <xsl:with-param name="value" select="dcc:eMail"/>
              <xsl:with-param name="isDate" select="'false'"/>
            </xsl:call-template>

          </tbody>
        </table>
      </xsl:for-each>
    </table>
  </xsl:template>
  <!-- template manufacturer-->
  <xsl:template match="dcc:manufacturer">

    <table>
      <tr>
        <td>
          <p><b>Hersteller</b></p>
          <p class="english-label"><b>Hersteller</b></p>
        </td>
        <td><p> </p> <p> </p></td>
      </tr>
      <xsl:apply-templates select="dcc:name"/>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Telefon'"/>
        <xsl:with-param name="english-label" select="'Phone'"/>
        <xsl:with-param name="value" select="dcc:phone"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>

      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'E-mail'"/>
        <xsl:with-param name="english-label" select="'E-mail'"/>
        <xsl:with-param name="value" select="dcc:eMail"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>

    </table>
    <xsl:apply-templates select="dcc:location"/>
  </xsl:template>
  <!-- template identifications-->
  <xsl:template match="dcc:identifications">

    <div class="nested-list">
      <p class="nested-header">Identifikatoren</p><p class="nested-english-label"> (identifications)</p>
      <xsl:for-each select="dcc:identification">
        <table>
          <thead>
            <tr>
              <th  colspan="2">Identifikator
                <xsl:value-of select="position()"/>
              </th>
            </tr>
          </thead>
          <tbody>

            <tr>
              <td><p>Herausgeber:</p><p class="english-label">Issuer</p></td>
              <td><p><xsl:value-of select="dcc:issuer"/></p><p> </p></td>
            </tr>
            <tr>
              <td><p>Identifikator:</p><p class="english-label">value</p></td>
              <td><p><xsl:value-of select="dcc:value"/></p><p> </p></td>
            </tr>
            <xsl:apply-templates select="dcc:name"/>
          </tbody>
        </table>
      </xsl:for-each>
    </div>
  </xsl:template>
  <!-- template items-->
  <xsl:template match="dcc:items">
    <xsl:for-each select="dcc:item">
      <h4>Kalibriergegenstand <xsl:value-of select="position()"/></h4>
      <table>
        <xsl:apply-templates select="dcc:name"/>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Modell'"/>
          <xsl:with-param name="english-label" select="'Model'"/>
          <xsl:with-param name="value" select="dcc:model"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>

        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Norm'"/>
          <xsl:with-param name="english-label" select="'Class Reference'"/>
          <xsl:with-param name="value" select="dcc:classReference"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>

        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Referenz'"/>
          <xsl:with-param name="english-label" select="'Class Id'"/>
          <xsl:with-param name="value" select="dcc:classId"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>

      </table>
      <xsl:call-template  name="richContent"/>
      <xsl:apply-templates select="dcc:manufacturer" />
      <xsl:apply-templates select="dcc:identifications"/>
      <xsl:if test="dcc:installedSoftwares">
        <div class="nested-list">
          <p class="nested-header">Installierte Software</p><p class="nested-english-label"> (installed Software)</p>
          <xsl:apply-templates select="dcc:installedSoftwares"/>
        </div>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="dcc:statements">
    <xsl:for-each select="dcc:statement">

      <h4>Statement <xsl:value-of select="position()"/></h4>
      <xsl:if test="@refType">
        <p>Ref-Type: <xsl:value-of select="@refType"/></p>
      </xsl:if>
      <table>
        <xsl:apply-templates select="dcc:name"/>
      </table>
      <xsl:call-template  name="richContent"/>
      <table>

        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Ländercode'"/>
          <xsl:with-param name="english-label" select="'Country Code'"/>
          <xsl:with-param name="value" select="dcc:countryCodeISO3166_1"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>

        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Konvention'"/>
          <xsl:with-param name="english-label" select="'convention'"/>
          <xsl:with-param name="value" select="dcc:convention"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>

        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Referenz'"/>
          <xsl:with-param name="english-label" select="'reference'"/>
          <xsl:with-param name="value" select="dcc:reference"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>

        <xsl:for-each select="dcc:norm">
          <xsl:call-template name="showIfNotEmpty">
            <xsl:with-param name="label" select="concat('Norm ', position())"/>
            <xsl:with-param name="english-label" select="concat('Norm ', position())"/>
            <xsl:with-param name="value" select="."/>
            <xsl:with-param name="isDate" select="'false'"/>
          </xsl:call-template>
        </xsl:for-each>

        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Datum'"/>
          <xsl:with-param name="english-label" select="'date'"/>
          <xsl:with-param name="value" select="dcc:date"/>
          <xsl:with-param name="isDate" select="'true'"/>
        </xsl:call-template>

        <!--        example period: P1Y2M3DT10H30M0S-->
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Periode'"/>
          <xsl:with-param name="english-label" select="'period'"/>
          <xsl:with-param name="value" select="dcc:period"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>

        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Konformität'"/>
          <xsl:with-param name="english-label" select="'conformity'"/>
          <xsl:with-param name="value" select="dcc:conformity"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>

        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Non-SI Unit'"/>
          <xsl:with-param name="english-label" select="'Non-SI Unit'"/>
          <xsl:with-param name="value" select="dcc:nonSIUnit"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>

        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Non-SI Definition'"/>
          <xsl:with-param name="english-label" select="'Non-SI Definition'"/>
          <xsl:with-param name="value" select="dcc:nonSIDefinition"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>

        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'rückführbar'"/>
          <xsl:with-param name="english-label" select="'traceable'"/>
          <xsl:with-param name="value" select="dcc:traceable"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>

        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Valide'"/>
          <xsl:with-param name="english-label" select="'Valid'"/>
          <xsl:with-param name="value" select="dcc:valid"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>

        <xsl:apply-templates select="dcc:location"/>
      </table>

      <xsl:apply-templates select="dcc:respAuthority"/>

    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:data">
    <xsl:apply-templates select="quantities"/>
  </xsl:template>
  <xsl:template match="dcc:quantities">
  </xsl:template>
  <xsl:template match="dcc:quantity">
  </xsl:template>
  <xsl:template match="dcc:respAuthority">
    <table>
      <tr>
        <td>
          <p><b>Verantwortliche Stelle</b></p>
          <p class="english-label"><b>Responsible Authority</b></p>
        </td>
        <td><p> </p> <p> </p></td>
      </tr>
      <xsl:apply-templates select="dcc:name"/>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Telefon'"/>
        <xsl:with-param name="english-label" select="'Phone'"/>
        <xsl:with-param name="value" select="dcc:phone"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>

      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'E-mail'"/>
        <xsl:with-param name="english-label" select="'E-mail'"/>
        <xsl:with-param name="value" select="dcc:eMail"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>

      <xsl:apply-templates select="dcc:location"/>
    </table>

  </xsl:template>

  <!-- if-not-empty -->
  <xsl:template name="showIfNotEmpty">
    <xsl:param name="label"/>
    <xsl:param name="english-label"/>
    <xsl:param name="value"/>
    <xsl:param name="isDate"/>
    <xsl:param name="lang"/> <!-- Neu: Sprache als Parameter -->

    <xsl:if test="string($value) != ''">
      <tr>
        <td>
          <p><xsl:value-of select="$label"/></p>
          <p class="english-label"><xsl:value-of select="$english-label"/></p>
        </td>
        <td>
          <p>
            <xsl:choose>
              <xsl:when test="$isDate = 'true'">
                <xsl:value-of select="substring-before($value, '+')"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="$value"/>
              </xsl:otherwise>
            </xsl:choose>
          </p>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>
  <!--  richContent -->
  <xsl:template name="richContent">
    <xsl:choose>
      <xsl:when test="dcc:further">
        <xsl:call-template name="richContentSection">
          <xsl:with-param name="sectionName" select="dcc:further" />
          <xsl:with-param name="labelDe" select="'Weitere Information'"/>
          <xsl:with-param name="labelEn" select="'Further'"/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>

    <!-- Überprüfe, ob es die Sektion 'description' gibt -->
    <xsl:choose>
      <xsl:when test="dcc:description">
        <xsl:call-template name="richContentSection">
          <xsl:with-param name="sectionName" select="dcc:description" />
          <xsl:with-param name="labelDe" select="'Beschreibung'"/>
          <xsl:with-param name="labelEn" select="'Description'"/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>

    <!-- Überprüfe, ob es die Sektion 'declaration' gibt -->
    <xsl:choose>
      <xsl:when test="dcc:declaration">
        <xsl:call-template name="richContentSection">
          <xsl:with-param name="sectionName" select="dcc:declaration" />
          <xsl:with-param name="labelDe" select="'Deklaration'"/>
          <xsl:with-param name="labelEn" select="'Declaration'"/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  <!-- template Richcontent(Weitere Information/Beschreibung/Deklaration) -->
  <xsl:template name="richContentSection">
    <xsl:param name="sectionName"/>
    <xsl:param name="labelDe"/>
    <xsl:param name="labelEn"/>
    <table>
      <tr>
        <td>
          <p><b><xsl:value-of select="$labelDe"/> </b></p>
          <p class="english-label"><b><xsl:value-of select="$labelEn"/></b></p>
        </td>
        <td><p> </p> <p> </p></td>
      </tr>
      <xsl:apply-templates select="$sectionName/dcc:name"/>
      <xsl:for-each select="$sectionName/dcc:content[@lang='de']">
        <tr>
          <td><p>Inhalt <xsl:value-of select="position()"/></p><p class="english-label">Content <xsl:value-of select="position()"/></p></td>
          <td><p><xsl:value-of select="."/></p> <p> </p></td>
        </tr>
      </xsl:for-each>
    </table>

  </xsl:template>


</xsl:stylesheet>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dcc="https://ptb.de/dcc"
                xmlns:si="https://ptb.de/si"
                version="2.0">

  <xsl:output method="html" encoding="UTF-8" indent="yes"/>
  <xsl:variable name="base" select="'/'"/>
  <xsl:variable name="primaryLang"
                select="dcc:digitalCalibrationCertificate/dcc:administrativeData/dcc:coreData/dcc:mandatoryLangCodeISO639_1"/>
  <xsl:variable name="secondaryLang"
                select="dcc:digitalCalibrationCertificate/dcc:administrativeData/dcc:coreData/dcc:usedLangCodeISO639_1[text() != $primaryLang]"/>
  <xsl:template match="dcc:digitalCalibrationCertificate">
    <html>
      <head>
        <style>
          .dcc-wrapper {
          font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
          Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
          border: 1px solid rgb(112, 112, 112);
          border-radius: 10px;
          min-width: 540px;
          width: auto;
          padding: 15px;
          .dcc-logos {
          display: flex;
          align-items: center;
          justify-content: space-between;
          width: 100%;
          height: 120px;
          border-bottom: 1px solid rgb(130, 130, 131);
          .ptb-logo {
          width: 230px;
          }
          .bundesadler-logo {
          width: 80px;
          }
          }
          .dcc-content {
          h1 {
          margin-top: 20px;
          margin-bottom: 5px;
          font-size: 36px;
          font-weight: 600;
          }
          .english-title {
          font-size: 18px;
          font-style: italic;
          }
          .english-label {
          font-size: 12px;
          font-style: italic;
          }
          .content {
          overflow-wrap: anywhere;
          white-space: normal;
          }
          .administrative-data,
          .measurementResults-container {
          .p-l {
          border-bottom: 1px solid rgb(0, 0, 0, 0.08);
          padding: 10px 0;
          }
          h2,
          h3,
          h4 {
          margin-top: 10px;
          margin-bottom: 5px;
          }
          h2 {
          font-size: 24px;
          font-weight: 600;
          }
          h3 {
          font-size: 20px;
          font-weight: 600;
          }
          h4 {
          font-size: 16px !important;
          font-weight: 600 !important;
          }
          h5 {
          font-size: 14px !important;
          font-weight: 600 !important;
          }
          .images{
          height:100px;
          }
          table {
          min-width: 200px;
          border-collapse: collapse;
          margin: 10px;
          }
          thead tr {
          border-bottom: 1px solid black;
          }
          th {
          text-align: left;
          border-bottom: 1px solid black;
          }
          td {
          vertical-align: top;
          overflow-wrap: break-word;
          white-space: normal;
          }
          td:first-child {
          width: 250px;
          }
          td p {
          margin: 0;
          width: 100%;
          }
          td p:last-child {
          margin-bottom: 5px;
          }
          }
          .nested-container {
          width: 80%;
          margin-left: 20px;
          .nested-container-heading,
          .nested-container-heading.nested-english-label {
          font-size: 14px;
          font-weight: 700;
          display: inline;
          }
          .nested-wrapper {
          width: auto;
          margin-top: 20px;
          .nested-div-content-heading {
          margin-top: 10px;
          }

          .table-wrapper {

          min-width: 500px;
          table {
          margin: 10px 0;
          min-width: 500px;
          }
          }
          .table-quantity-wrapper {
          margin: 10px 0;
          min-width: 500px;
          .table-quantity-metrics-horizontal,
          .table-quantity-metrics-vertical {
          margin: 0;
          margin: 15px 0;
          min-width: 500px;
          th p {
          margin: 3px 0;
          }
          th {
          background-color: rgb(224, 224, 224);
          }
          th,
          td {
          text-align: center;
          border: 1px solid black;
          text-align: center;
          vertical-align: middle;
          }
          .english-label {
          font-size: 12px;
          font-weight: 400;
          font-style: italic;
          }
          }
          .table-quantity-metrics-horizontal {
          th p:first-child,
          td p:first-child {
          font-weight: 700;
          }
          td:first-child {
          background-color: rgb(224, 224, 224);
          width: 180px;
          }
          }
          }
          }
          }
          }
          }
        </style>
      </head>
      <body>
        <div class="dcc-wrapper">
          <div class="dcc-content p-l">
            <h1>Certificato di taratura</h1>
            <p class="english-title">Digital Calibration Certificate</p>
            <xsl:apply-templates select="dcc:administrativeData"/>
            <xsl:call-template name="measurementResultsSection"/>
          </div>
        </div>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="dcc:administrativeData">
    <div class="administrative-data p-l">
      <h2>Dati amministrativi</h2>
      <p class="english-label">Administrative Data</p>
      <div class="software-container p-l">
        <h3>DCC Software</h3>
        <p class="english-label">DCC Software</p>
        <xsl:apply-templates select="dcc:dccSoftware"/>
      </div>
      <div class="coreData-container p-l">
        <h3>Dati principali</h3>
        <p class="english-label">Coredata</p>
        <xsl:apply-templates select="dcc:coreData"/>
      </div>
      <div class="customer-container p-l">
        <h3>Cliente</h3>
        <p class="english-label">Customer</p>
        <xsl:apply-templates select="dcc:customer"/>
      </div>
      <div class="calibrationLaboratory-container p-l">
        <h3>Laboratorio di taratura</h3>
        <p class="english-label">Calibration Laboratory</p>
        <xsl:apply-templates select="dcc:calibrationLaboratory"/>
      </div>
      <div class="responsiblePerson-container p-l">
        <h3>Persona responsabile</h3>
        <p class="english-label">ResponsablePerson</p>
        <xsl:apply-templates select="dcc:respPersons"/>
      </div>
      <div class="items-container p-l">
        <h3>Oggetti di taratura</h3>
        <p class="english-label">items</p>
        <xsl:apply-templates select="dcc:items"/>
      </div>
      <div class="statments-container p-l">
        <h3>Constatazioni</h3>
        <p class="english-label">Statements</p>
        <xsl:apply-templates select="dcc:statements"/>
      </div>
    </div>
  </xsl:template>
  <xsl:template name="measurementResultsSection">
    <div class="measurementResults-container p-l">
      <h3>Risultati di misura</h3>
      <p class="english-label">Measure Results</p>
      <xsl:apply-templates select="dcc:measurementResults"/>
    </div>
  </xsl:template>
  <xsl:template match="dcc:dccSoftware | dcc:installedSoftwares | dcc:usedSoftware">
    <xsl:for-each select="dcc:software">
      <table>
        <thead>
          <tr>
            <th colspan="2"><xsl:value-of select="position()"/>. Software (Software)
            </th>
          </tr>
        </thead>
        <tbody>
          <xsl:apply-templates select="dcc:name"/>
          <tr>
            <td>
              <p>Version:</p>
              <p class="english-label">Release</p>
            </td>
            <td>
              <p>
                <xsl:value-of select="dcc:release"/>
              </p>
              <p></p>
            </td>
          </tr>
        </tbody>
      </table>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:coreData">
    <table>
      <tr>
        <td>
          <p>Codice paese</p>
          <p class="english-label">Country Code</p>
        </td>
        <td>
          <p>
            <xsl:value-of select="dcc:countryCodeISO3166_1"/>
          </p>
          <p></p>
        </td>
      </tr>
      <tr>
        <td>
          <p>Lingue utilizzate</p>
          <p class="english-label">Used Language</p>
        </td>
        <td>
          <p>
            <xsl:for-each select="dcc:usedLangCodeISO639_1">
              <xsl:value-of select="."/>
              <xsl:if test="position() !=last()">,</xsl:if>
            </xsl:for-each>
          </p>
          <p></p>
        </td>
      </tr>
      <tr>
        <td>
          <p>Lingua obbligatoria</p>
          <p class="english-label">Mandatory Language</p>
        </td>
        <td>
          <p>
            <xsl:for-each select="dcc:mandatoryLangCodeISO639_1">
              <xsl:value-of select="."/>
              <xsl:if test="position() !=last()">,</xsl:if>
            </xsl:for-each>
          </p>
          <p></p>
        </td>
      </tr>
      <tr>
        <td>
          <p>Identificatore univoco</p>
          <p class="english-label">Unique Identdifier</p>
        </td>
        <td>
          <p>
            <xsl:value-of select="dcc:uniqueIdentifier"/>
          </p>
          <p></p>
        </td>
      </tr>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Data di ricezione'"/>
        <xsl:with-param name="english-label" select="'Arrival date'"/>
        <xsl:with-param name="value" select="dcc:receiptDate"/>
        <xsl:with-param name="isDate" select="'true'"/>
      </xsl:call-template>
      <tr>
        <td>
          <p>Inizio delle attività di laboratorio</p>
          <p class="english-label">Start of lab operations</p>
        </td>
        <td>
          <p>
            <xsl:value-of select="substring-before(dcc:beginPerformanceDate, '+')"/>
          </p>
        </td>
      </tr>
      <tr>
        <td>
          <p>Fine delle attività di laboratorio</p>
          <p class="english-label">End of lab operations</p>
        </td>
        <td>
          <p>
            <xsl:value-of select="substring-before(dcc:endPerformanceDate, '+')"></xsl:value-of>
          </p>
        </td>
      </tr>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Luogo di esecuzione'"/>
        <xsl:with-param name="english-label" select="'Place of Calibration'"/>
        <xsl:with-param name="value" select="dcc:performanceLocation"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>
    </table>
  </xsl:template>
  <xsl:template match="dcc:customer">
    <table>
      <xsl:apply-templates select="dcc:name"/>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Telefono'"/>
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
  <xsl:template match="dcc:location">
    <table>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Città'"/>
        <xsl:with-param name="english-label" select="'City'"/>
        <xsl:with-param name="value" select="dcc:city"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Codice paese'"/>
        <xsl:with-param name="english-label" select="'CountryCode'"/>
        <xsl:with-param name="value" select="dcc:countryCode"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Codice postale'"/>
        <xsl:with-param name="english-label" select="'Postal Code'"/>
        <xsl:with-param name="value" select="dcc:postCode"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Regione'"/>
        <xsl:with-param name="english-label" select="'State'"/>
        <xsl:with-param name="value" select="dcc:state"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Via'"/>
        <xsl:with-param name="english-label" select="'Street'"/>
        <xsl:with-param name="value" select="dcc:street"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Numero civico'"/>
        <xsl:with-param name="english-label" select="'Street No.'"/>
        <xsl:with-param name="value" select="dcc:streetNo"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Casella postale'"/>
        <xsl:with-param name="english-label" select="'PostOfficeBox'"/>
        <xsl:with-param name="value" select="dcc:postOfficeBox"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>
    </table>
    <xsl:call-template name="richContent"/>
  </xsl:template>
  <xsl:template match="dcc:name">
    <xsl:variable name="count" select="count(dcc:content)"/>
    <xsl:for-each select="dcc:content">
      <xsl:variable name="lang" select="@lang"/>
      <xsl:variable name="pos" select="position()"/>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="concat('Name ',$pos)"/>
        <xsl:with-param name="english-label" select="concat('Name ',$pos)"/>
        <xsl:with-param name="value" select="."/>
        <xsl:with-param name="isDate" select="'false'"/>
        <xsl:with-param name="lang" select="$lang"/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:calibrationLaboratory">
    <table>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Identificatore'"/>
        <xsl:with-param name="english-label" select="'Calibration Laboratory Code'"/>
        <xsl:with-param name="value" select="dcc:calibrationLaboratoryCode"/>
        <xsl:with-param name="isDate" select="'false'"/>
      </xsl:call-template>
      <xsl:apply-templates select="dcc:contact/dcc:name"/>
      <tr>
        <td>
          <p>Telefono</p>
          <p class="english-label">Phone</p>
        </td>
        <td>
          <p>
            <xsl:value-of select="dcc:contact/dcc:phone"/>
          </p>
          <p></p>
        </td>
      </tr>
      <tr>
        <td>
          <p>E-mail</p>
          <p class="english-label">E-mail</p>
        </td>
        <td>
          <p>
            <xsl:value-of select="dcc:contact/dcc:eMail"/>
          </p>
          <p></p>
        </td>
      </tr>
    </table>
    <xsl:apply-templates select="dcc:contact/dcc:location"/>

  </xsl:template>
  <xsl:template match="dcc:respPersons">
    <table>
      <xsl:for-each select="dcc:respPerson/dcc:person">
        <table>
          <thead>
            <tr>
              <th colspan="2"><xsl:value-of select="position()"/>. Person (Person)
              </th>
            </tr>
          </thead>
          <tbody>
            <xsl:apply-templates select="dcc:name"/>
            <xsl:call-template name="showIfNotEmpty">
              <xsl:with-param name="label" select="'Telefono'"/>
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
  <xsl:template match="dcc:manufacturer">
    <table>
      <tr>
        <td>
          <p>
            <b>Produttore</b>
          </p>
          <p class="english-label">
            <b>Manufacturer</b>
          </p>
        </td>
        <td>
          <p></p>
          <p></p>
        </td>
      </tr>
      <xsl:apply-templates select="dcc:name"/>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Telefono'"/>
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
  <xsl:template match="dcc:identifications">
    <div class="nested-container">
      <h4>Identificatori
        (Identifications)
      </h4>
      <xsl:for-each select="dcc:identification">
        <div class="nested-wrapper">
          <div class="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th colspan="2"><xsl:value-of select="position()"/>. Identificatore (Identificator)
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>
                    <p>Emittente</p>
                    <p class="english-label">Issuer</p>
                  </td>
                  <td>
                    <p>
                      <xsl:value-of select="dcc:issuer"/>
                    </p>
                    <p></p>
                  </td>
                </tr>
                <tr>
                  <td>
                    <p>Identificatore:</p>
                    <p class="english-label">value</p>
                  </td>
                  <td>
                    <p>
                      <xsl:value-of select="dcc:value"/>
                    </p>
                    <p></p>
                  </td>
                </tr>
                <xsl:apply-templates select="dcc:name"/>
              </tbody>
            </table>
          </div>
        </div>
      </xsl:for-each>
    </div>
  </xsl:template>
  <xsl:template match="dcc:items">
    <xsl:for-each select="dcc:item">
      <h4><xsl:value-of select="position()"/>. Oggetto di taratura (Item)
      </h4>
      <table>
        <xsl:apply-templates select="dcc:name"/>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Modello'"/>
          <xsl:with-param name="english-label" select="'Model'"/>
          <xsl:with-param name="value" select="dcc:model"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Class Reference'"/>
          <xsl:with-param name="english-label" select="'Class Reference'"/>
          <xsl:with-param name="value" select="dcc:equipmentClass/dcc:reference"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Class ID'"/>
          <xsl:with-param name="english-label" select="'Class ID'"/>
          <xsl:with-param name="value" select="dcc:equipmentClass/dcc:classID"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>
      </table>
      <xsl:call-template name="richContent"/>
      <xsl:apply-templates select="dcc:manufacturer"/>
      <xsl:apply-templates select="dcc:identifications"/>
      <xsl:if test="dcc:installedSoftwares">
        <div class="nested-container">
          <p class="nested-container-heading">Software installata</p>
          <p class="nested-container-heading nested-english-label">(Installed Software)</p>
          <xsl:apply-templates select="dcc:installedSoftwares"/>
        </div>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:statements | dcc:measurementMetaData">
    <xsl:variable name="title">
      <xsl:choose>
        <xsl:when test="self::dcc:statements">Constatazione (Statement)</xsl:when>
        <xsl:otherwise>Metadati (Meta Data)</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:for-each select="dcc:statement | dcc:metaData">
      <h4><xsl:value-of select="position()"/>.
        <xsl:value-of select="$title"/>
      </h4>
      <table>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'RefType'"/>
          <xsl:with-param name="english-label" select="'RefType'"/>
          <xsl:with-param name="value" select="@refType"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>
        <xsl:apply-templates select="dcc:name"/>
      </table>
      <xsl:call-template name="richContent"/>
      <table>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Codice paese'"/>
          <xsl:with-param name="english-label" select="'Country Code'"/>
          <xsl:with-param name="value" select="dcc:countryCodeISO3166_1"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Convenzione'"/>
          <xsl:with-param name="english-label" select="'convention'"/>
          <xsl:with-param name="value" select="dcc:convention"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Riferimento'"/>
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
          <xsl:with-param name="label" select="'Data'"/>
          <xsl:with-param name="english-label" select="'date'"/>
          <xsl:with-param name="value" select="dcc:date"/>
          <xsl:with-param name="isDate" select="'true'"/>
        </xsl:call-template>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Periodo'"/>
          <xsl:with-param name="english-label" select="'period'"/>
          <xsl:with-param name="value" select="dcc:period"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Conformità'"/>
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
          <xsl:with-param name="label" select="'tracciabile'"/>
          <xsl:with-param name="english-label" select="'traceable'"/>
          <xsl:with-param name="value" select="dcc:traceable"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Valido'"/>
          <xsl:with-param name="english-label" select="'Valid'"/>
          <xsl:with-param name="value" select="dcc:valid"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>
      </table>
      <xsl:apply-templates select="dcc:location"/>
      <xsl:apply-templates select="dcc:respAuthority"/>
      <xsl:apply-templates select="dcc:data"/>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:data">
    <div class="nested-container">
      <xsl:if test="dcc:quantity">
        <xsl:call-template name="quantity"/>
      </xsl:if>
      <xsl:if test="dcc:list">
        <xsl:apply-templates select="dcc:list"/>
      </xsl:if>
    </div>
  </xsl:template>
  <xsl:template match="dcc:measurementResults">
    <xsl:for-each select="dcc:measurementResult">
      <h4><xsl:value-of select="position()"/>. Risultato di misura (Measurement Results)
      </h4>
      <xsl:if test="dcc:name">
        <table>
          <xsl:apply-templates select="dcc:name"/>
        </table>
      </xsl:if>
      <xsl:if test="dcc:usedSoftware">
        <h3>
          Software utilizzata
        </h3>
        <p class="english-label">
          Used Software
        </p>
        <xsl:apply-templates select="dcc:usedSoftware"/>
      </xsl:if>
      <xsl:if test="dcc:usedMethods">
        <h3>Metodi</h3>
        <p class="english-label">Methods</p>
        <xsl:apply-templates select="dcc:usedMethods"/>
      </xsl:if>
      <xsl:if test="dcc:influenceConditions">
        <h3>Fattori di influenza</h3>
        <p class="english-label">Influence Condition</p>
        <xsl:apply-templates select="dcc:influenceConditions"/>
      </xsl:if>
      <xsl:if test="dcc:results">
        <h3>Risultati</h3>
        <p class="english-label">Results</p>
        <xsl:apply-templates select="dcc:results"/>
      </xsl:if>
      <xsl:if test="dcc:measurementMetaData">
        <h3>Metadati</h3>
        <p class="english-label">Meta Data</p>
        <xsl:apply-templates select="dcc:measurementMetaData"/>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:usedMethods">
    <xsl:for-each select="dcc:usedMethod">
      <h4><xsl:value-of select="position()"/>. Metodo utilizzato (Used Method)
      </h4>
      <xsl:if test="dcc:name">
        <table>
          <xsl:call-template name="showIfNotEmpty">
            <xsl:with-param name="label" select="'RefType'"/>
            <xsl:with-param name="english-label" select="'RefType'"/>
            <xsl:with-param name="value" select="@refType"/>
            <xsl:with-param name="isDate" select="'false'"/>
          </xsl:call-template>
          <xsl:apply-templates select="dcc:name"/>
          <xsl:if test="dcc:norm">
            <tr>
              <td>
                <p>Norm</p>
                <p class="english-label">Norm</p>
              </td>
              <td>
                <p>
                  <xsl:value-of select="dcc:norm"/>
                </p>
                <p></p>
              </td>
            </tr>
          </xsl:if>
        </table>
        <xsl:call-template name="richContent"/>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:influenceConditions">
    <xsl:for-each select="dcc:influenceCondition">
      <h4><xsl:value-of select="position()"/>. Fattore di influenza (Influence Condition)
      </h4>
      <table>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'RefType'"/>
          <xsl:with-param name="english-label" select="'RefType'"/>
          <xsl:with-param name="value" select="@refType"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>
        <xsl:apply-templates select="dcc:name"/>
        <xsl:if test="dcc:status">
          <tr>
            <td>
              <p>
                Status
              </p>
              <p class="english-label">
                status
              </p>
            </td>
            <td>
              <p>
                <xsl:value-of select="dcc:status"/>
              </p>
              <p></p>
            </td>
          </tr>
        </xsl:if>
      </table>
      <xsl:apply-templates select="dcc:data"/>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:results">
    <xsl:for-each select="dcc:result">
      <h4><xsl:value-of select="position()"/>. Risultato (Result)
      </h4>
      <table>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'RefType'"/>
          <xsl:with-param name="english-label" select="'RefType'"/>
          <xsl:with-param name="value" select="@refType"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>
        <xsl:apply-templates select="dcc:name"/>
      </table>
      <xsl:apply-templates select="dcc:data"/>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:respAuthority">
    <table>
      <tr>
        <td>
          <p>
            <b>Autorità responsabile</b>
          </p>
          <p class="english-label">
            <b>Responsible Authority</b>
          </p>
        </td>
        <td>
          <p></p>
          <p></p>
        </td>
      </tr>
      <xsl:apply-templates select="dcc:name"/>
      <xsl:call-template name="showIfNotEmpty">
        <xsl:with-param name="label" select="'Telefono'"/>
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
  <xsl:template match="dcc:list">
    <xsl:call-template name="quantity"/>
  </xsl:template>
  <xsl:template match="si:expandedUncXMLList">
    <h5>Expanded Uncertainty</h5>
    <table class="table-quantity-metrics-horizontal">
      <tr>
        <td>Uncertainty</td>
        <td>
          <xsl:value-of select="si:uncertaintyXMLList"/>
        </td>
      </tr>
      <tr>
        <td>Coverage Factor</td>
        <td>
          <xsl:value-of select="si:coverageFactorXMLList"/>
        </td>
      </tr>
      <tr>
        <td>Coverage Probability</td>
        <td>
          <xsl:value-of select="si:coverageProbabilityXMLList"/>
        </td>
      </tr>
      <tr>
        <td>Distribution</td>
        <td>
          <xsl:value-of select="si:distributionXMLList"/>
        </td>
      </tr>
    </table>
  </xsl:template>
  <xsl:template name="showIfNotEmpty">
    <xsl:param name="label"/>
    <xsl:param name="english-label"/>
    <xsl:param name="value"/>
    <xsl:param name="isDate"/>
    <xsl:param name="lang"/>
    <xsl:if test="string($value) != ''">

      <tr>
        <td>
          <p>
            <xsl:value-of select="$label"/>
          </p>
          <p class="english-label">
            <xsl:value-of select="$english-label"/>
          </p>
        </td>
        <td>
          <p class="content">
            <xsl:choose>
              <xsl:when test="$isDate = 'true'">
                <xsl:value-of select="substring-before($value, '+')"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:choose>
                  <xsl:when test="$label = 'RefType'">
                    <xsl:value-of select="string-join(tokenize($value, '\s+'), ', ')"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="$value"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:otherwise>
            </xsl:choose>
          </p>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>
  <xsl:template name="richContent">
    <xsl:choose>
      <xsl:when test="dcc:further">
        <xsl:call-template name="richContentSection">
          <xsl:with-param name="sectionName" select="dcc:further"/>
          <xsl:with-param name="labelDe" select="'Ulteriori informazioni'"/>
          <xsl:with-param name="labelEn" select="'Further'"/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
    <xsl:choose>
      <xsl:when test="dcc:description">
        <xsl:call-template name="richContentSection">
          <xsl:with-param name="sectionName" select="dcc:description"/>
          <xsl:with-param name="labelDe" select="'Descrizione'"/>
          <xsl:with-param name="labelEn" select="'Description'"/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
    <xsl:choose>
      <xsl:when test="dcc:declaration">
        <xsl:call-template name="richContentSection">
          <xsl:with-param name="sectionName" select="dcc:declaration"/>
          <xsl:with-param name="labelDe" select="'Dichiarazione'"/>
          <xsl:with-param name="labelEn" select="'Declaration'"/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="richContentSection">
    <xsl:param name="sectionName"/>
    <xsl:param name="labelDe"/>
    <xsl:param name="labelEn"/>
    <table>
      <tr>
        <td>
          <p>
            <b>
              <xsl:value-of select="$labelDe"/>
            </b>
          </p>
          <p class="english-label">
            <b>
              <xsl:value-of select="$labelEn"/>
            </b>
          </p>
        </td>
        <td>
          <p></p>
          <p></p>
        </td>
      </tr>
      <xsl:apply-templates select="$sectionName/dcc:name"/>
      <xsl:for-each select="$sectionName/dcc:content">
        <tr>
          <td>
            <p>Contenuto
              <xsl:value-of select="position()"/>
            </p>
            <p class="english-label">Content
              <xsl:value-of select="position()"/>
            </p>
          </td>
          <td>
            <p class="content">
              <xsl:value-of select="."/>
            </p>
            <p></p>
          </td>
        </tr>
      </xsl:for-each>
      <xsl:if test="$sectionName/dcc:file">
        <xsl:for-each select="$sectionName/dcc:file">
          <tr>
            <td>
              <p>Nome file</p>
              <p class="english-label">Filename</p>
            </td>
            <td>
              <p>
                <xsl:value-of select="dcc:fileName"/>
              </p>
              <p></p>
            </td>
          </tr>
          <tr>
            <td>
              <p>MIME-Type</p>
              <p class="english-label">MIME Type</p>
            </td>
            <td>
              <p>
                <xsl:value-of select="dcc:mimeType"/>
              </p>
              <p></p>
            </td>
          </tr>
          <tr>
            <td>
              <p>Immagine</p>
              <p class="english-label">Image</p>
            </td>
            <td>
              <xsl:if test="dcc:mimeType = 'image/jpeg' or
              dcc:mimeType = 'image/png' or
              dcc:mimeType = 'image/gif' or
              dcc:mimeType = 'image/svg+xml'">
                <img class="images" src="{ concat('data:', dcc:mimeType, ';base64,', dcc:dataBase64)}"
                     alt="{dcc:fileName}"/>
              </xsl:if>
            </td>
          </tr>
        </xsl:for-each>
      </xsl:if>
    </table>
  </xsl:template>
  <xsl:template name="quantity">
    <xsl:for-each select="dcc:quantity">
      <xsl:if test="@refType or dcc:name or si:hybrid or si:real or si:realListXMLList">
        <div class="nested-wrapper">
          <div class="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th colspan="2"><xsl:value-of select="position()"/>. Grandezza di misura (Quantity)
                  </th>
                </tr>
              </thead>
              <tbody>
                <xsl:call-template name="showIfNotEmpty">
                  <xsl:with-param name="label" select="'RefType'"/>
                  <xsl:with-param name="english-label" select="'RefType'"/>
                  <xsl:with-param name="value" select="@refType"/>
                  <xsl:with-param name="isDate" select="'false'"/>
                </xsl:call-template>
                <xsl:apply-templates select="dcc:name"/>
              </tbody>
            </table>
          </div>
          <xsl:if test="si:hybrid">
            <div class="table-quantity-wrapper">
              <xsl:if test="si:hybrid/si:real">
                <xsl:call-template name="real">
                  <xsl:with-param name="path" select="si:hybrid/si:real"/>
                </xsl:call-template>
              </xsl:if>
              <xsl:if test="si:hybrid/si:realListXMLList">
                <xsl:call-template name="realListXMLList">
                  <xsl:with-param name="values" select="si:hybrid/si:realListXMLList/si:valueXMLList"/>
                  <xsl:with-param name="units" select="si:hybrid/si:realListXMLList/si:unitXMLList"/>
                </xsl:call-template>
                <xsl:apply-templates select="si:hybrid/si:realListXMLList/si:expandedUncXMLList"/>
              </xsl:if>
            </div>
          </xsl:if>
          <xsl:if test="si:realListXMLList">
            <div class="table-quantity-wrapper">
              <xsl:call-template name="realListXMLList">
                <xsl:with-param name="values" select="si:realListXMLList/si:valueXMLList"/>
                <xsl:with-param name="units" select="si:realListXMLList/si:unitXMLList"/>
              </xsl:call-template>
              <xsl:apply-templates select="si:realListXMLList/si:expandedUncXMLList"/>
            </div>
          </xsl:if>
          <xsl:if test="si:real">
            <div class="table-quantity-wrapper">
              <xsl:call-template name="real">
                <xsl:with-param name="path" select="si:real"/>
              </xsl:call-template>
            </div>
          </xsl:if>
        </div>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="real">
    <xsl:param name="path"/>
    <table class="table-quantity-metrics-vertical">
      <tr>
        <th>
          <p>Valore misurato</p>
          <p class="english-label">Measurement</p>
        </th>
        <th>
          <p>Valore</p>
          <p class="english-label">Value</p>
        </th>
        <th>
          <p>Unità</p>
          <p class="english-label">Unit</p>
        </th>
      </tr>

      <xsl:for-each select="$path">
        <tr>
          <td>
            <p>
              <xsl:value-of select="position()"/>
            </p>
          </td>
          <td>
            <xsl:value-of select="si:value"/>
          </td>
          <td>
            <xsl:value-of select="si:unit"/>
          </td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>
  <xsl:template name="realListXMLList">
    <xsl:param name="values"/>
    <xsl:param name="units"/>
    <table class="table-quantity-metrics-vertical">
      <tr>
        <th>
          <p>Valore</p>
          <p class="english-label">Value</p>
        </th>
        <th>
          <p>Unità</p>
          <p class="english-label">Unit</p>
        </th>
      </tr>
      <xsl:call-template name="splitAndPrintList">
        <xsl:with-param name="values" select="$values"/>
        <xsl:with-param name="units" select="$units"/>
      </xsl:call-template>
    </table>
  </xsl:template>
  <xsl:template name="splitAndPrintList">
    <xsl:param name="values"/>
    <xsl:param name="units"/>
    <xsl:variable name="vals" select="tokenize(normalize-space($values), '\s+')"/>
    <xsl:variable name="unts" select="tokenize(normalize-space($units), '\s+')"/>
    <xsl:for-each select="1 to count($vals)">
      <xsl:variable name="i" select="."/>
      <tr>
        <td>
          <xsl:value-of select="$vals[current()]"/>
        </td>
        <td>
          <xsl:choose>
            <xsl:when test="count($unts) = 1">
              <xsl:value-of select="$unts[1]"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$unts[$i]"/>
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>

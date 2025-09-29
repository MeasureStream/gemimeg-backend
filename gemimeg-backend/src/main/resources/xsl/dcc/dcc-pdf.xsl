<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dcc="https://ptb.de/dcc"
                xmlns:si="https://ptb.de/si"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="2.0">
  <xsl:output method="xml" indent="yes"/>
  <xsl:template match="dcc:digitalCalibrationCertificate">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="A4"
                               page-height="29.7cm"
                               page-width="21cm"
                               margin="1cm"
        >
          <fo:region-body margin-top="2.5cm" margin-bottom="2cm"/>
          <fo:region-before extent="2cm"/>
          <fo:region-after extent="1.5cm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <fo:page-sequence master-reference="A4">
        <!-- Fußzeile -->
        <fo:static-content flow-name="xsl-region-after">
          <fo:block border-top="0.5pt solid gray"
                    padding-top="4pt"
                    font-size="10pt"
                    text-align="right">
            Seite
            <fo:page-number/>
            von
            <fo:page-number-citation-last ref-id="last-page"/>
          </fo:block>
        </fo:static-content>
        <fo:flow flow-name="xsl-region-body">
          <fo:block keep-together="always">
            <fo:block font-size="26pt" font-weight="bold" space-after="3pt">
              Kalibrierzertifikat
            </fo:block>
            <fo:block font-size="14pt" font-style="italic" space-after="20pt">
              Digital Calibration Certificate
            </fo:block>
          </fo:block>
          <xsl:apply-templates select="dcc:administrativeData"/>
          <xsl:call-template name="measurementResultsSection"/>
          <!-- unsichtbarer Block am Ende für die Gesamtseitenzahl -->
          <fo:block id="last-page" font-size="0pt" height="0pt"/>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
  <xsl:template match="dcc:administrativeData">
    <fo:block space-before="20pt">
      <fo:block keep-together="always">
        <fo:block font-size="18pt" font-weight="bold">Verwaltungsdaten</fo:block>
        <fo:block font-size="10pt" font-style="italic">Administrative Data</fo:block>
      </fo:block>
    </fo:block>
    <fo:block space-before="20pt">
      <fo:block keep-together="always" keep-with-next="always">
        <fo:block font-size="14pt" font-weight="bold">DCC Software</fo:block>
        <fo:block font-size="10pt" font-style="italic">DCC Software</fo:block>
      </fo:block>
      <xsl:apply-templates select="dcc:dccSoftware"/>
    </fo:block>
    <fo:block space-before="20pt">
      <fo:block keep-together="always" keep-with-next="always">
        <fo:block font-size="14pt" font-weight="bold">Kerndaten</fo:block>
        <fo:block font-size="10pt" font-style="italic">Coredata</fo:block>
      </fo:block>
      <xsl:apply-templates select="dcc:coreData"/>
    </fo:block>
    <fo:block space-before="20pt">
      <fo:block keep-together="always" keep-with-next="always">
        <fo:block font-size="14pt" font-weight="bold">Kunde</fo:block>
        <fo:block font-size="10pt" font-style="italic">Customer</fo:block>
      </fo:block>
      <xsl:apply-templates select="dcc:customer"/>
    </fo:block>
    <fo:block space-before="20pt">
      <fo:block keep-together="always">
        <fo:block font-size="14pt" font-weight="bold">Kalibrierlabor</fo:block>
        <fo:block font-size="10pt" font-style="italic">Calibration Laboratory</fo:block>
      </fo:block>
      <xsl:apply-templates select="dcc:calibrationLaboratory"/>
    </fo:block>
    <fo:block space-before="20pt">
      <fo:block keep-together="always" keep-with-next="always">
        <fo:block font-size="14pt" font-weight="bold">Verantwortliche Person</fo:block>
        <fo:block font-size="10pt" font-style="italic">ResponsablePerson</fo:block>
      </fo:block>
      <xsl:apply-templates select="dcc:respPersons"/>
    </fo:block>
    <fo:block space-before="20pt">
      <fo:block keep-together="always" keep-with-next="always">
        <fo:block font-size="14pt" font-weight="bold">Kalibriergegenstände</fo:block>
        <fo:block font-size="10pt" font-style="italic">items</fo:block>
      </fo:block>
      <xsl:apply-templates select="dcc:items"/>
    </fo:block>
    <fo:block space-before="20pt">
      <xsl:if test="dcc:statements">
        <fo:block keep-together="always" keep-with-next="always">
          <fo:block font-size="14pt" font-weight="bold">Feststellungen</fo:block>
          <fo:block font-size="10pt" font-style="italic">Statements</fo:block>
        </fo:block>
      </xsl:if>
      <xsl:apply-templates select="dcc:statements"/>
    </fo:block>
  </xsl:template>
  <xsl:template name="measurementResultsSection">
    <fo:block keep-together="always" keep-with-next="always" space-before="20pt">
      <fo:block font-size="14pt" font-weight="bold">Messergebnisse</fo:block>
      <fo:block font-size="10pt" font-style="italic">Measure Results</fo:block>
    </fo:block>
    <xsl:apply-templates select="dcc:measurementResults"/>
  </xsl:template>
  <xsl:template match="dcc:dccSoftware | dcc:installedSoftwares | dcc:usedSoftware">
    <xsl:for-each select="dcc:software">
      <fo:block>
        <fo:table table-layout="fixed" width="80%" space-before="20pt" space-after="20pt">
          <fo:table-column column-width="50%"/>
          <fo:table-column column-width="50%"/>

          <fo:table-header>
            <fo:table-row>
              <fo:table-cell padding="4pt" border-bottom="0.5pt solid black" number-columns-spanned="2">
                <fo:block font-size="12pt" font-weight="bold" keep-with-next="always">
                  <xsl:value-of select="position()"/>. Software (Software)
                </fo:block>
              </fo:table-cell>
            </fo:table-row>
          </fo:table-header>
          <fo:table-body>
            <xsl:apply-templates select="dcc:name"/>
            <fo:table-row>
              <fo:table-cell padding="4pt">
                <fo:block keep-together="always">
                  <fo:block font-size="12pt">Version:</fo:block>
                  <fo:block font-size="10pt" font-style="italic">Release</fo:block>
                </fo:block>
              </fo:table-cell>
              <fo:table-cell padding="4pt">
                <fo:block font-size="12pt">
                  <xsl:value-of select="dcc:release"/>
                </fo:block>
              </fo:table-cell>
            </fo:table-row>
          </fo:table-body>
        </fo:table>
      </fo:block>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:coreData">
    <fo:table table-layout="fixed" width="100%" space-before="20pt" space-after="20pt">
      <fo:table-column column-width="40%"/>
      <fo:table-column column-width="60%"/>
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell padding="4pt">
            <fo:block keep-together="always">
              <fo:block font-size="12pt">Ländercode</fo:block>
              <fo:block font-size="10pt" font-style="italic">Country Code</fo:block>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt">
            <fo:block>
              <xsl:value-of select="dcc:countryCodeISO3166_1"/>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <fo:table-cell padding="4pt">
            <fo:block keep-together="always">
              <fo:block font-size="12pt">Verwendete Sprache</fo:block>
              <fo:block font-size="10pt" font-style="italic">Used Language</fo:block>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt">
            <fo:block font-size="12pt">
              <xsl:for-each select="dcc:usedLangCodeISO639_1">
                <xsl:value-of select="."/>
                <xsl:if test="position() !=last()">,</xsl:if>
              </xsl:for-each>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <fo:table-cell padding="4pt">
            <fo:block keep-together="always">
              <fo:block font-size="12pt">Pflichtsprache</fo:block>
              <fo:block font-size="10pt" font-style="italic">Mandatory Language</fo:block>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt">
            <fo:block font-size="12pt">
              <xsl:for-each select="dcc:mandatoryLangCodeISO639_1">
                <xsl:value-of select="."/>
                <xsl:if test="position() !=last()">,</xsl:if>
              </xsl:for-each>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <fo:table-cell padding="4pt">
            <fo:block keep-together="always">
              <fo:block font-size="12pt">Kalibrierzeichen</fo:block>
              <fo:block font-size="10pt" font-style="italic">Unique Identdifier</fo:block>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt">
            <fo:block font-size="12pt">
              <xsl:value-of select="dcc:uniqueIdentifier"/>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Eingangsdatum'"/>
          <xsl:with-param name="english-label" select="'Arrival date'"/>
          <xsl:with-param name="value" select="dcc:receiptDate"/>
          <xsl:with-param name="isDate" select="'true'"/>
        </xsl:call-template>
        <fo:table-row>
          <fo:table-cell padding="4pt">
            <fo:block keep-together="always">
              <fo:block font-size="12pt">Beginn der Labortätigkeit</fo:block>
              <fo:block font-size="10pt" font-style="italic">Start of lab operations</fo:block>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt">
            <fo:block font-size="12pt">
              <xsl:value-of select="substring-before(dcc:beginPerformanceDate, '+')"/>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <fo:table-cell padding="4pt">
            <fo:block keep-together="always">
              <fo:block font-size="12pt">Ende der Labortätigkeit</fo:block>
              <fo:block font-size="10pt" font-style="italic">End of lab operations</fo:block>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt">
            <fo:block font-size="12pt">
              <xsl:value-of select="substring-before(dcc:endPerformanceDate, '+')"/>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Ausführungsort'"/>
          <xsl:with-param name="english-label" select="'Place of Calibration'"/>
          <xsl:with-param name="value" select="dcc:performanceLocation"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>
      </fo:table-body>
    </fo:table>

  </xsl:template>
  <xsl:template match="dcc:customer">
    <fo:table table-layout="fixed" width="100%" space-before="10pt" space-after="10pt">
      <fo:table-column column-width="40%"/>
      <fo:table-column column-width="60%"/>
      <fo:table-body>
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
      </fo:table-body>
    </fo:table>
    <xsl:apply-templates select="dcc:location"/>
  </xsl:template>
  <xsl:template match="dcc:location">
    <fo:table table-layout="fixed" width="100%">
      <fo:table-column column-width="40%"/>
      <fo:table-column column-width="60%"/>
      <fo:table-body>
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
      </fo:table-body>
    </fo:table>
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
    <fo:table table-layout="fixed" width="100%" space-before="10pt" space-after="10pt">
      <fo:table-column column-width="40%"/>
      <fo:table-column column-width="60%"/>
      <fo:table-body>
        <xsl:call-template name="showIfNotEmpty">
          <xsl:with-param name="label" select="'Kennung'"/>
          <xsl:with-param name="english-label" select="'Calibration Laboratory Code'"/>
          <xsl:with-param name="value" select="dcc:calibrationLaboratoryCode"/>
          <xsl:with-param name="isDate" select="'false'"/>
        </xsl:call-template>
        <xsl:apply-templates select="dcc:location"/>
        <xsl:apply-templates select="dcc:contact/dcc:name"/>
        <fo:table-row>
          <fo:table-cell padding="4pt">
            <fo:block keep-together="always">
              <fo:block font-size="12pt">Telefon</fo:block>
              <fo:block font-size="10pt" font-style="italic">Phone</fo:block>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt">
            <fo:block font-size="12pt">
              <xsl:value-of select="dcc:contact/dcc:phone"/>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <fo:table-cell padding="4pt">
            <fo:block keep-together="always">
              <fo:block font-size="12pt">E-mail</fo:block>
              <fo:block font-size="10pt" font-style="italic">E-mail</fo:block>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt">
            <fo:block>
              <xsl:value-of select="dcc:contact/dcc:eMail"/>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
    <xsl:apply-templates select="dcc:contact/dcc:location"/>
  </xsl:template>
  <xsl:template match="dcc:respPersons">
    <xsl:for-each select="dcc:respPerson/dcc:person">
      <fo:table table-layout="fixed" width="80%" space-before="20pt" space-after="20pt">
        <fo:table-column column-width="50%"/>
        <fo:table-column column-width="50%"/>
        <fo:table-header>
          <fo:table-row>
            <fo:table-cell padding="4pt" border-bottom="0.5pt solid black" number-columns-spanned="2">
              <fo:block font-size="12pt" font-weight="bold">
                <xsl:value-of select="position()"/>. Person (Person)
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-header>
        <fo:table-body>
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
        </fo:table-body>
      </fo:table>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:manufacturer">
    <fo:table table-layout="fixed" width="100%" space-before="20pt" space-after="20pt">
      <fo:table-column column-width="40%"/>
      <fo:table-column column-width="60%"/>
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell padding="4pt">
            <fo:block keep-together="always" keep-with-next="always">
              <fo:block keep-with-next="always" font-size="12px" font-weight="bold">Hersteller</fo:block>
              <fo:block keep-with-previous="always" font-size="10px" font-style="italic">Manufacturer
              </fo:block>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt">
            <fo:block></fo:block>
          </fo:table-cell>
        </fo:table-row>
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
      </fo:table-body>
    </fo:table>
    <xsl:apply-templates select="dcc:location"/>
  </xsl:template>
  <xsl:template match="dcc:identifications">
    <fo:block margin-left="10pt" margin-right="10pt" space-before="20pt">
      <fo:block keep-with-next="always" font-size="12pt" font-weight="bold" padding="4pt">Identifikatoren
        (Identifications)
      </fo:block>
      <xsl:for-each select="dcc:identification">
        <fo:block>
          <fo:table table-layout="fixed" width="100%" space-before="20pt" space-after="20pt">
            <fo:table-column column-width="40%"/>
            <fo:table-column column-width="60%"/>
            <fo:table-header>
              <fo:table-row>
                <fo:table-cell padding="4pt" number-columns-spanned="2">
                  <fo:block keep-with-next="always" font-size="12pt" font-weight="bold">
                    <xsl:value-of select="position()"/>. Identifikator (Identificator)
                  </fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-header>
            <fo:table-body>
              <fo:table-row>
                <fo:table-cell padding="4pt">
                  <fo:block keep-together="always">
                    <fo:block font-size="12pt">Herausgeber</fo:block>
                    <fo:block font-size="10pt" font-style="italic">Issuer</fo:block>
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell padding="4pt">
                  <fo:block font-size="12pt">
                    <xsl:value-of select="dcc:issuer"/>
                  </fo:block>
                </fo:table-cell>
              </fo:table-row>
              <fo:table-row>
                <fo:table-cell padding="4pt">
                  <fo:block keep-together="always">
                    <fo:block font-size="12pt">Identifikator</fo:block>
                    <fo:block font-size="10pt" font-style="italic">value</fo:block>
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell padding="4pt">
                  <fo:block font-size="12pt">
                    <xsl:value-of select="dcc:value"/>
                  </fo:block>
                </fo:table-cell>
              </fo:table-row>
              <xsl:apply-templates select="dcc:name"/>
            </fo:table-body>
          </fo:table>
        </fo:block>
      </xsl:for-each>
    </fo:block>
  </xsl:template>
  <xsl:template match="dcc:items">
    <xsl:for-each select="dcc:item">
      <fo:block>
        <fo:table table-layout="fixed" width="100%" space-before="20pt" space-after="20pt">
          <fo:table-column column-width="40%"/>
          <fo:table-column column-width="60%"/>
          <fo:table-header>
            <fo:table-row>
              <fo:table-cell padding="4pt" number-columns-spanned="2">
                <fo:block font-size="12pt" font-weight="bold">
                  <xsl:value-of select="position()"/>. Kalibriergegenstand (Item)
                </fo:block>
              </fo:table-cell>
            </fo:table-row>
          </fo:table-header>
          <fo:table-body>
            <xsl:apply-templates select="dcc:name"/>
            <xsl:call-template name="showIfNotEmpty">
              <xsl:with-param name="label" select="'Modell'"/>
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
          </fo:table-body>
        </fo:table>
      </fo:block>
      <xsl:call-template name="richContent"/>
      <xsl:apply-templates select="dcc:manufacturer"/>
      <xsl:apply-templates select="dcc:identifications"/>
      <xsl:if test="dcc:installedSoftwares">
        <fo:block keep-together="always" keep-with-next="always" space-before="20pt" padding="4pt">
          <fo:block font-size="12pt" font-weight="bold">Installierte Software</fo:block>
          <fo:block font-size="10pt" font-weight="italic">(Installed Software)</fo:block>
          <fo:block>
            <xsl:apply-templates select="dcc:installedSoftwares"/>
          </fo:block>
        </fo:block>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:statements | dcc:measurementMetaData">

    <xsl:variable name="title">
      <xsl:choose>
        <xsl:when test="self::dcc:statements">Feststellung (Statement)</xsl:when>
        <xsl:otherwise>Metadaten (Meta Data)</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:for-each select="dcc:statement | dcc:metaData">
      <fo:block keep-with-previous="always" keep-with-next="always" font-size="12pt" font-weight="bold"
                space-before="20pt" space-after="10pt">
        <xsl:value-of select="position()"/>.
        <xsl:value-of select="$title"/>
      </fo:block>
      <fo:block>
        <xsl:if test="@refType">
          <fo:table table-layout="fixed" width="100%">
            <fo:table-column column-width="40%"/>
            <fo:table-column column-width="60%"/>
            <fo:table-body>
              <xsl:call-template name="showIfNotEmpty">
                <xsl:with-param name="label" select="'RefType'"/>
                <xsl:with-param name="english-label" select="'RefType'"/>
                <xsl:with-param name="value" select="@refType"/>
                <xsl:with-param name="isDate" select="'false'"/>
              </xsl:call-template>
            </fo:table-body>
          </fo:table>
        </xsl:if>
      </fo:block>

      <xsl:if test="dcc:name">
        <fo:table table-layout="fixed" width="100%">
          <fo:table-column column-width="40%"/>
          <fo:table-column column-width="60%"/>
          <fo:table-body>
            <xsl:apply-templates select="dcc:name"/>
          </fo:table-body>
        </fo:table>
      </xsl:if>
      <xsl:call-template name="richContent"/>
      <xsl:if test="dcc:countryCodeISO3166_1 or
                    dcc:convention or
                    dcc:reference or
                    dcc:norm or
                    dcc:date or
                    dcc:period or
                    dcc:conformity or
                    dcc:nonSIUnit or
                    dcc:nonSIDefinition or
                    dcc:traceable or
                    dcc:valid">
        <fo:table table-layout="fixed" width="100%">
          <fo:table-column column-width="40%"/>
          <fo:table-column column-width="60%"/>
          <fo:table-body>

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
          </fo:table-body>
        </fo:table>
      </xsl:if>
      <xsl:apply-templates select="dcc:location"/>
      <xsl:apply-templates select="dcc:respAuthority"/>
      <xsl:apply-templates select="dcc:data"/>

    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:data">
    <fo:block>
      <xsl:if test="dcc:quantity">
        <xsl:call-template name="quantity"/>
      </xsl:if>
      <xsl:if test="dcc:list">
        <xsl:apply-templates select="dcc:list"/>
      </xsl:if>
    </fo:block>
  </xsl:template>
  <xsl:template match="dcc:measurementResults">
    <xsl:for-each select="dcc:measurementResult">
      <fo:block keep-with-next="always" font-size="12pt" font-weight="bold" padding="4pt" space-before="20pt"
                space-after="20pt">
        <xsl:value-of select="position()"/>. Messergebnis (Measurement Results)
      </fo:block>
      <fo:block>
        <xsl:if test="dcc:name">
          <fo:table table-layout="fixed" width="100%" space-before="20pt" space-after="20pt">
            <fo:table-column column-width="40%"/>
            <fo:table-column column-width="60%"/>
            <fo:table-body>
              <xsl:apply-templates select="dcc:name"/>
            </fo:table-body>
          </fo:table>
        </xsl:if>
      </fo:block>
      <xsl:if test="dcc:usedSoftware">
        <fo:block keep-together="always" space-before="20pt" space-after="20pt">
          <fo:block font-size="12pt" font-weight="bold" padding="4pt">
            Verwendete Software
          </fo:block>
          <fo:block font-size="12pt" font-weight="bold" padding="4pt">
            Used Software
          </fo:block>
        </fo:block>
        <xsl:apply-templates select="dcc:usedSoftware"/>
      </xsl:if>
      <xsl:if test="dcc:usedMethods">
        <fo:block keep-together="always" keep-with-next="always" space-before="20pt" space-after="20pt">
          <fo:block font-size="12pt" font-weight="bold" padding="4pt">
            Methoden
          </fo:block>
          <fo:block font-size="10pt" font-style="italic" padding="4pt">
            Methods
          </fo:block>
        </fo:block>
        <xsl:apply-templates select="dcc:usedMethods"/>
      </xsl:if>
      <xsl:if test="dcc:influenceConditions">
        <fo:block keep-together="always" keep-with-next="always" space-before="20pt" space-after="20pt">
          <fo:block font-size="12pt" font-weight="bold" padding="4pt">
            Einflussfaktoren
          </fo:block>
          <fo:block font-size="10pt" font-style="italic" padding="4pt">
            Influence Condition
          </fo:block>
        </fo:block>
        <xsl:apply-templates select="dcc:influenceConditions"/>
      </xsl:if>
      <xsl:if test="dcc:results">
        <fo:block keep-together="always" space-before="20pt" space-after="20pt">
          <fo:block font-size="12pt" font-weight="bold" padding="4pt">
            Ergebnisse
          </fo:block>
          <fo:block font-size="10pt" font-style="italic" padding="4pt">
            Results
          </fo:block>
        </fo:block>
        <xsl:apply-templates select="dcc:results"/>
      </xsl:if>
      <xsl:if test="dcc:measurementMetaData">
        <fo:block keep-together="always" space-before="20pt" space-after="20pt">
          <fo:block font-size="12pt" font-weight="bold" padding="4pt">
            Metadaten
          </fo:block>
          <fo:block font-size="10pt" font-style="italic" padding="4pt">
            Meta Data
          </fo:block>
        </fo:block>
        <xsl:apply-templates select="dcc:measurementMetaData"/>

      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:usedMethods">
    <xsl:for-each select="dcc:usedMethod">
      <fo:table table-layout="fixed" width="100%" space-before="20pt" space-after="20pt">
        <fo:table-column column-width="40%"/>
        <fo:table-column column-width="60%"/>
        <fo:table-header>
          <fo:table-row>
            <fo:table-cell padding="4pt" number-columns-spanned="2">
              <fo:block keep-with-next="auto" font-size="12pt" font-weight="bold" margin-bottom="10pt">
                <xsl:value-of select="position()"/>. Verwendete Methode (Used Method)
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-header>
        <fo:table-body>
          <xsl:call-template name="showIfNotEmpty">
            <xsl:with-param name="label" select="'RefType'"/>
            <xsl:with-param name="english-label" select="'RefType'"/>
            <xsl:with-param name="value" select="@refType"/>
            <xsl:with-param name="isDate" select="'false'"/>
          </xsl:call-template>
          <xsl:apply-templates select="dcc:name"/>
          <xsl:if test="dcc:norm">
            <fo:table-row>
              <fo:table-cell padding="4pt">
                <fo:block keep-together="always">
                  <fo:block font-size="12pt">
                    Norm
                  </fo:block>
                  <fo:block font-size="10pt" font-style="italic">
                    Norm
                  </fo:block>
                </fo:block>
              </fo:table-cell>
              <fo:table-cell padding="4pt">
                <fo:block font-size="12pt">
                  <xsl:value-of select="dcc:norm"/>
                </fo:block>
              </fo:table-cell>
            </fo:table-row>
          </xsl:if>
        </fo:table-body>
      </fo:table>
      <xsl:call-template name="richContent"/>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:influenceConditions">
    <xsl:for-each select="dcc:influenceCondition">
      <fo:table table-layout="fixed" width="100%">
        <fo:table-column column-width="40%"/>
        <fo:table-column column-width="60%"/>
        <fo:table-header>
          <fo:table-row>
            <fo:table-cell number-columns-spanned="2">
              <fo:block keep-together="always" keep-with-next="always" font-size="12pt" font-weight="bold"
                        margin-bottom="10pt">
                <xsl:value-of select="position()"/>. Einflussfaktor (Influence Condition)
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-header>
        <fo:table-body>
          <xsl:call-template name="showIfNotEmpty">
            <xsl:with-param name="label" select="'RefType'"/>
            <xsl:with-param name="english-label" select="'RefType'"/>
            <xsl:with-param name="value" select="@refType"/>
            <xsl:with-param name="isDate" select="'false'"/>
          </xsl:call-template>
          <xsl:apply-templates select="dcc:name"/>
          <xsl:if test="dcc:status">
            <fo:table-row>
              <fo:table-cell padding="4pt">
                <fo:block keep-together="always">
                  <fo:block font-size="12pt">
                    Status
                  </fo:block>
                  <fo:block font-size="10pt" font-style="italic">
                    status
                  </fo:block>
                </fo:block>
              </fo:table-cell>
              <fo:table-cell padding="4pt">
                <fo:block font-size="12pt">
                  <xsl:value-of select="dcc:status"/>
                </fo:block>
              </fo:table-cell>
            </fo:table-row>
          </xsl:if>
        </fo:table-body>
      </fo:table>
      <xsl:apply-templates select="dcc:data"/>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:results">
    <xsl:for-each select="dcc:result">
      <fo:table table-layout="fixed" width="100%" space-before="20pt" space-after="20pt">
        <fo:table-column column-width="40%"/>
        <fo:table-column column-width="60%"/>
        <fo:table-header>
          <fo:table-row>
            <fo:table-cell number-columns-spanned="2">
              <fo:block keep-together="always" font-size="12pt" font-weight="bold">
                <xsl:value-of select="position()"/>. Ergebnis (Result)
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-header>
        <fo:table-body>
          <xsl:call-template name="showIfNotEmpty">
            <xsl:with-param name="label" select="'RefType'"/>
            <xsl:with-param name="english-label" select="'RefType'"/>
            <xsl:with-param name="value" select="@refType"/>
            <xsl:with-param name="isDate" select="'false'"/>
          </xsl:call-template>

          <xsl:apply-templates select="dcc:name"/>
        </fo:table-body>
      </fo:table>
      <xsl:apply-templates select="dcc:data"/>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:respAuthority">
    <fo:table table-layout="fixed" width="100%" space-before="10pt" space-after="10pt">
      <fo:table-column column-width="40%"/>
      <fo:table-column column-width="60%"/>
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell padding="4pt">
            <fo:block font-size="12pt" font-weight="bold">Verantwortliche Stelle</fo:block>
            <fo:block font-size="10pt" font-style="italic">Responsible Authority</fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt">
            <fo:block></fo:block>
          </fo:table-cell>
        </fo:table-row>
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

      </fo:table-body>
    </fo:table>
    <!--    todo: hier wieder einschalten wenn location gefixed ist-->
    <xsl:apply-templates select="dcc:location"/>
  </xsl:template>
  <xsl:template match="dcc:list">
    <xsl:call-template name="quantity"/>
  </xsl:template>
  <xsl:template match="si:expandedUncXMLList">
    <fo:block font-size="12pt" font-weight="bold" space-before="20pt">
      Expanded Uncertainty
    </fo:block>
    <fo:table table-layout="fixed" width="100%" space-before="20pt" border="1pt solid black">
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell padding="4pt" border="1pt solid black" background-color="#E0E0E0"
                         text-align="center">
            <fo:block>Uncertainty</fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt" border="1pt solid black" text-align="center">
            <fo:block>
              <xsl:value-of select="si:uncertaintyXMLList"/>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>

        <fo:table-row>
          <fo:table-cell padding="4pt" border="1pt solid black" background-color="#E0E0E0"
                         text-align="center">
            <fo:block>Coverage Factor</fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt" border="1pt solid black" text-align="center">
            <fo:block>
              <xsl:value-of select="si:coverageFactorXMLList"/>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <fo:table-cell padding="4pt" border="1pt solid black" background-color="#E0E0E0"
                         text-align="center">
            <fo:block>Coverage Probability</fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt" border="1pt solid black" text-align="center">
            <fo:block>
              <xsl:value-of select="si:coverageProbabilityXMLList"/>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <fo:table-cell padding="4pt" border="1pt solid black" background-color="#E0E0E0"
                         text-align="center">
            <fo:block>Distribution</fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt" border="1pt solid black" text-align="center">
            <fo:block>
              <xsl:value-of select="si:distributionXMLList"/>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
  </xsl:template>

  <xsl:template name="showIfNotEmpty">
    <xsl:param name="label"/>
    <xsl:param name="english-label"/>
    <xsl:param name="value"/>
    <xsl:param name="isDate"/>
    <xsl:param name="lang"/>
    <xsl:if test="string($value) != ''">
      <fo:table-row>
        <fo:table-cell padding="4pt">
          <fo:block keep-together="always">
            <fo:block font-size="12pt">
              <xsl:value-of select="$label"/>
            </fo:block>
            <fo:block font-size="10pt" font-style="italic">
              <xsl:value-of select="$english-label"/>
            </fo:block>
          </fo:block>
        </fo:table-cell>
        <fo:table-cell padding="4pt">
          <fo:block font-size="12pt">
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
          </fo:block>
        </fo:table-cell>
      </fo:table-row>
    </xsl:if>
  </xsl:template>
  <xsl:template name="richContent">
    <xsl:choose>
      <xsl:when test="dcc:further">
        <xsl:call-template name="richContentSection">
          <xsl:with-param name="sectionName" select="dcc:further"/>
          <xsl:with-param name="labelDe" select="'Weitere Information'"/>
          <xsl:with-param name="labelEn" select="'Further'"/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
    <xsl:choose>
      <xsl:when test="dcc:description">
        <xsl:call-template name="richContentSection">
          <xsl:with-param name="sectionName" select="dcc:description"/>
          <xsl:with-param name="labelDe" select="'Beschreibung'"/>
          <xsl:with-param name="labelEn" select="'Description'"/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
    <xsl:choose>
      <xsl:when test="dcc:declaration">
        <xsl:call-template name="richContentSection">
          <xsl:with-param name="sectionName" select="dcc:declaration"/>
          <xsl:with-param name="labelDe" select="'Deklaration'"/>
          <xsl:with-param name="labelEn" select="'Declaration'"/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="richContentSection">
    <xsl:param name="sectionName"/>
    <xsl:param name="labelDe"/>
    <xsl:param name="labelEn"/>
    <fo:table table-layout="fixed" width="100%">

      <fo:table-column column-width="40%"/>
      <fo:table-column column-width="60%"/>
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell padding="4pt">
            <fo:block keep-together="always" keep-with-next="always">
              <fo:block font-size="12pt" font-weight="bold">
                <xsl:value-of select="$labelDe"/>
              </fo:block>
              <fo:block font-size="10pt" font-weight="bold">
                <xsl:value-of select="$labelEn"/>
              </fo:block>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell padding="4pt">
            <fo:block/>
            <fo:block/>
          </fo:table-cell>
        </fo:table-row>
        <xsl:apply-templates select="$sectionName/dcc:name"/>
        <xsl:for-each select="$sectionName/dcc:content">

          <fo:table-row>

            <fo:table-cell padding="4pt">
              <fo:block keep-together="always">
                <fo:block font-size="12pt">Inhalt
                  <xsl:value-of select="position()"/>
                </fo:block>
                <fo:block font-size="10pt" font-style="italic">Content
                  <xsl:value-of select="position()"/>
                </fo:block>
              </fo:block>
            </fo:table-cell>
            <fo:table-cell padding="4pt">

              <fo:block font-size="12pt">
                <xsl:value-of select="."/>
              </fo:block>
              <fo:block>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </xsl:for-each>
        <xsl:if test="$sectionName/dcc:file">

          <xsl:for-each select="$sectionName/dcc:file">
            <fo:table-row>
              <fo:table-cell padding="4pt">
                <fo:block keep-together="always">
                  <fo:block font-size="12pt">Dateiname</fo:block>
                  <fo:block font-size="10pt" font-style="italic">Filename</fo:block>
                </fo:block>
              </fo:table-cell>
              <fo:table-cell padding="4pt">
                <fo:block>
                  <xsl:value-of select="dcc:fileName"/>
                </fo:block>
                <fo:block></fo:block>
              </fo:table-cell>
            </fo:table-row>
            <fo:table-row>
              <fo:table-cell padding="4pt">
                <fo:block font-size="12pt">
                  MIME-Type
                </fo:block>
                <fo:block font-size="10pt">
                  MIME Type
                </fo:block>
              </fo:table-cell>
              <fo:table-cell padding="4pt">
                <fo:block font-size="12pt">
                  <xsl:value-of select="dcc:mimeType"/>
                </fo:block>
              </fo:table-cell>
            </fo:table-row>
            <fo:table-row>
              <fo:table-cell padding="4pt">
                <fo:block font-size="12pt">
                  Bild
                </fo:block>
                <fo:block font-size="10pt">
                  Image
                </fo:block>
              </fo:table-cell>
              <fo:table-cell padding="4pt">
                <fo:block font-size="12pt">

                  <xsl:if test="dcc:mimeType = 'image/jpeg' or
                                dcc:mimeType = 'image/png' or
                                dcc:mimeType = 'image/gif' or
                                dcc:mimeType = 'image/svg+xml'">
                    <fo:block>
                      <fo:external-graphic
                          src="{concat('data:image/svg+xml;base64,', dcc:dataBase64)}"
                          content-height="50px"/>
                    </fo:block>
                  </xsl:if>
                </fo:block>
              </fo:table-cell>
            </fo:table-row>
          </xsl:for-each>
        </xsl:if>

      </fo:table-body>
    </fo:table>
  </xsl:template>
  <xsl:template name="quantity">
    <xsl:for-each select="dcc:quantity">
      <xsl:if test="@refType or dcc:name or si:hybrid or si:real or si:realListXMLList">
        <fo:block space-before="20pt" space-after="25pt" margin-right="8pt" margin-left="8pt">
          <fo:block>

            <fo:table table-layout="fixed" width="100%" space-before="5pt" space-after="10pt">
              <fo:table-column column-width="40%"/>
              <fo:table-column column-width="60%"/>
              <fo:table-header>
                <fo:table-row>
                  <fo:table-cell padding="4pt" number-columns-spanned="2">
                    <fo:block font-size="12pt" font-weight="bold" border-bottom="1pt solid black"
                              margin-bottom="10pt"><xsl:value-of select="position()"/>. Messgröße
                      (Quantity)
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-header>
              <fo:table-body>
                <xsl:call-template name="showIfNotEmpty">
                  <xsl:with-param name="label" select="'RefType'"/>
                  <xsl:with-param name="english-label" select="'RefType'"/>
                  <xsl:with-param name="value" select="@refType"/>
                  <xsl:with-param name="isDate" select="'false'"/>
                </xsl:call-template>
                <xsl:apply-templates select="dcc:name"/>
                <fo:table-row>
                  <fo:table-cell number-columns-spanned="2">
                    <fo:block>&#160;</fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:block>
          <xsl:if test="si:hybrid">
            <xsl:if test="si:hybrid/si:real">
              <xsl:call-template name="real">
                <xsl:with-param name="path" select="si:hybrid/si:real"/>
              </xsl:call-template>
            </xsl:if>
          </xsl:if>
          <xsl:if test="si:hybrid/si:realListXMLList">
            <xsl:call-template name="realListXMLList">
              <xsl:with-param name="values" select="si:hybrid/si:realListXMLList/si:valueXMLList"/>
              <xsl:with-param name="units" select="si:hybrid/si:realListXMLList/si:unitXMLList"/>
            </xsl:call-template>
            <xsl:apply-templates select="si:hybrid/si:realListXMLList/si:expandedUncXMLList"/>
          </xsl:if>
          <xsl:if test="si:real">
            <fo:block>
              <xsl:call-template name="real">
                <xsl:with-param name="path" select="si:real"/>
              </xsl:call-template>
            </fo:block>
          </xsl:if>

        </fo:block>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  <xsl:template name="real">
    <xsl:param name="path"/>
    <fo:block width="100%" space-before="10pt">
      <fo:block padding="0pt" margin-left="2.5%" margin-right="2.5%">
        <fo:table table-layout="fixed" width="100%" border="1pt solid black">
          <fo:table-header>
            <fo:table-row>
              <fo:table-cell padding="4pt" border="1px solid black" background-color="#E0E0E0"
                             text-align="center">
                <fo:block font-size="12pt" font-weight="bold">Messwert</fo:block>
                <fo:block font-size="10pt" font-style="italic">Measurement</fo:block>
              </fo:table-cell>
              <fo:table-cell padding="4pt" border="1px solid black" background-color="#E0E0E0"
                             text-align="center">
                <fo:block font-size="12pt" font-weight="bold">Wert</fo:block>
                <fo:block font-size="10pt" font-style="italic">Value</fo:block>
              </fo:table-cell>
              <fo:table-cell padding="4pt" border="1px solid black" background-color="#E0E0E0"
                             text-align="center">
                <fo:block font-size="12pt" font-weight="bold">Einheit</fo:block>
                <fo:block font-size="10pt" font-style="italic">Unit</fo:block>
              </fo:table-cell>
            </fo:table-row>
          </fo:table-header>
          <fo:table-body>
            <xsl:for-each select="$path">
              <fo:table-row>
                <fo:table-cell padding="4pt" border="1pt solid black" text-align="center">
                  <fo:block>
                    <xsl:value-of select="position()"/>
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell padding="4pt" border="1pt solid black" text-align="center">
                  <fo:block>
                    <xsl:value-of select="si:value"/>
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell padding="4pt" border="1pt solid black" text-align="center">
                  <fo:block>
                    <xsl:value-of select="si:unit"/>
                  </fo:block>
                </fo:table-cell>
              </fo:table-row>
            </xsl:for-each>
          </fo:table-body>
        </fo:table>
      </fo:block>
    </fo:block>
  </xsl:template>
  <xsl:template name="realListXMLList">
    <xsl:param name="values"/>
    <xsl:param name="units"/>
    <fo:block width="100%" space-before="10pt">
      <fo:block padding="0pt" margin-left="2.5%" margin-right="2.5%">
        <fo:table table-layout="fixed" width="100%" border="1pt solid black">
          <fo:table-column column-width="40%"/>
          <fo:table-column column-width="60%"/>
          <fo:table-body>
            <fo:table-row>
              <fo:table-cell padding="4pt" font-weight="bold" border="1pt solid black"
                             background-color="#E0E0E0"
                             text-align="center">
                <fo:block font-size="12pt">
                  Wert
                </fo:block>
                <fo:block font-size="10pt" font-style="italic">
                  Value
                </fo:block>
              </fo:table-cell>
              <fo:table-cell padding="4pt" font-weight="bold" border="1pt solid black"
                             background-color="#E0E0E0"
                             text-align="center">
                <fo:block font-size="12pt">
                  Einheit
                </fo:block>
                <fo:block font-size="10pt" font-style="italic">
                  Unit
                </fo:block>
              </fo:table-cell>
            </fo:table-row>
            <xsl:call-template name="splitAndPrintList">
              <xsl:with-param name="values" select="$values"/>
              <xsl:with-param name="units" select="$units"/>
            </xsl:call-template>
          </fo:table-body>
        </fo:table>
      </fo:block>
    </fo:block>
  </xsl:template>
  <xsl:template name="splitAndPrintList">
    <xsl:param name="values"/>
    <xsl:param name="units"/>

    <xsl:variable name="vals" select="tokenize(normalize-space($values), '\s+')"/>
    <xsl:variable name="unts" select="tokenize(normalize-space($units), '\s+')"/>

    <xsl:for-each select="1 to count($vals)">
      <xsl:variable name="i" select="."/>
      <fo:table-row>
        <fo:table-cell padding="4pt" border="1pt solid black" text-align="center">
          <fo:block>
            <xsl:value-of select="$vals[current()]"/>
          </fo:block>
        </fo:table-cell>
        <fo:table-cell padding="4pt" border="1pt solid black" text-align="center">
          <fo:block>
            <xsl:choose>
              <xsl:when test="count($unts) = 1">
                <xsl:value-of select="$unts[1]"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="$unts[$i]"/>
              </xsl:otherwise>
            </xsl:choose>
          </fo:block>
        </fo:table-cell>
      </fo:table-row>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>

<!--
  Copyright 2025 Physikalisch-Technische Bundesanstalt

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

  3. Neither the name of the copyright holder nor the names of its contributors
  may be used to endorse or promote products derived from this software without
  specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
  OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
  OF THE POSSIBILITY OF SUCH DAMAGE.
-->
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
        <link rel="stylesheet" href="{$base}assets/dcc.css"/>
      </head>
      <body>
        <div class="w-100">
          <div class="text-center">
            <p class="heading">Kalibrierschein</p>
            <i>Calibration Certificate</i>
          </div>
          <xsl:apply-templates select="dcc:administrativeData"/>
          <div class="page-break"/>
          <xsl:apply-templates select="dcc:administrativeData/dcc:items"/>
          <xsl:apply-templates select="dcc:administrativeData/dcc:statements"/>
          <xsl:apply-templates select="dcc:measurementResults"/>
          <div class="page-break"/>
          <xsl:call-template name="cipm_mra"/>
        </div>
        <script>
          MathJax = {
          tex: {
          inlineMath: [['$', '$'], ['\\(', '\\)']]
          }
          }
        </script>
        <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js"/>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="dcc:items">
    <xsl:variable name="itemsLength" select="count(dcc:item)"/>
    <xsl:for-each select="dcc:item">
      <xsl:if test="$itemsLength > 1">
        <p class="sub-heading">Gegenstand
          <xsl:value-of select="position()"/>
        </p>
        <p class="description-secondary mb-default">Object
          <xsl:value-of select="position()"/>
        </p>
      </xsl:if>
      <xsl:apply-templates select="dcc:description"/>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:administrativeData">
    <table class="administrative-data-table">
      <xsl:variable name="itemsLength" select="count(dcc:items/dcc:item)"/>
      <xsl:for-each select="dcc:items/dcc:item">
        <xsl:if test="$itemsLength > 1">
          <tr class="p-row-bottom">
            <td>
              <p>
                <b>
                  Gegenstand
                  <xsl:value-of select="position()"/>
                </b>
              </p>
              <p>
                Object
                <xsl:value-of select="position()"/>
              </p>
            </td>
          </tr>
        </xsl:if>
        <tr class="p-row-bottom">
          <td>
            <p>Gegenstand:</p>
            <p>Object:</p>
          </td>
          <td>
            <p>
              <xsl:value-of select="dcc:name/dcc:content[@lang=$primaryLang]"/>
            </p>
            <p>
              <xsl:value-of select="dcc:name/dcc:content[@lang='en']"/>
            </p>
          </td>
        </tr>
        <tr class="p-row-bottom">
          <td>
            <p>Hersteller:</p>
            <p>Manufacturer:</p>
          </td>
          <td>
            <p>
              <xsl:value-of select="dcc:manufacturer/dcc:name/dcc:content"/>
            </p>
            <xsl:apply-templates select="dcc:manufacturer/dcc:location"/>
            <p>
              <xsl:value-of select="dcc:manufacturer/dcc:eMail"/>
            </p>
          </td>
        </tr>
        <xsl:if test="dcc:model != ''">
          <tr>
            <td>
              <p>Typ:</p>
              <p>Type:</p>
            </td>
            <td>
              <p>
                <xsl:value-of select="dcc:model"/>
              </p>
            </td>
          </tr>
        </xsl:if>
        <xsl:for-each select="dcc:identifications/dcc:identification">
          <tr>
            <td>
              <p>
                <xsl:value-of select="dcc:description/dcc:content[@lang=$primaryLang]"/>
              </p>
              <p>
                <xsl:value-of select="dcc:description/dcc:content[@lang=$secondaryLang]"/>
              </p>
            </td>
            <td>
              <p>
                <xsl:value-of select="dcc:value"/>
              </p>
            </td>
          </tr>
        </xsl:for-each>
        <tr class="p-row-bottom">
          <td/>
        </tr>
      </xsl:for-each>
      <tr>
        <td>
          <p>Auftraggeber:</p>
          <p>Customer:</p>
        </td>
        <td>
          <p>
            <xsl:value-of select="dcc:customer/dcc:name/dcc:content"/>
          </p>
          <p style="white-space: pre-line;">
            <xsl:value-of select="dcc:customer/dcc:location/dcc:further/dcc:content[@lang=$primaryLang]"/>
          </p>
          <xsl:apply-templates select="dcc:customer/dcc:location"/>
        </td>
      </tr>
      <tr>
        <td>
          <p>Kalibrierzeichen:</p>
          <p>Calibration mark:</p>
        </td>
        <td>
          <p>
            <xsl:value-of select="dcc:coreData/dcc:uniqueIdentifier"/>
          </p>
        </td>
      </tr>
      <tr>
        <td>
          <p>Ort der Kalibrierung:</p>
          <p>Location of calibration:</p>
        </td>
        <td>
          <p>
            <xsl:value-of select="dcc:calibrationLaboratory/dcc:contact/dcc:name/dcc:content"/>
          </p>
        </td>
      </tr>
      <tr>
        <td>
          <p>Datum der Kalibrierung:</p>
          <p>Date of calibration:</p>
        </td>
        <td>
          <p>
            <xsl:choose>
              <xsl:when
                  test="dcc:coreData/dcc:beginPerformanceDate = dcc:coreData/dcc:endPerformanceDate">
                <xsl:value-of select="dcc:coreData/dcc:beginPerformanceDate"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="dcc:coreData/dcc:beginPerformanceDate"/>bis
                <xsl:value-of select="dcc:coreData/dcc:endPerformanceDate"/>
              </xsl:otherwise>
            </xsl:choose>
          </p>
        </td>
      </tr>
      <tr class="p-row-bottom">
        <td>
          <p>Im Auftrag</p>
          <p>On behalf of</p>
        </td>
        <td>
        </td>
        <td>
          <p>Im Auftrag</p>
          <p class="description-secondary">On behalf of</p>
        </td>
      </tr>
      <tr>
        <td class="v-align-bottom">
          <p>
            <xsl:call-template name="textBlock">
              <xsl:with-param name="textBlock"
                              select="dcc:respPersons/dcc:respPerson[1]/dcc:person/dcc:name"/>
            </xsl:call-template>
          </p>
        </td>
        <td class="td-translated">
          <p>Siegel</p>
          <p>Seal</p>
        </td>
        <td class="v-align-bottom">
          <p>
            <xsl:call-template name="textBlock">
              <xsl:with-param name="textBlock"
                              select="dcc:respPersons/dcc:respPerson[2]/dcc:person/dcc:name"/>
            </xsl:call-template>
          </p>
        </td>
      </tr>
    </table>
  </xsl:template>
  <xsl:template match="dcc:location">
    <p>
      <xsl:value-of select="dcc:street"/>&#160;
      <xsl:value-of select="dcc:streetNo"/>
    </p>
    <p>
      <xsl:value-of select="dcc:postCode"/>&#160;
      <xsl:value-of select="dcc:city"/>
    </p>
  </xsl:template>
  <xsl:template match="dcc:statements">
    <xsl:variable name="statementLength" select="count(dcc:statement/dcc:declaration)"/>
    <xsl:if test="$statementLength > 0">
      <div class="mb-default">
        <p class="sub-heading">
          Statements
        </p>
        <p class="description-secondary mb-default">
          Statements
        </p>
        <xsl:for-each select="dcc:statement">
          <xsl:for-each select="dcc:declaration/dcc:content">
            <p>
              <xsl:choose>
                <xsl:when test="@lang=$secondaryLang">
                  <i>
                    <span style='font-size:8.0pt'>
                      <xsl:value-of select="."/>
                    </span>
                  </i>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="."/>
                </xsl:otherwise>
              </xsl:choose>
            </p>
          </xsl:for-each>
        </xsl:for-each>
      </div>
    </xsl:if>
  </xsl:template>
  <xsl:template match="dcc:influenceCondition">
    <div class="avoid-break">
      <p class="sub-heading">
        <xsl:value-of select="dcc:name/dcc:content[@lang=$primaryLang]"/>
      </p>
      <p class="description-secondary mb-default">
        <xsl:value-of select="dcc:name/dcc:content[@lang=$secondaryLang]"/>
      </p>
      <xsl:for-each select="dcc:data/dcc:list">
        <p class="description">
          <xsl:value-of select="dcc:name/dcc:content[@lang=$primaryLang]"/>
        </p>
        <p class="description-secondary">
          <xsl:value-of select="dcc:name/dcc:content[@lang=$secondaryLang]"/>
        </p>
        <table class="dcc-table mb-default w-100">
          <thead>
            <tr>
              <td>&#160;</td>
              <td class="text-center">
                <p>von</p>
                <p class="description-secondary">from</p>
              </td>
              <td class="text-center">
                <p>bis</p>
                <p class="description-secondary">to</p>
              </td>
              <td class="text-center">
                <p>Unsicherheit</p>
                <p class="description-secondary">uncertainty</p>
                <span><i>k</i>=2
                </span>
              </td>
            </tr>
          </thead>
          <tbody>
            <xsl:for-each select="dcc:list">
              <tr>
                <td>
                  <p class="mb-1">
                    <xsl:value-of select="dcc:name/dcc:content[@lang=$primaryLang]"/>
                    /
                    <xsl:apply-templates select="dcc:quantity[1]/si:real/si:unit"/>
                  </p>
                  <p class="description-secondary">
                    <xsl:value-of select="dcc:name/dcc:content[@lang=$secondaryLang]"/>
                    /
                    <xsl:apply-templates select="dcc:quantity[1]/si:real/si:unit"/>
                  </p>
                </td>
                <td class="text-center">
                  <xsl:value-of
                      select="dcc:quantity[dcc:name/dcc:content[@lang=$primaryLang]='von']/si:real/si:value"/>
                </td>
                <td class="text-center">
                  <xsl:value-of
                      select="dcc:quantity[dcc:name/dcc:content[@lang=$primaryLang]='bis']/si:real/si:value"/>
                </td>
                <td class="text-center">
                  <xsl:value-of select="dcc:quantity/si:real/si:expandedUnc/si:uncertainty"/>
                </td>
              </tr>
            </xsl:for-each>
          </tbody>
        </table>
      </xsl:for-each>
    </div>
  </xsl:template>
  <xsl:template match="dcc:measurementResults">
    <xsl:for-each select="dcc:measurementResult">
      <xsl:apply-templates select="dcc:usedMethods"/>
      <xsl:apply-templates select="dcc:influenceConditions/dcc:influenceCondition"/>
      <p class="sub-heading">Messergebnisse</p>
      <p class="description-secondary mb-default">Measurement results</p>
      <xsl:for-each select="dcc:results/dcc:result">
        <xsl:variable name="result" select="."/>
        <xsl:apply-templates select="$result"/>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="dcc:result">
    <div class="avoid-break">
      <p class="text-bold">
        <xsl:value-of select="dcc:name/dcc:content[@lang=$primaryLang]"/>
      </p>
      <p class="description-secondary mb-default">
        <xsl:value-of select="dcc:name/dcc:content[@lang=$secondaryLang]"/>
      </p>
      <xsl:apply-templates select="dcc:description"/>
      <xsl:choose>
        <xsl:when
            test="dcc:data/dcc:list/dcc:list != 0 and count(dcc:data/dcc:list/dcc:list/dcc:name) &lt; count(dcc:data/dcc:list/dcc:list[1]/dcc:quantity)">
          <!-- Vertical Table Rows because there are more quantities than headers -->
          <xsl:call-template name="verticalTable">
            <xsl:with-param name="data" select="dcc:data"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:when
            test="dcc:data/dcc:list/dcc:list != 0 and count(dcc:data/dcc:list/dcc:list/dcc:name) &gt;= count(dcc:data/dcc:list/dcc:list[1]/dcc:quantity)">
          <!-- Horizontal Table Rows because there are more headers than quantities -->
          <xsl:call-template name="horizontalTable">
            <xsl:with-param name="data" select="dcc:data"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="dcc:data/dcc:list/dcc:quantity">
          <!-- Table with one row of quantities -->
          <xsl:call-template name="oneRowQuantitiesTable">
            <xsl:with-param name="data" select="dcc:data"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <div class="alert">
            Unsupported Table Structure
          </div>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>
  <xsl:template name="verticalTable">
    <xsl:param name="data"/>
    <div class="flex mb-default">
      <xsl:for-each select="$data/dcc:list/dcc:list">
        <div class="col">
          <!-- Table Headers-->
          <div class="cell">
            <xsl:call-template name="listName">
              <xsl:with-param name="name" select="dcc:name"/>
            </xsl:call-template>
          </div>
          <xsl:for-each select="dcc:quantity">
            <div class="cell">
              <xsl:call-template name="quantityValue">
                <xsl:with-param name="quantity" select="."/>
              </xsl:call-template>
            </div>
          </xsl:for-each>
        </div>
      </xsl:for-each>
    </div>
  </xsl:template>
  <xsl:template name="horizontalTable">
    <xsl:param name="data"/>
    <xsl:param name="expandedUnc" select="false()"/>
    <div class="mb-default">
      <xsl:for-each select="$data/dcc:list/dcc:list">
        <xsl:if test="position() = 1 and dcc:quantity/dcc:name">
          <div class="flex">
            <!-- Table Headers-->
            <div class="col cell"/>
            <xsl:for-each select="dcc:quantity">
              <div class="col cell">
                <xsl:call-template name="listName">
                  <xsl:with-param name="name" select="dcc:name"/>
                </xsl:call-template>
              </div>
            </xsl:for-each>
            <xsl:if test="$expandedUnc">
              <td class="text-center">
                <p>
                  Unsicherheit
                </p>
                <p class="description-secondary">
                  uncertainty
                </p>
              </td>
            </xsl:if>
          </div>
        </xsl:if>
        <div class="flex">
          <div class="col cell">
            <!-- Table Headers-->
            <xsl:call-template name="listName">
              <xsl:with-param name="name" select="dcc:name"/>
            </xsl:call-template>
          </div>
          <xsl:for-each select="dcc:quantity">
            <div class="col cell">
              <xsl:call-template name="quantityValue">
                <xsl:with-param name="quantity" select="."/>
              </xsl:call-template>
            </div>
          </xsl:for-each>
          <xsl:if test="dcc:quantity/si:real/si:expandedUnc">
            <div class="col cell">
              <xsl:value-of select="dcc:quantity/si:real/si:expandedUnc/si:uncertainty"/>
            </div>
          </xsl:if>
        </div>
      </xsl:for-each>
    </div>
  </xsl:template>
  <xsl:template name="oneRowQuantitiesTable">
    <xsl:param name="data"/>
    <div class="flex mb-default">
      <xsl:for-each select="$data/dcc:list/dcc:quantity">
        <div class="col">
          <div class="cell">
            <xsl:call-template name="listName">
              <xsl:with-param name="name" select="dcc:name"/>
            </xsl:call-template>
          </div>
          <div class="cell">
            <xsl:call-template name="quantityValue">
              <xsl:with-param name="quantity" select="."/>
            </xsl:call-template>
          </div>
        </div>
        <xsl:if test="si:real/si:expandedUnc">
          <div class="col">
            <div class="cell">
              <p>
                Unsicherheit
              </p>
              <p class="description-secondary">
                uncertainty
              </p>
            </div>
            <div class="cell">
              <xsl:value-of select="si:real/si:expandedUnc/si:uncertainty"/>&#160;
              <xsl:apply-templates select="si:real/si:unit"/>
            </div>
          </div>
        </xsl:if>
      </xsl:for-each>
    </div>
  </xsl:template>
  <xsl:template name="listName">
    <xsl:param name="name"/>
    <xsl:choose>
      <xsl:when test="$name">
        <p>
          <xsl:value-of select="$name/dcc:content[@lang=$primaryLang]"/>
        </p>
        <p class="description-secondary">
          <xsl:value-of select="$name/dcc:content[@lang=$secondaryLang]"/>
        </p>
      </xsl:when>
      <xsl:otherwise>
        <p/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="quantityValue">
    <xsl:param name="quantity"/>
    <xsl:choose>
      <xsl:when test="$quantity/dcc:noQuantity/dcc:content[@lang=$primaryLang]">
        <xsl:value-of select="$quantity/dcc:noQuantity/dcc:content[@lang=$primaryLang]"/>/&#160;
        <i>
          <xsl:value-of select="$quantity/dcc:noQuantity/dcc:content[@lang=$secondaryLang]"/>
        </i>
      </xsl:when>
      <xsl:when test="$quantity/dcc:noQuantity/dcc:content">
        <xsl:value-of select="$quantity/dcc:noQuantity/dcc:content"/>
      </xsl:when>
      <xsl:when test="$quantity/si:real/si:label">
        <xsl:value-of select="$quantity/si:real/si:label"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$quantity/si:real/si:value"/>&#160;
        <xsl:apply-templates select="$quantity/si:real/si:unit"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="dcc:usedMethods">
    <p class="sub-heading">Messmethoden</p>
    <p class="description-secondary mb-default">Procedures</p>
    <xsl:for-each select="dcc:usedMethod">
      <p class="sub-heading">
        <xsl:value-of select="dcc:name/dcc:content[@lang=$primaryLang]"/>
      </p>
      <p class="description-secondary mb-default">
        <xsl:value-of select="dcc:name/dcc:content[@lang=$secondaryLang]"/>
      </p>
      <xsl:apply-templates select="dcc:description"/>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="si:unit">
    <span class="si-unit">
      <xsl:value-of select="."/>
    </span>
  </xsl:template>
  <xsl:template match="dcc:description">
    <div class="avoid-break mb-default">
      <xsl:for-each select="dcc:content[@lang=$primaryLang]">
        <p class="description">
          <xsl:value-of select="."/>
        </p>
      </xsl:for-each>
      <xsl:for-each select="dcc:content[@lang=$secondaryLang]">
        <p class="description-secondary">
          <xsl:value-of select="."/>
        </p>
      </xsl:for-each>
    </div>
  </xsl:template>
  <xsl:template name="textBlock">
    <xsl:param name="textBlock" select="."/>
    <xsl:choose>
      <xsl:when test="$textBlock/dcc:content[@lang=$primaryLang] != ''">
        <xsl:value-of select="$textBlock/dcc:content[@lang=$primaryLang]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$textBlock/dcc:content"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="cipm_mra">
    <div>
      <p>
        <b>Die Physikalisch-Technische Bundesanstalt</b>
        (PTB) in Braunschweig und
        Berlin ist das nationale Metrologieinstitut und die technische Oberbehörde der
        Bundesrepublik Deutschland für das Messwesen. Die PTB gehört zum
        Geschäftsbereich des Bundesministeriums für Wirtschaft und Energie. Sie erfüllt
        die Anforderungen an Kalibrier- und Prüflaboratorien auf der Grundlage der
        DIN&#160;EN&#160;ISO/IEC&#160;17025.
      </p>
      <p>
        Zentrale Aufgabe der PTB ist es, die
        gesetzlichen Einheiten in Übereinstimmung mit dem Internationalen
        Einheitensystem (SI) darzustellen, zu bewahren und weiterzugeben. Die PTB steht
        damit an oberster Stelle der metrologischen Hierarchie in Deutschland. Die
        Kalibrierscheine der PTB dokumentieren eine auf nationale Normale rückgeführte
        Kalibrierung.
      </p>
      <p>
        Dieser Ergebnisbericht ist in
        Übereinstimmung mit den Kalibrier- und Messmöglichkeiten (CMCs), wie sie im
        Anhang C des gegenseitigen Abkommens (MRA) des Internationalen Komitees für
        Maße und Gewichte enthalten sind. Im Rahmen des MRA wird die Gültigkeit der
        Ergebnisberichte von allen teilnehmenden Instituten für die im Anhang C
        spezifizierten Messgrößen, Messbereiche und Messunsicherheiten gegenseitig
        anerkannt (nähere Informationen unter <a href="http://www.bipm.org">http://www.bipm.org</a>).
      </p>
      <p>
        Diese Aussage und das CIPM-MRA-Logo beziehen sich nur auf die Messergebnisse in diesem
        Kalibrierschein.
      </p>
      <p class="text-center">
        <img width="100" src="{$base}img/CIPM_MRA.svg"/>
      </p>
      <i>
        <p>
          <b>
            The Physikalisch-Technische Bundesanstalt
          </b>
          (PTB) in Braunschweig and Berlin
          is the National Metrology Institute and the supreme technical authority
          of the Federal Republic of Germany for metrology. The PTB comes under the
          auspices of the Federal Ministry of Economics and Energy. It meets the requirements
          for calibration and testing laboratories as defined in
          DIN&#160;EN&#160;ISO/IEC&#160;17025.
        </p>
        <p>
          The central task of PTB
          is to realize, to maintain and to disseminate the legal units in compliance
          with the International System of Units (SI). PTB thus is at the top of
          the metrological hierarchy in Germany. The calibration certificates issued
          by PTB document a calibration traceable to national measurement standards.
        </p>
        <p>
          This certificate is consistent with the Calibration and
          Measurement Capabilities (CMCs) that are included in Appendix C of the Mutual
          Recognition Arrangement (MRA) drawn up by the International Committee for Weights
          and Measures (CIPM). Under the MRA, all participating institutes recognize
          the validity of each other’s calibration and measurement certificates for
          the quantities, ranges and measurement uncertainties specified in Appendix C
          (for details, see <a href="http://www.bipm.org">http://www.bipm.org</a>).
        </p>
        <p>
          The CIPM MRA Logo and this statement attest only to the measurement component of the
          certificate.
        </p>
      </i>
    </div>
  </xsl:template>
</xsl:stylesheet>

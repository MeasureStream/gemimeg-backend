package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.FormulaDto;
import de.ptb.common.dcc.xjc.generated.FormulaType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.XmlType;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FormulaMapperTest {

  private static final String MATHML = """
      <html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
      \s
      <head>
      <title>MathML's Hello Square</title>
      </head>
      
      <body>
      
      <p> This is a perfect square:</p>
      
      <math xmlns="http://www.w3.org/1998/Math/MathML">
       <mrow>
         <msup>
           <mfenced>
             <mrow>
               <mi>a</mi>
               <mo>+</mo>
               <mi>b</mi>
             </mrow>
           </mfenced>
           <mn>2</mn>
         </msup>
       </mrow>\s
      </math>
      
      </body>
      </html>""";

  private static final String ENCODED_CONTENT = "eyJhbnkiOiI8aHRtbCB4bWxucz1cImh0dHA6Ly93d3cudzMub3JnLzE5OTkveGh0bWxc" +
      "IiBsYW5nPVwiZW5cIiB4bWw6bGFuZz1cImVuXCI+XG4gXG48aGVhZD5cbjx0aXRsZT5NYXRoTUwncyBIZWxsbyBTcXVhcmU8L3RpdGxlPlxuPC" +
      "9oZWFkPlxuXG48Ym9keT5cblxuPHA+IFRoaXMgaXMgYSBwZXJmZWN0IHNxdWFyZTo8L3A+XG5cbjxtYXRoIHhtbG5zPVwiaHR0cDovL3d3dy53" +
      "My5vcmcvMTk5OC9NYXRoL01hdGhNTFwiPlxuIDxtcm93PlxuICAgPG1zdXA+XG4gICAgIDxtZmVuY2VkPlxuICAgICAgIDxtcm93PlxuICAgIC" +
      "AgICAgPG1pPmE8L21pPlxuICAgICAgICAgPG1vPis8L21vPlxuICAgICAgICAgPG1pPmI8L21pPlxuICAgICAgIDwvbXJvdz5cbiAgICAgPC9t" +
      "ZmVuY2VkPlxuICAgICA8bW4+MjwvbW4+XG4gICA8L21zdXA+XG4gPC9tcm93PiBcbjwvbWF0aD5cblxuPC9ib2R5PlxuPC9odG1sPiJ9";

  @Autowired
  private FormulaMapper formulaMapper;

  @Autowired
  private ObjectFactory objectFactory;

  @Test
  void mapToDto_MathML_Ok() {
    FormulaType jaxbObject = objectFactory.createFormulaType();
    XmlType mathml = objectFactory.createXmlType();
    mathml.setAny(MATHML);
    jaxbObject.setMathml(mathml);
    FormulaDto actual = formulaMapper.mapToDto(jaxbObject);
    assertNotNull(actual);
    assertEquals(FormulaDto.FormulaType.MATHML, actual.getType());
    assertNotNull(actual.getContent());
    assertEquals(ENCODED_CONTENT, actual.getContent());
    byte[] decodedContent = Base64.getDecoder().decode(actual.getContent());
    assertEquals("{\"any\":\"<html xmlns=\\\"http://www.w3.org/1999/xhtml\\\" lang=\\\"en\\\" xml:lang=\\\"en" +
            "\\\">\\n \\n<head>\\n<title>MathML's Hello Square</title>\\n</head>\\n\\n<body>\\n\\n<p> This is a perfe" +
            "ct square:</p>\\n\\n<math xmlns=\\\"http://www.w3.org/1998/Math/MathML\\\">\\n <mrow>\\n   <msup>\\n    " +
            " <mfenced>\\n       <mrow>\\n         <mi>a</mi>\\n         <mo>+</mo>\\n         <mi>b</mi>\\n       </" +
            "mrow>\\n     </mfenced>\\n     <mn>2</mn>\\n   </msup>\\n </mrow> \\n</math>\\n\\n</body>\\n</html>\"}",
        IOUtils.toString(decodedContent, StandardCharsets.UTF_8.name()));
  }

  @Test
  void mapToJaxbObject_MathML_Ok() {
    FormulaDto dto = new FormulaDto();
    dto.setType(FormulaDto.FormulaType.MATHML);
    dto.setContent(ENCODED_CONTENT);
    FormulaType actual = formulaMapper.mapToJaxbObject(dto);
    assertNotNull(actual);
    assertNotNull(actual.getMathml());
    assertNotNull(actual.getMathml().getAny());
    assertEquals(MATHML, actual.getMathml().getAny().toString());
  }
}
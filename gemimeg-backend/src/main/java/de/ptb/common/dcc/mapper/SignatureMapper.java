package de.ptb.common.dcc.mapper;

import de.ptb.common.dcc.api.v1.dcc.SignatureDto;
import de.ptb.common.dcc.xjc.generated.CanonicalizationMethodType;
import de.ptb.common.dcc.xjc.generated.KeyInfoType;
import de.ptb.common.dcc.xjc.generated.KeyValueType;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import de.ptb.common.dcc.xjc.generated.ObjectType;
import de.ptb.common.dcc.xjc.generated.PGPDataType;
import de.ptb.common.dcc.xjc.generated.RetrievalMethodType;
import de.ptb.common.dcc.xjc.generated.SPKIDataType;
import de.ptb.common.dcc.xjc.generated.SignatureMethodType;
import de.ptb.common.dcc.xjc.generated.SignatureType;
import de.ptb.common.dcc.xjc.generated.SignatureValueType;
import de.ptb.common.dcc.xjc.generated.SignedInfoType;
import de.ptb.common.dcc.xjc.generated.TransformType;
import de.ptb.common.dcc.xjc.generated.TransformsType;
import de.ptb.common.dcc.xjc.generated.X509DataType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.ptb.common.dcc.api.v1.dcc.SignatureDto.KeyInfo.KEY_VALUE_KEY;
import static de.ptb.common.dcc.api.v1.dcc.SignatureDto.KeyInfo.PGP_KEY;
import static de.ptb.common.dcc.api.v1.dcc.SignatureDto.KeyInfo.RETRIEVAL_METHOD_KEY;
import static de.ptb.common.dcc.api.v1.dcc.SignatureDto.KeyInfo.SPKI_KEY;
import static de.ptb.common.dcc.api.v1.dcc.SignatureDto.KeyInfo.STRING_KEY;
import static de.ptb.common.dcc.api.v1.dcc.SignatureDto.KeyInfo.X509_KEY;
import static de.ptb.common.dcc.util.DccServiceUtil.setId;
import static de.ptb.common.dcc.util.DccServiceUtil.setRefIds;

@Component
public class SignatureMapper implements JaxbDtoBidirectionalMapper<SignatureType, SignatureDto> {

  private final ObjectFactory objectFactory;

  @Autowired
  public SignatureMapper(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  @Override
  public SignatureDto mapToDto(SignatureType jaxbObject) {
    SignatureDto target = new SignatureDto();
    setId(target, jaxbObject.getId());
    if (jaxbObject.getSignatureValue() != null) {
      SignatureDto.SignatureValue signatureValue = new SignatureDto.SignatureValue();
      signatureValue.setId(jaxbObject.getId());
      signatureValue.setValue(signatureValue.getValue());
      target.setValue(signatureValue);
    }
    if (jaxbObject.getSignedInfo() != null) {
      SignatureDto.SignedInfo signedInfo = getSignedInfo(jaxbObject);
      target.setSignedInfo(signedInfo);
    }
    if (jaxbObject.getKeyInfo() != null) {
      SignatureDto.KeyInfo keyInfo = new SignatureDto.KeyInfo();
      keyInfo.setId(jaxbObject.getKeyInfo().getId());
      if (jaxbObject.getKeyInfo().getContent() != null && !jaxbObject.getKeyInfo().getContent().isEmpty()) {
        Map<String, Object> keyInfoContent = new HashMap<>();
        jaxbObject.getKeyInfo().getContent().forEach(content -> {
          if (content instanceof KeyValueType) {
            keyInfoContent.put(KEY_VALUE_KEY, ((KeyValueType) content).getContent());
          }
          if (content instanceof PGPDataType) {
            keyInfoContent.put(PGP_KEY, ((PGPDataType) content).getContent());
          }
          if (content instanceof RetrievalMethodType) {
            SignatureDto.KeyInfo.RetrievalMethod retrievalMethod = getRetrievalMethod((RetrievalMethodType) content);
            keyInfoContent.put(RETRIEVAL_METHOD_KEY, retrievalMethod);
          }
          if (content instanceof SPKIDataType) {
            keyInfoContent.put(SPKI_KEY, ((SPKIDataType) content).getSPKISexpAndAny());
          }
          if (content instanceof X509DataType) {
            keyInfoContent.put(X509_KEY, ((X509DataType) content).getX509IssuerSerialOrX509SKIOrX509SubjectName());
          }
          if (content instanceof String) {
            keyInfoContent.put(STRING_KEY, content);
          }
        });
        keyInfo.setContent(keyInfoContent);
      }
      target.setKeyInfo(keyInfo);
    }
    if (jaxbObject.getObject() != null && !jaxbObject.getObject().isEmpty()) {
      List<SignatureDto.Content> contentList = new ArrayList<>();
      jaxbObject.getObject().forEach(objectType -> {
        SignatureDto.Content content = new SignatureDto.Content();
        content.setId(objectType.getId());
        content.setEncoding(objectType.getEncoding());
        content.setMimeType(objectType.getMimeType());
        if (objectType.getContent() != null && !objectType.getContent().isEmpty()) {
          content.setContent(objectType.getContent());
        }
        contentList.add(content);
      });
      target.setContentList(contentList);
    }
    return target;
  }

  @Override
  public SignatureType mapToJaxbObject(SignatureDto dto) {
    SignatureType target = objectFactory.createSignatureType();
    if (StringUtils.isNotBlank(dto.getId())) {
      target.setId(dto.getId());
    }
    if (dto.getValue() != null) {
      SignatureValueType signatureValueType = objectFactory.createSignatureValueType();
      signatureValueType.setId(dto.getValue().getId());
      signatureValueType.setValue(dto.getValue().getValue());
      target.setSignatureValue(signatureValueType);
    }
    if (dto.getSignedInfo() != null) {
      SignedInfoType signedInfoType = objectFactory.createSignedInfoType();
      signedInfoType.setId(dto.getSignedInfo().getId());
      if (StringUtils.isNotBlank(dto.getSignedInfo().getCanonicalizationAlgorithm()) &&
          dto.getSignedInfo().getCanonicalizationContent() != null) {
        CanonicalizationMethodType canonicalizationMethodType = objectFactory.createCanonicalizationMethodType();
        canonicalizationMethodType.setAlgorithm(dto.getSignedInfo().getCanonicalizationAlgorithm());
        canonicalizationMethodType.getContent().addAll(dto.getSignedInfo().getCanonicalizationContent());
        signedInfoType.setCanonicalizationMethod(canonicalizationMethodType);
      }
      if (StringUtils.isNotBlank(dto.getSignedInfo().getMethodAlgorithm()) &&
          dto.getSignedInfo().getMethodContent() != null) {
        SignatureMethodType signatureMethodType = objectFactory.createSignatureMethodType();
        signatureMethodType.setAlgorithm(dto.getSignedInfo().getMethodAlgorithm());
        signatureMethodType.getContent().addAll(dto.getSignedInfo().getMethodContent());
        signedInfoType.setSignatureMethod(signatureMethodType);
      }
      target.setSignedInfo(signedInfoType);
    }
    if (dto.getKeyInfo() != null) {
      KeyInfoType keyInfoType = objectFactory.createKeyInfoType();
      keyInfoType.setId(dto.getKeyInfo().getId());
      dto.getKeyInfo().getContent().forEach((key, value) -> {
        if (StringUtils.equalsIgnoreCase(key, KEY_VALUE_KEY)) {
          KeyValueType keyValueType = objectFactory.createKeyValueType();
          keyValueType.getContent().addAll((List<?>) value);
          keyInfoType.getContent().add(keyValueType);
        }
        if (StringUtils.equalsIgnoreCase(key, PGP_KEY)) {
          PGPDataType pgpDataType = objectFactory.createPGPDataType();
          pgpDataType.getContent().addAll((List<?>) value);
          keyInfoType.getContent().add(pgpDataType);
        }
        if (StringUtils.equalsIgnoreCase(key, RETRIEVAL_METHOD_KEY)) {
          RetrievalMethodType retrievalMethodType = objectFactory.createRetrievalMethodType();
          SignatureDto.KeyInfo.RetrievalMethod sourceRetrievalMethod = (SignatureDto.KeyInfo.RetrievalMethod) value;
          retrievalMethodType.setType(sourceRetrievalMethod.getType());
          retrievalMethodType.setURI(sourceRetrievalMethod.getUri());
          if (sourceRetrievalMethod.getTransformAlgorithms() != null && sourceRetrievalMethod.getTransforms() != null) {
            if (sourceRetrievalMethod.getTransformAlgorithms().size() != sourceRetrievalMethod.getTransforms().size()) {
              throw new IllegalArgumentException("RetrievalMethod algorithms and content length mismatch!");
            }
            TransformsType transformsType = objectFactory.createTransformsType();
            for (int i = 0; i < sourceRetrievalMethod.getTransformAlgorithms().size(); i++) {
              TransformType transformType = objectFactory.createTransformType();
              transformType.setAlgorithm(sourceRetrievalMethod.getTransformAlgorithms().get(i));
              transformType.getContent().addAll(sourceRetrievalMethod.getTransforms().get(i));
              transformsType.getTransform().add(transformType);
            }
            retrievalMethodType.setTransforms(transformsType);
          }
          keyInfoType.getContent().add(retrievalMethodType);
        }
        if (StringUtils.equalsIgnoreCase(key, SPKI_KEY)) {
          SPKIDataType spkiDataType = objectFactory.createSPKIDataType();
          spkiDataType.getSPKISexpAndAny().addAll((List<?>) value);
          keyInfoType.getContent().add(spkiDataType);
        }
        if (StringUtils.equalsIgnoreCase(key, X509_KEY)) {
          X509DataType x509DataType = objectFactory.createX509DataType();
          x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName().addAll((List<?>) value);
          keyInfoType.getContent().add(x509DataType);
        }
        if (StringUtils.equalsIgnoreCase(key, STRING_KEY)) {
          keyInfoType.getContent().add(value);
        }
      });
      target.setKeyInfo(keyInfoType);
    }
    if (dto.getContentList() != null && !dto.getContentList().isEmpty()) {
      dto.getContentList().forEach(content -> {
        ObjectType objectType = objectFactory.createObjectType();
        objectType.setEncoding(content.getEncoding());
        objectType.setMimeType(content.getMimeType());
        objectType.setId(content.getId());
        if (content.getContent() != null && !content.getContent().isEmpty()) {
          objectType.getContent().addAll(content.getContent());
        }
        target.getObject().add(objectType);
      });
    }
    return target;
  }

  @Nonnull
  private static SignatureDto.SignedInfo getSignedInfo(SignatureType jaxbObject) {
    SignatureDto.SignedInfo signedInfo = new SignatureDto.SignedInfo();
    signedInfo.setId(jaxbObject.getSignedInfo().getId());
    if (jaxbObject.getSignedInfo().getSignatureMethod() != null) {
      signedInfo.setMethodContent(jaxbObject.getSignedInfo().getSignatureMethod().getContent());
      signedInfo.setMethodAlgorithm(jaxbObject.getSignedInfo().getSignatureMethod().getAlgorithm());
    }
    if (jaxbObject.getSignedInfo().getCanonicalizationMethod() != null) {
      signedInfo.setCanonicalizationContent(jaxbObject.getSignedInfo().getCanonicalizationMethod().getContent());
      signedInfo.setCanonicalizationAlgorithm(jaxbObject.getSignedInfo().getCanonicalizationMethod().getAlgorithm());
    }
    return signedInfo;
  }

  @Nonnull
  private static SignatureDto.KeyInfo.RetrievalMethod getRetrievalMethod(RetrievalMethodType content) {
    SignatureDto.KeyInfo.RetrievalMethod retrievalMethod = new SignatureDto.KeyInfo.RetrievalMethod();
    retrievalMethod.setType(content.getType());
    retrievalMethod.setUri(content.getURI());
    if (content.getTransforms() != null) {
      List<TransformType> transformTypes = content.getTransforms().getTransform();
      if (transformTypes != null && !transformTypes.isEmpty()) {
        List<String> transformAlgorithms = new ArrayList<>();
        List<List<Object>> transforms = new ArrayList<>();
        transformTypes.forEach(transformType -> {
          transformAlgorithms.add(transformType.getAlgorithm());
          transforms.add(transformType.getContent());
        });
        retrievalMethod.setTransformAlgorithms(transformAlgorithms);
        retrievalMethod.setTransforms(transforms);
      }
    }
    return retrievalMethod;
  }
}

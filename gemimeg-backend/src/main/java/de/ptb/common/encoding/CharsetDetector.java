package de.ptb.common.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CharsetDetector {

  public CharsetDetector() {
  }

  public CharsetDetector setDeclaredEncoding(String encoding) {
    fDeclaredEncoding = encoding;
    return this;
  }

  public CharsetDetector setText(byte[] in) {
    fRawInput = in;
    fRawLength = in.length;
    return this;
  }

  private static final int kBufSize = 8000;

  public CharsetDetector setText(InputStream in) throws CharsetDetectException {
    fInputStream = in;
    fInputStream.mark(kBufSize);
    fRawInput = new byte[kBufSize];
    fRawLength = 0;
    int remainingLength = kBufSize;
    try {
      while (remainingLength > 0) {
        int bytesRead = fInputStream.read(fRawInput, fRawLength, remainingLength);
        if (bytesRead <= 0) {
          break;
        }
        fRawLength += bytesRead;
        remainingLength -= bytesRead;
      }
      fInputStream.reset();
    } catch (IOException e) {
      throw new CharsetDetectException(e.getMessage(), e.getCause());
    }
    return this;
  }

  public CharsetMatch detect() {
    CharsetMatch[] matches = detectAll();
    if (matches == null || matches.length == 0) {
      return null;
    }
    return matches[0];
  }

  public CharsetMatch[] detectAll() {
    ArrayList<CharsetMatch> matches = new ArrayList<>();
    MungeInput();
    for (int i = 0; i < ALL_CS_RECOGNIZERS.size(); i++) {
      CSRecognizerInfo rcinfo = ALL_CS_RECOGNIZERS.get(i);
      boolean active = (fEnabledRecognizers != null) ? fEnabledRecognizers[i] : rcinfo.isDefaultEnabled;
      if (active) {
        CharsetMatch m = rcinfo.recognizer.match(this);
        if (m != null) {
          matches.add(m);
        }
      }
    }
    Collections.sort(matches);
    Collections.reverse(matches);
    CharsetMatch[] resultArray = new CharsetMatch[matches.size()];
    resultArray = matches.toArray(resultArray);
    return resultArray;
  }

  public Reader getReader(InputStream in, String declaredEncoding) throws CharsetDetectException {
    fDeclaredEncoding = declaredEncoding;
    setText(in);
    CharsetMatch match = detect();
    if (match == null) {
      return null;
    }
    return match.getReader();
  }

  public String getString(byte[] in, String declaredEncoding) {
    fDeclaredEncoding = declaredEncoding;
    try {
      setText(in);
      CharsetMatch match = detect();
      if (match == null) {
        return null;
      }
      return match.getString(-1);
    } catch (IOException e) {
      return null;
    }
  }

  public static String[] getAllDetectableCharsets() {
    String[] allCharsetNames = new String[ALL_CS_RECOGNIZERS.size()];
    for (int i = 0; i < allCharsetNames.length; i++) {
      allCharsetNames[i] = ALL_CS_RECOGNIZERS.get(i).recognizer.getName();
    }
    return allCharsetNames;
  }

  public boolean inputFilterEnabled() {
    return fStripTags;
  }

  public boolean enableInputFilter(boolean filter) {
    boolean previous = fStripTags;
    fStripTags = filter;
    return previous;
  }

  private void MungeInput() {
    int srci = 0;
    int dsti = 0;
    byte b;
    boolean inMarkup = false;
    int openTags = 0;
    int badTags = 0;
    if (fStripTags) {
      for (srci = 0; srci < fRawLength && dsti < fInputBytes.length; srci++) {
        b = fRawInput[srci];
        if (b == (byte) '<') {
          if (inMarkup) {
            badTags++;
          }
          inMarkup = true;
          openTags++;
        }
        if (!inMarkup) {
          fInputBytes[dsti++] = b;
        }
        if (b == (byte) '>') {
          inMarkup = false;
        }
      }
      fInputLen = dsti;
    }
    if (openTags < 5 || openTags / 5 < badTags ||
        (fInputLen < 100 && fRawLength > 600)) {
      int limit = fRawLength;
      if (limit > kBufSize) {
        limit = kBufSize;
      }
      for (srci = 0; srci < limit; srci++) {
        fInputBytes[srci] = fRawInput[srci];
      }
      fInputLen = srci;
    }
    Arrays.fill(fByteStats, (short) 0);
    for (srci = 0; srci < fInputLen; srci++) {
      int val = fInputBytes[srci] & 0x00ff;
      fByteStats[val]++;
    }
    fC1Bytes = false;
    for (int i = 0x80; i <= 0x9F; i += 1) {
      if (fByteStats[i] != 0) {
        fC1Bytes = true;
        break;
      }
    }
  }

  byte[] fInputBytes = new byte[kBufSize];
  int fInputLen;
  short[] fByteStats = new short[256];
  boolean fC1Bytes = false;
  String fDeclaredEncoding;
  byte[] fRawInput;
  int fRawLength;
  InputStream fInputStream;
  private boolean fStripTags = false;
  private boolean[] fEnabledRecognizers;

  private static class CSRecognizerInfo {
    CharsetRecognizer recognizer;
    boolean isDefaultEnabled;

    CSRecognizerInfo(CharsetRecognizer recognizer, boolean isDefaultEnabled) {
      this.recognizer = recognizer;
      this.isDefaultEnabled = isDefaultEnabled;
    }
  }

  private static final List<CSRecognizerInfo> ALL_CS_RECOGNIZERS;

  static {
    List<CSRecognizerInfo> list = new ArrayList<>();
    list.add(new CSRecognizerInfo(new UTF8Recognizer(), true));
    list.add(new CSRecognizerInfo(new UnicodeRecognizer.UTF16BE(), true));
    list.add(new CSRecognizerInfo(new UnicodeRecognizer.UTF16LE(), true));
    list.add(new CSRecognizerInfo(new UnicodeRecognizer.UTF32BE(), true));
    list.add(new CSRecognizerInfo(new UnicodeRecognizer.UTF32LE(), true));
    list.add(new CSRecognizerInfo(new MBCSRecognizer.SJIS(), true));
    list.add(new CSRecognizerInfo(new ISO2022Recognizer.JP(), true));
    list.add(new CSRecognizerInfo(new ISO2022Recognizer.CN(), true));
    list.add(new CSRecognizerInfo(new ISO2022Recognizer.KR(), true));
    list.add(new CSRecognizerInfo(new MBCSRecognizer.EUC.GB18030(), true));
    list.add(new CSRecognizerInfo(new MBCSRecognizer.EUC.JP(), true));
    list.add(new CSRecognizerInfo(new MBCSRecognizer.EUC.KR(), true));
    list.add(new CSRecognizerInfo(new MBCSRecognizer.Big5(), true));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.ISO8859_1(), true));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.ISO8859_2(), true));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.ISO8859_5ru(), true));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.ISO8859_6ar(), true));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.ISO8859_7el(), true));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.ISO8859_8Ihe(), true));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.ISO8859_8he(), true));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.Windows1251(), true));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.Windows1256(), true));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.KOI8R(), true));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.ISO8859_9tr(), true));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.IBM424he_rtl(), false));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.IBM424he_ltr(), false));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.IBM420ar_rtl(), false));
    list.add(new CSRecognizerInfo(new SBCSRecognizer.IBM420ar_ltr(), false));
    ALL_CS_RECOGNIZERS = Collections.unmodifiableList(list);
  }

  public String[] getDetectableCharsets() {
    List<String> csnames = new ArrayList<>(ALL_CS_RECOGNIZERS.size());
    for (int i = 0; i < ALL_CS_RECOGNIZERS.size(); i++) {
      CSRecognizerInfo rcinfo = ALL_CS_RECOGNIZERS.get(i);
      boolean active = (fEnabledRecognizers == null) ? rcinfo.isDefaultEnabled : fEnabledRecognizers[i];
      if (active) {
        csnames.add(rcinfo.recognizer.getName());
      }
    }
    return csnames.toArray(new String[0]);
  }

  public CharsetDetector setDetectableCharset(String encoding, boolean enabled) {
    int modIdx = -1;
    boolean isDefaultVal = false;
    for (int i = 0; i < ALL_CS_RECOGNIZERS.size(); i++) {
      CSRecognizerInfo csrinfo = ALL_CS_RECOGNIZERS.get(i);
      if (csrinfo.recognizer.getName().equals(encoding)) {
        modIdx = i;
        isDefaultVal = (csrinfo.isDefaultEnabled == enabled);
        break;
      }
    }
    if (modIdx < 0) {
      throw new IllegalArgumentException("Invalid encoding: " + "\"" + encoding + "\"");
    }
    if (fEnabledRecognizers == null && !isDefaultVal) {
      fEnabledRecognizers = new boolean[ALL_CS_RECOGNIZERS.size()];
      for (int i = 0; i < ALL_CS_RECOGNIZERS.size(); i++) {
        fEnabledRecognizers[i] = ALL_CS_RECOGNIZERS.get(i).isDefaultEnabled;
      }
    }
    if (fEnabledRecognizers != null) {
      fEnabledRecognizers[modIdx] = enabled;
    }
    return this;
  }
}
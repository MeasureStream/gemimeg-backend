package de.ptb.common.encoding;

public abstract class ISO2022Recognizer extends CharsetRecognizer {

  int match(byte[] text, int textLen, byte[][] escapeSequences) {
    int i, j;
    int escN;
    int hits = 0;
    int misses = 0;
    int shifts = 0;
    int quality;
    scanInput:
    for (i = 0; i < textLen; i++) {
      if (text[i] == 0x1b) {
        checkEscapes:
        for (escN = 0; escN < escapeSequences.length; escN++) {
          byte[] seq = escapeSequences[escN];
          if ((textLen - i) < seq.length) {
            continue;
          }
          for (j = 1; j < seq.length; j++) {
            if (seq[j] != text[i + j]) {
              continue checkEscapes;
            }
          }
          hits++;
          i += seq.length - 1;
          continue scanInput;
        }
        misses++;
      }
      if (text[i] == 0x0e || text[i] == 0x0f) {
        shifts++;
      }
    }
    if (hits == 0) {
      return 0;
    }
    quality = (100 * hits - 100 * misses) / (hits + misses);
    if (hits + shifts < 5) {
      quality -= (5 - (hits + shifts)) * 10;
    }
    if (quality < 0) {
      quality = 0;
    }
    return quality;
  }

  static class JP extends ISO2022Recognizer {
    private final byte[][] escapeSequences = {
        {0x1b, 0x24, 0x28, 0x43},   // KS X 1001:1992
        {0x1b, 0x24, 0x28, 0x44},   // JIS X 212-1990
        {0x1b, 0x24, 0x40},         // JIS C 6226-1978
        {0x1b, 0x24, 0x41},         // GB 2312-80
        {0x1b, 0x24, 0x42},         // JIS X 208-1983
        {0x1b, 0x26, 0x40},         // JIS X 208 1990, 1997
        {0x1b, 0x28, 0x42},         // ASCII
        {0x1b, 0x28, 0x48},         // JIS-Roman
        {0x1b, 0x28, 0x49},         // Half-width katakana
        {0x1b, 0x28, 0x4a},         // JIS-Roman
        {0x1b, 0x2e, 0x41},         // ISO 8859-1
        {0x1b, 0x2e, 0x46}          // ISO 8859-7
    };

    String getName() {
      return "ISO-2022-JP";
    }

    CharsetMatch match(CharsetDetector det) {
      int confidence = match(det.fInputBytes, det.fInputLen, escapeSequences);
      return confidence == 0 ? null : new CharsetMatch(det, this, confidence);
    }
  }

  static class KR extends ISO2022Recognizer {
    private final byte[][] escapeSequences = {
        {0x1b, 0x24, 0x29, 0x43}
    };

    String getName() {
      return "ISO-2022-KR";
    }

    CharsetMatch match(CharsetDetector det) {
      int confidence = match(det.fInputBytes, det.fInputLen, escapeSequences);
      return confidence == 0 ? null : new CharsetMatch(det, this, confidence);
    }
  }

  static class CN extends ISO2022Recognizer {
    private final byte[][] escapeSequences = {
        {0x1b, 0x24, 0x29, 0x41},   // GB 2312-80
        {0x1b, 0x24, 0x29, 0x47},   // CNS 11643-1992 Plane 1
        {0x1b, 0x24, 0x2A, 0x48},   // CNS 11643-1992 Plane 2
        {0x1b, 0x24, 0x29, 0x45},   // ISO-IR-165
        {0x1b, 0x24, 0x2B, 0x49},   // CNS 11643-1992 Plane 3
        {0x1b, 0x24, 0x2B, 0x4A},   // CNS 11643-1992 Plane 4
        {0x1b, 0x24, 0x2B, 0x4B},   // CNS 11643-1992 Plane 5
        {0x1b, 0x24, 0x2B, 0x4C},   // CNS 11643-1992 Plane 6
        {0x1b, 0x24, 0x2B, 0x4D},   // CNS 11643-1992 Plane 7
        {0x1b, 0x4e},               // SS2
        {0x1b, 0x4f},               // SS3
    };

    String getName() {
      return "ISO-2022-CN";
    }

    CharsetMatch match(CharsetDetector det) {
      int confidence = match(det.fInputBytes, det.fInputLen, escapeSequences);
      return confidence == 0 ? null : new CharsetMatch(det, this, confidence);
    }
  }
}

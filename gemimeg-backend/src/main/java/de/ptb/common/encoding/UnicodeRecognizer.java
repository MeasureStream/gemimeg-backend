package de.ptb.common.encoding;

public abstract class UnicodeRecognizer extends CharsetRecognizer {

  abstract String getName();

  abstract CharsetMatch match(CharsetDetector det);

  static int codeUnit16FromBytes(byte hi, byte lo) {
    return ((hi & 0xff) << 8) | (lo & 0xff);
  }

  static int adjustConfidence(int codeUnit, int confidence) {
    if (codeUnit == 0) {
      confidence -= 10;
    } else if ((codeUnit >= 0x20 && codeUnit <= 0xff) || codeUnit == 0x0a) {
      confidence += 10;
    }
    if (confidence < 0) {
      confidence = 0;
    } else if (confidence > 100) {
      confidence = 100;
    }
    return confidence;
  }

  static class UTF16BE extends UnicodeRecognizer {
    String getName() {
      return "UTF-16BE";
    }

    CharsetMatch match(CharsetDetector det) {
      byte[] input = det.fRawInput;
      int confidence = 10;
      int bytesToCheck = Math.min(input.length, 30);
      for (int charIndex = 0; charIndex < bytesToCheck - 1; charIndex += 2) {
        int codeUnit = codeUnit16FromBytes(input[charIndex], input[charIndex + 1]);
        if (charIndex == 0 && codeUnit == 0xFEFF) {
          confidence = 100;
          break;
        }
        confidence = adjustConfidence(codeUnit, confidence);
        if (confidence == 0 || confidence == 100) {
          break;
        }
      }
      if (bytesToCheck < 4 && confidence < 100) {
        confidence = 0;
      }
      if (confidence > 0) {
        return new CharsetMatch(det, this, confidence);
      }
      return null;
    }
  }

  static class UTF16LE extends UnicodeRecognizer {
    String getName() {
      return "UTF-16LE";
    }

    CharsetMatch match(CharsetDetector det) {
      byte[] input = det.fRawInput;
      int confidence = 10;
      int bytesToCheck = Math.min(input.length, 30);
      for (int charIndex = 0; charIndex < bytesToCheck - 1; charIndex += 2) {
        int codeUnit = codeUnit16FromBytes(input[charIndex + 1], input[charIndex]);
        if (charIndex == 0 && codeUnit == 0xFEFF) {
          confidence = 100;
          break;
        }
        confidence = adjustConfidence(codeUnit, confidence);
        if (confidence == 0 || confidence == 100) {
          break;
        }
      }
      if (bytesToCheck < 4 && confidence < 100) {
        confidence = 0;
      }
      if (confidence > 0) {
        return new CharsetMatch(det, this, confidence);
      }
      return null;
    }
  }

  static abstract class UTF32 extends UnicodeRecognizer {
    abstract int getChar(byte[] input, int index);

    abstract String getName();

    CharsetMatch match(CharsetDetector det) {
      byte[] input = det.fRawInput;
      int limit = (det.fRawLength / 4) * 4;
      int numValid = 0;
      int numInvalid = 0;
      boolean hasBOM = false;
      int confidence = 0;
      if (limit == 0) {
        return null;
      }
      if (getChar(input, 0) == 0x0000FEFF) {
        hasBOM = true;
      }
      for (int i = 0; i < limit; i += 4) {
        int ch = getChar(input, i);
        if (ch < 0 || ch >= 0x10FFFF || (ch >= 0xD800 && ch <= 0xDFFF)) {
          numInvalid += 1;
        } else {
          numValid += 1;
        }
      }
      if (hasBOM && numInvalid == 0) {
        confidence = 100;
      } else if (hasBOM && numValid > numInvalid * 10) {
        confidence = 80;
      } else if (numValid > 3 && numInvalid == 0) {
        confidence = 100;
      } else if (numValid > 0 && numInvalid == 0) {
        confidence = 80;
      } else if (numValid > numInvalid * 10) {
        confidence = 25;
      }
      return confidence == 0 ? null : new CharsetMatch(det, this, confidence);
    }
  }

  static class UTF32BE extends UTF32 {
    int getChar(byte[] input, int index) {
      return (input[index] & 0xFF) << 24 | (input[index + 1] & 0xFF) << 16 |
          (input[index + 2] & 0xFF) << 8 | (input[index + 3] & 0xFF);
    }

    String getName() {
      return "UTF-32BE";
    }
  }

  static class UTF32LE extends UTF32 {
    int getChar(byte[] input, int index) {
      return (input[index + 3] & 0xFF) << 24 | (input[index + 2] & 0xFF) << 16 |
          (input[index + 1] & 0xFF) << 8 | (input[index] & 0xFF);
    }

    String getName() {
      return "UTF-32LE";
    }
  }
}

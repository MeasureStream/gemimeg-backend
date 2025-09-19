package de.ptb.common.encoding;

public class UTF8Recognizer extends CharsetRecognizer {
  String getName() {
    return "UTF-8";
  }

  CharsetMatch match(CharsetDetector det) {
    boolean hasBOM = false;
    int numValid = 0;
    int numInvalid = 0;
    byte[] input = det.fRawInput;
    int i;
    int trailBytes;
    int confidence;
    if (det.fRawLength >= 3 &&
        (input[0] & 0xFF) == 0xef && (input[1] & 0xFF) == 0xbb && (input[2] & 0xFF) == 0xbf) {
      hasBOM = true;
    }
    for (i = 0; i < det.fRawLength; i++) {
      int b = input[i];
      if ((b & 0x80) == 0) {
        continue;
      }
      if ((b & 0x0e0) == 0x0c0) {
        trailBytes = 1;
      } else if ((b & 0x0f0) == 0x0e0) {
        trailBytes = 2;
      } else if ((b & 0x0f8) == 0xf0) {
        trailBytes = 3;
      } else {
        numInvalid++;
        continue;
      }
      while (true) {
        i++;
        if (i >= det.fRawLength) {
          break;
        }
        b = input[i];
        if ((b & 0xc0) != 0x080) {
          numInvalid++;
          break;
        }
        if (--trailBytes == 0) {
          numValid++;
          break;
        }
      }
    }
    confidence = 0;
    if (hasBOM && numInvalid == 0) {
      confidence = 100;
    } else if (hasBOM && numValid > numInvalid * 10) {
      confidence = 80;
    } else if (numValid > 3 && numInvalid == 0) {
      confidence = 100;
    } else if (numValid > 0 && numInvalid == 0) {
      confidence = 80;
    } else if (numValid == 0 && numInvalid == 0) {
      confidence = 15;
    } else if (numValid > numInvalid * 10) {
      confidence = 25;
    }
    return confidence == 0 ? null : new CharsetMatch(det, this, confidence);
  }
}

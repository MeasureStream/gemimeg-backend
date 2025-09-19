package de.ptb.common.encoding;

abstract class CharsetRecognizer {

  abstract String getName();

  public String getLanguage() {
    return null;
  }

  abstract CharsetMatch match(CharsetDetector det);
}

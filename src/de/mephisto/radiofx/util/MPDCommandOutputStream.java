package de.mephisto.radiofx.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Stores the last mpd telnet command result.
 */
public class MPDCommandOutputStream extends OutputStream {

  private StringBuilder mBuf = new StringBuilder();

  private String lastCommandOutput;

  public void write(int b) throws IOException {
    mBuf.append((char) b);
    if(mBuf.toString().contains("OK")) {
      lastCommandOutput = mBuf.toString();
      mBuf = new StringBuilder();
    }
  }


  public String getLastCommandOutput() {
    return lastCommandOutput;
  }
}
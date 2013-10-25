package de.mephisto.radiofx.services.mpd.impl;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Reads the output of the telnet connection.
 */
public class TelnetReader extends Thread {
  private final static Logger LOG = LoggerFactory.getLogger(TelnetReader.class);

  private TelnetClient tc;
  private String lastCommand;
  private StringBuilder b = new StringBuilder();

  public TelnetReader(TelnetClient tc) {
    this.tc = tc;
  }

  /**
   * Reader thread.
   * Reads lines from the TelnetClient and echoes them
   * on the screen.
   */
  public void run() {
    InputStream instr = tc.getInputStream();

    try {
      byte[] buff = new byte[1024];
      int ret_read = 0;

      do {
        ret_read = instr.read(buff);
        if (ret_read > 0) {
          b.append(new String(buff, 0, ret_read));
        }
        if (b.toString().contains("OK")) {
          lastCommand = b.toString();
          b = new StringBuilder();
        }
      }
      while (ret_read >= 0);
    } catch (Exception e) {
      LOG.error("Exception while reading socket:" + e.getMessage(), e);
    }

    try {
      tc.disconnect();
    } catch (Exception e) {
      LOG.error("Exception while closing telnet:" + e.getMessage(), e);
    }
  }

  public String getLastCommand() {
    return lastCommand;
  }
}

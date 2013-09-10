package de.mephisto.radiofx.services.mpd.impl;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility methods for handling the MPD.
 */
public class MPDClient {
  private final static Logger LOG = LoggerFactory.getLogger(MPDClient.class);

  private TelnetClient client;
  private InputStream in = null;
  private boolean localModeEnabled;
  private String host;
  private int port;

  public MPDClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  /**
   * Creates a new telnet connection to the MPD.
   */
  public void connect() {
    try {
      if (in != null) {
        in.close();
      }
      if (client != null) {
        client.disconnect();
      }
      client = new TelnetClient();
      localModeEnabled = host.equalsIgnoreCase("localhost");
      client.connect(host, port);
      in = client.getInputStream();
      LOG.info("Initialized " + this);
    } catch (Exception e) {
      LOG.error("Failed to connect to " + this + ": " + e.getMessage());
    }
  }

  /**
   * Waits until an acknowledge flag is logged by the mpc.
   *
   * @return
   */
  private boolean awaitOk() {
    try {
//      do {
//        ret_read = in.read(buff);
//        if (ret_read > 0) {
//          String msg = new String(buff, 0, ret_read).trim();
//          LOG.info("MPD: " + msg);
//          if (msg.contains("OK")) {
//            return true;
//          }
//        }
//      }
//      while (ret_read >= 0);
      Thread.sleep(100);
    } catch (Exception e) {
      LOG.error("Exception while reading from MPD:" + e.getMessage());
      this.client = null;
    }
    return false;
  }


  /**
   * Executes a MPD command via telnet.
   *
   * @param cmd
   */
  public void executeTelnetCommand(String cmd) {
    try {
      LOG.info("Executing telnet command '" + cmd + "'");
      cmd += "\n";
      if(client == null || client.getOutputStream() == null) {
        connect();
      }

      if (client.getOutputStream() != null) {
        client.getOutputStream().write(cmd.getBytes());
        client.getOutputStream().flush();
      }
      else {
        LOG.error("Exception executing MPD telnet command: Could not acquire telnet output steam, please check the MPD server connection.");
      }

      awaitOk();
    } catch (IOException e) {
      LOG.error("Exception executing MPD telnet command '" + cmd + "':" + e.getMessage());
      this.client = null;
    }
  }

  /**
   * Invokes a system command for the mpc client.
   *
   * @param cmd
   */
  public String executeLocalCommand(String cmd) {
    try {
      LOG.info("Executing mpc command '" + cmd + "'");
      cmd = "mpc " + cmd + "\n";
      Process p = Runtime.getRuntime().exec(cmd);
      p.waitFor();
      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line = reader.readLine();
      StringBuilder builder = new StringBuilder();
      while (line != null) {
        line = reader.readLine();
        builder.append(line);
      }

      return builder.toString();
    } catch (Exception e) {
      LOG.error("Exception executing MPD command:" + e.getMessage());
      this.client = null;
      return null;
    }
  }

  public boolean isLocalModeEnabled() {
    return localModeEnabled;
  }

  @Override
  public String toString() {
    return "MPCClient for " + host + ":" + port;
  }
}

package de.mephisto.radiofx.services.mpd.impl;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

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
  private TelnetReader outputStream;

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

      outputStream = new TelnetReader(client);
      outputStream.start();
      in = client.getInputStream();
      LOG.info("Initialized " + this);
    } catch (Exception e) {
      LOG.error("Failed to connect to " + this + ": " + e.getMessage());
    }
  }

  /**
   * Playback of the given URL.
   *
   * @param url
   */
  public long play(String url) {
    try {
      LOG.info("Playback of URL " + url);
      executeTelnetCommand("clear");
      Thread.sleep(100);
      executeTelnetCommand("add " + url);
      Thread.sleep(100);
      executeTelnetCommand("play");
    } catch (Exception e) {
      LOG.error("Error executing playback of URL " + url + ": " + e.getMessage(), e);
    }
    return new Date().getTime();
  }

  /**
   * Executes a MPD command via telnet.
   *
   * @param cmd
   */
  public void executeTelnetCommand(String cmd) {
    try {
      cmd += "\n";
      if (client == null || client.getOutputStream() == null) {
        connect();
      }

      if (client.getOutputStream() != null) {
        client.getOutputStream().write(cmd.getBytes());
        client.getOutputStream().flush();
      }
      else {
        LOG.error("Exception executing MPD telnet command: Could not acquire telnet output steam, please check the MPD server connection.");
      }
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


  /**
   * Executes the playlistinfo command and applies
   * the result string to the current station.
   * @return
   */
  public PlaylistInfo playlistInfo() {
    if(client != null && client.isConnected()) {
      executeTelnetCommand("playlistinfo");
      String output = outputStream.getLastCommand();
      return new PlaylistInfo(output);
    }
    else {
      LOG.error("Failed to retrieve mpc playlist info: " + this + " is not connected.");
    }
    return null;
  }


  @Override
  public String toString() {
    return "MPD Client for " + host + ":" + port;
  }
}

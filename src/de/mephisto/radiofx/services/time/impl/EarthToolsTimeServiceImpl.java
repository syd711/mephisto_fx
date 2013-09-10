package de.mephisto.radiofx.services.time.impl;

import de.mephisto.radiofx.services.time.TimeListener;
import de.mephisto.radiofx.services.time.TimeService;
import de.mephisto.radiofx.util.Config;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Service for retrieving the current time.
 */
public class EarthToolsTimeServiceImpl implements TimeService {
  private final static Logger LOG = LoggerFactory.getLogger(EarthToolsTimeServiceImpl.class);

  private Date localTime;
  private List<TimeListener> listeners = new ArrayList<TimeListener>();

  public Date getTime() {
    if(localTime != null) {
      return localTime;
    }

    BufferedReader in = null;
    try {
      new Timer().start();
      Configuration configuration = Config.getConfiguration("time.properties");
      String url = configuration.getString("time.service.url");
      URL earthToolsServer = new URL(url);
      URLConnection yc = earthToolsServer.openConnection();
      in = new BufferedReader( new InputStreamReader(yc.getInputStream()));
      String inputLine;

      while ((inputLine = in.readLine()) != null) {
        if(inputLine.contains("localtime")) {
          String date = inputLine.substring(inputLine.indexOf(">")+1, inputLine.lastIndexOf("<"));
          localTime = new SimpleDateFormat("d MMM yyyy hh:mm:ss", Locale.getDefault()).parse(date);
          return localTime;
        }
      }

    } catch (Exception e) {
      LOG.error("Error retrieving local time: " + e.getMessage());
    }
    finally {
      if(in != null) {
        try {
          in.close();
        } catch (IOException e) {
          //ignore
        }
      }
    }
    return new Date();
  }

  @Override
  public void addTimeListener(TimeListener listener) {
    this.listeners.add(listener);
  }

  /**
   * Updates the timer so that the time is requested only once.
   */
  class Timer extends Thread {
    private boolean running = true;

    @Override
    public void run() {
      while(running) {
        try {
          Thread.sleep(1000);
          if(localTime != null) {
            long dateTime = localTime.getTime()+1000;
            localTime = new Date(dateTime);

            for(TimeListener listener : listeners) {
              listener.timeChanged(localTime);
            }
          }
        } catch (InterruptedException e) {
          LOG.error("Error in timer thread: " + e.getMessage());
        }
      }
    }
  }
}

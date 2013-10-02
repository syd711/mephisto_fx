package de.mephisto.radiofx.services.time.impl;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.RefreshingService;
import de.mephisto.radiofx.services.time.DateTimeInfo;
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
public class EarthToolsTimeServiceImpl extends RefreshingService {
  private final static Logger LOG = LoggerFactory.getLogger(EarthToolsTimeServiceImpl.class);
  private final static int REFRESH_INTERVAL = 60000;

  private Date localTime;

  public EarthToolsTimeServiceImpl() {
    super(REFRESH_INTERVAL);
  }

  @Override
  public List<IServiceModel> getServiceData() {
    List<IServiceModel> data = new ArrayList<IServiceModel>();
    if(localTime != null) {
      DateTimeInfo info = new DateTimeInfo();
      long time = localTime.getTime() + REFRESH_INTERVAL;
      localTime = new Date(time);
      info.setDate(localTime);
      data.add(info);
      return data;
    }

    BufferedReader in = null;
    try {
      Configuration configuration = Config.getConfiguration("time.properties");
      String url = configuration.getString("time.service.url");
      URL earthToolsServer = new URL(url);
      URLConnection yc = earthToolsServer.openConnection();
      in = new BufferedReader( new InputStreamReader(yc.getInputStream()));
      String inputLine;

      while ((inputLine = in.readLine()) != null) {
        if(inputLine.contains("localtime")) {
          String date = inputLine.substring(inputLine.indexOf(">")+1, inputLine.lastIndexOf("<"));
          localTime = new SimpleDateFormat("d MMM yyyy hh:mm:ss", Locale.US).parse(date);
          DateTimeInfo info = new DateTimeInfo();
          info.setDate(localTime);
          data.add(info);
          break;
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

    return data;
  }
}

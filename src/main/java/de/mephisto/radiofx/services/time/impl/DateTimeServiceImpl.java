package de.mephisto.radiofx.services.time.impl;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.RefreshingService;
import de.mephisto.radiofx.services.time.DateTimeInfo;
import de.mephisto.radiofx.ui.SplashScreen;
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
public class DateTimeServiceImpl extends RefreshingService {
  private final static int REFRESH_INTERVAL = 60000;

  private Date localTime;

  public DateTimeServiceImpl() {
    super(REFRESH_INTERVAL);
  }

  @Override
  public List<IServiceModel> getServiceData(boolean forceRefresh) {
    List<IServiceModel> data = new ArrayList<IServiceModel>();
    if (localTime != null) {
      DateTimeInfo info = new DateTimeInfo();
      long time = localTime.getTime() + REFRESH_INTERVAL;
      localTime = new Date(time);
      info.setDate(localTime);
      data.add(info);
    }
    return data;
  }


  @Override
  public void initService(SplashScreen splashScreen) {
    splashScreen.setMessage("Loading Date and Time", (splashScreen.getProgress()+0.25));
    localTime = new Date();
  }
}

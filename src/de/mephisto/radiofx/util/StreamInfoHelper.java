package de.mephisto.radiofx.util;

import de.mephisto.radiofx.services.mpd.StationInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Utility to read data from a stream
 */
public class StreamInfoHelper {
  private final static Logger LOG = LoggerFactory.getLogger(StreamInfoHelper.class);

  public static void loadInfo(StationInfo stream) {
    if(!stream.isPlayable() || !stream.isInfoAvailable()) {
      return; //ignore streams that have already failed.
    }
    URL url = null;
    try {
      url = new URL(stream.getUrl());
      StreamInfo info = new StreamInfo(url);
      if(!StringUtils.isEmpty(info.getTitle()) || !StringUtils.isEmpty(info.getArtist())) {
        stream.setTrack(info.getTitle() + " - " + info.getArtist());
      }
    }
    catch (UnknownHostException e) {
      stream.setPlayable(false);
      stream.setInfoAvailable(false);
      LOG.warn("Failed to retrieve meta information for Stream '" + stream.getUrl() + "', " + e.getClass().getName() + ":" + e.getMessage());
    }
    catch (SocketException e) {
      stream.setPlayable(false);
      stream.setInfoAvailable(false);
      LOG.warn("Failed to retrieve meta information for Stream '" + stream.getUrl() + "', " + e.getClass().getName() + ":" + e.getMessage());
    }
    catch (IOException e) {
      stream.setInfoAvailable(false);
      LOG.warn("Failed to retrieve meta information for Stream '" + stream.getUrl() + "', " + e.getClass().getName() + ":" + e.getMessage());
    }
    LOG.info("Retrieved Stream info for " + stream.getUrl());
  }
}

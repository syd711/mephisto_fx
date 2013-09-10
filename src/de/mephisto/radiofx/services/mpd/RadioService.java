package de.mephisto.radiofx.services.mpd;

import java.util.List;

/**
 * The common interface for the MPD service
 */
public interface RadioService {
  /**
   * Returns a list of all available stations.
   * @return
   */
  List<StationInfo> getStations();

  /**
   * Adds a listener for station changes.
   * @param listener
   */
  void addStationListener(IStationListener listener);
}

package de.mephisto.radiofx.services.mpd;

/**
 * Listener for refreshing station info.
 */
public interface IStationListener {

  /**
   * Fired when the station info has changed.
   * @param info
   */
  void stationChanged(StationInfo info);
}

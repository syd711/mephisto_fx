package de.mephisto.radiofx.services.time;

import java.util.Date;

/**
 * Event listener for time changes.
 */
public interface TimeListener {

  /**
   * Fired when one of the time information sources has changed.
   * @param time
   */
  void timeChanged(Date time);
}

package de.mephisto.radiofx.services.time;

import java.util.Date;

/**
 * Service for retrieving the local time.
 */
public interface TimeService {

  /**
   * Returns the local time.
   * @return
   */
  Date getTime();

  /**
   * Registers a new listener that fires once the time changes.
   * @param listener
   */
  void addTimeListener(TimeListener listener);
}

package de.mephisto.radiofx.services.time.impl;

import de.mephisto.radiofx.services.IServiceModel;

import java.util.Date;

/**
 * Service model for the date time service.
 */
public class DateTimeInfo implements IServiceModel{
  private Date date;

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}

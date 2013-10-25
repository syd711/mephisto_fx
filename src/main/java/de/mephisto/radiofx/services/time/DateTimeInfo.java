package de.mephisto.radiofx.services.time;

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

  @Override
  public boolean equals(Object obj) {
    return obj != null && obj instanceof DateTimeInfo && ((DateTimeInfo)obj).getDate().getTime() == date.getTime();
  }

  @Override
  public void setActive(boolean b) {
    //not used
  }

  @Override
  public boolean isActive() {
    return false;  //not used
  }
}

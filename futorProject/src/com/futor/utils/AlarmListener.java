package com.futor.utils;

import com.futor.utils.AlarmEntry;

/**
  * The listener interface for receiving alarm events.
  *
  * @author  Olivier Dedieu, David Sims, Jim Lerner
  * @version 1.4, 2001/01/16
  */
public interface AlarmListener {

  /**
    * Invoked when an alarm is triggered.
    *
    * @param entry the AlarmEntry which has been triggered.
    */
  public abstract void handleAlarm(AlarmEntry entry);
}

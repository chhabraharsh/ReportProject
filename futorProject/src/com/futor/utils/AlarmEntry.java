package com.futor.utils;


import java.util.Calendar;
import java.util.Date;

/**
  * This class contains the attributes of an alarm.
  *
  * @author  Olivier Dedieu, David Sims, Simon Bécot, Jim Lerner, Ralph Schaer
  * @version 1.4, 2001/01/16
  */
public class AlarmEntry implements Comparable, java.io.Serializable {
  public int minute = -1;
  public int hour = -1;
  public int dayOfMonth = -1;
  public int month = -1;
  public int dayOfWeek = -1;
  public int year = -1;
  public boolean isRelative;
  public boolean isRepetitive;
  public long alarmTime;
  public transient AlarmListener listener;
  private transient boolean debug = false;

  private void debug(String s) {
    if (debug)
      System.out.println("[" + Thread.currentThread().getName() + "] AlarmEntry: " + s);
  }

  /**
    * Creates a new AlarmEntry.
    *
    * @param date the alarm date to be added.
    * @param listener the alarm listener.
    * @exception PastDateException if the alarm date is in the past
    * (or less than 1 second closed to the current date).
    */
  public AlarmEntry(Date date, AlarmListener listener) throws PastDateException {
    this.listener = listener;
    Calendar alarm = Calendar.getInstance();
    alarm.setTime(date);
    minute = alarm.get(Calendar.MINUTE);
    hour = alarm.get(Calendar.HOUR_OF_DAY);
    dayOfMonth = alarm.get(Calendar.DAY_OF_MONTH);
    month = alarm.get(Calendar.MONTH);
    year = alarm.get(Calendar.YEAR);
    isRepetitive = false;
    isRelative = false;
    alarmTime = date.getTime();
    checkAlarmTime();
  }


  /**
    * Creates a new AlarmEntry.
    *
    * @param delay the alarm delay in minute (relative to now).
    * @param isRepetitive <code>true</code> if the alarm must be
    * reactivated, <code>false</code> otherwise.
    * @param listener the alarm listener.
    * @exception PastDateException if the alarm date is in the past
    * (or less than 1 second closed to the current date).
    */
  public AlarmEntry(int delay, boolean isRepetitive, AlarmListener listener)
    throws PastDateException {
    if (delay < 1) {
      throw new PastDateException();
    }

    minute = delay;
    this.listener = listener;
    this.isRepetitive = isRepetitive;
    if (minute < 1)
      throw new IllegalArgumentException();

    isRelative = true;
    updateAlarmTime();
  }

  /**
    * Creates a new AlarmEntry.
    *
    * @param minute minute of the alarm. Allowed values 0-59.
    * @param hour hour of the alarm. Allowed values 0-23.
    * @param dayOfMonth day of month of the alarm (-1 if every
    * day). This attribute is exclusive with
    * <code>dayOfWeek</code>. Allowed values 1-31.
    * @param month month of the alarm (-1 if every month). Allowed values
    * 0-11 (0 = January, 1 = February, ...). <code>java.util.Calendar</code>
    * constants can be used.
    * @param dayOfWeek day of week of the alarm (-1 if every
    * day). This attribute is exclusive with
    * <code>dayOfMonth</code>. Allowed values 1-7 (1 = Sunday, 2 =
    * Monday, ...). <code>java.util.Calendar</code> constants can  be used.
    * @param year year of the alarm. When this field is not set
    * (i.e. -1) the alarm is repetitive (i.e. it is rescheduled when
    *  reached).
    * @param listener the alarm listener.
    * @return the AlarmEntry.
    * @exception PastDateException if the alarm date is in the past
    * (or less than 1 second closed to the current date).
    */
  public AlarmEntry(int minute, int hour, int dayOfMonth, int month,
                    int dayOfWeek, int year, AlarmListener listener)
    throws PastDateException {
    this.minute = minute;
    this.hour = hour;
    this.dayOfMonth = dayOfMonth;
    this.month = month;
    this.dayOfWeek = dayOfWeek;
    this.year = year;
    this.listener = listener;
    isRepetitive = (year == -1);
    isRelative = false;
    updateAlarmTime();
    checkAlarmTime();
  }

  /**
    * Updates the alarm time for repetitive alarms.
    */
  public void updateAlarmTime() {
    if (isRelative) {
      alarmTime = System.currentTimeMillis() + (minute * 60000);
      return;
    }

    Calendar now = Calendar.getInstance();
    Calendar alarm = (Calendar)now.clone();
    debug("now: " + now.getTime());

    if (year != -1) {
      alarm.set(Calendar.YEAR, year);
    }

    if (month != -1) {
      alarm.set(Calendar.MONTH, month);
    }

    if (hour != -1) {
	alarm.set(Calendar.HOUR_OF_DAY, hour);
    }

    if (minute != -1) {
	alarm.set(Calendar.MINUTE, minute);
    }

    alarm.set(Calendar.SECOND, 0);


    // Increments minute if now >= alarm (for every minute alarms)
    if (minute == -1) {
      alarm.add(Calendar.MINUTE, 1);
    }

    // Increments hour if now >= alarm (for every hour alarms)
    if (hour == -1 && minute != -1 && now.get(Calendar.MINUTE) >= minute) {
      alarm.add(Calendar.HOUR_OF_DAY, 1);
    }


   // Incrementes dayOfYear if now >= alarm (for everyday alarms)
    if (dayOfMonth == -1 &&
    	dayOfWeek == -1 &&
    	hour != -1 && minute != -1 &&
        (now.get(Calendar.HOUR_OF_DAY) > hour ||
         (now.get(Calendar.HOUR_OF_DAY) == hour &&
          now.get(Calendar.MINUTE) >= minute))) {
      alarm.add(Calendar.DAY_OF_YEAR, 1); // Fix 31dec bug
    }

    // Incrementes year if now >= alarm (for monthly or yearly alarms)
    if (month != -1 && year == -1 &&
        (now.get(Calendar.MONTH) > month ||
         (now.get(Calendar.MONTH) == month &&
          (now.get(Calendar.DAY_OF_MONTH) > dayOfMonth ||
           (now.get(Calendar.DAY_OF_MONTH) == dayOfMonth &&
            (now.get(Calendar.HOUR_OF_DAY) > hour ||
             (now.get(Calendar.HOUR_OF_DAY) == hour &&
              (now.get(Calendar.MINUTE) >= minute)))))))) {
        alarm.add(Calendar.YEAR, 1);
      }

    // Weekly alarms
    if (dayOfWeek != -1) {
      int deltaOfDay = (7 + (dayOfWeek - now.get(Calendar.DAY_OF_WEEK))) % 7;
      debug("deltaOfDay: " + deltaOfDay);
      if (deltaOfDay != 0) {
	    alarm.add(Calendar.DAY_OF_YEAR, deltaOfDay);
      }
      // Incrementes week if now >= alarm
      else if (now.get(Calendar.HOUR_OF_DAY) > hour ||
	       (now.get(Calendar.HOUR_OF_DAY) == hour &&
		now.get(Calendar.MINUTE) >= minute)) {
	    alarm.add(Calendar.WEEK_OF_YEAR, 1);
      }
    }

    // Monthly alarms
    else if (dayOfMonth != -1) {
      alarm.set(Calendar.DAY_OF_MONTH, dayOfMonth);

      // Incrementes month if now >= alarm (for weekly alarms)
      if (month == -1 &&
          (now.get(Calendar.DAY_OF_MONTH) > dayOfMonth ||
           (now.get(Calendar.DAY_OF_MONTH) == dayOfMonth &&
            (now.get(Calendar.HOUR_OF_DAY) > hour ||
             (now.get(Calendar.HOUR_OF_DAY) == hour &&
              now.get(Calendar.MINUTE) >= minute))))) {
        if (year != -1) { // Fix december bug
          alarm.roll(Calendar.MONTH, true);
        } else {
          alarm.add(Calendar.MONTH, 1);
        }
      }
    }

    debug("alarm: " + alarm.getTime());

    alarmTime = alarm.getTime().getTime();
  }

  /**
    * Checks that alarm is not in the past (or less than 1 second
    * closed to the current date).
    *
    * @exception PastDateException if the alarm date is in the past
    * (or less than 1 second closed to the current date).
    */
    public void checkAlarmTime() throws PastDateException {
    long delay = alarmTime - System.currentTimeMillis();
    if (delay <= 1000) {
      throw new PastDateException();
    }
  }

  /**
    * Returns a string representation of this alarm.
    *
    * @return the string.
    */
  public String toString() {
    if (year != -1) {
      return "Alarm at " + new Date(alarmTime);
    }
    StringBuffer sb = new StringBuffer("Alarm params");
    sb.append(" minute="); sb.append(minute);
    sb.append(" hour="); sb.append(hour);
    sb.append(" dayOfMonth="); sb.append(dayOfMonth);
    sb.append(" month="); sb.append(month);
    sb.append(" dayOfWeek="); sb.append(dayOfWeek);
    sb.append(" (next alarm date=" + new Date(alarmTime) + ")");
    return sb.toString();
  }

  // ----------------------------------------------------------------------
  //                      Comparable interface
  // ----------------------------------------------------------------------

  /**
    * Compares this AlarmEntry with the specified AlarmEntry for order.
    *
    * @param obj the AlarmEntry with which to compare.
    * @return a negative integer, zero, or a positive integer as this
    * AlarmEntry is less than, equal to, or greater than the given
    * AlarmEntry.
    * @exception ClassCastException if the specified Object's type
    * prevents it from being compared to this AlarmEntry.
    */
  public int compareTo(Object obj) {
    AlarmEntry entry = (AlarmEntry)obj;
    if (alarmTime < entry.alarmTime)
      return -1;
    if (alarmTime > entry.alarmTime)
      return 1;
    return 0;
  }


  /**
    * Indicates whether some other AlarmEntry is "equal to" this one.
    *
    * @param obj the AlarmEntry with which to compare.
    * @return <code>true if this AlarmEntry has the same
    * <code>alarmTime</code> as the <code>alarmTime</code> of the obj
    * argument; <code>false</code> otherwise.
    */
  public boolean equals(Object obj) {
      if (obj == this)
          return true;
      if (obj == null || !(obj instanceof AlarmEntry))
          return false;
      AlarmEntry entry = (AlarmEntry) obj;
      if (alarmTime == entry.alarmTime) {
          return true;
      }
      return false;
  }
}










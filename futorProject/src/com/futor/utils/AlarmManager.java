package com.futor.utils;

import java.util.Calendar;
import java.util.Date;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
  * This class implements an alarm manager similar to Unix <code>cron</code>
  * and <code>at</code> daemons. It is intended to fire events
  * when alarms' date and time match the current ones. Alarms are
  * added dynamically and can be one-shot or repetitive
  * (i.e. rescheduled when matched). Time unit is seconds. Alarms
  * scheduled less than one second to the current time are rejected (a
  * <code>PastDateException</code> is thrown).<p>
  *
  * The alarm scheduler has been designed to
  * manage a large quantity of alarms (it uses a priority queue to
  * optimize alarm dates selection) and to reduce the use of the CPU
  * time (the AlarmManager's thread is started only when there are
  * alarms to be managed and it sleeps until the next alarm
  * date). <p>
  *
  * Note : because of clocks' skews some alarm dates may be erroneous,
  * particularly if the next alarm date is scheduled for a remote time
  * (e.g. more than a few days). In order to avoid that problem,
  * well-connected machines can use the <a
  * href="ftp://ftp.inria.fr/rfc/rfc13xx/rfc1305.Z">Network Time
  * Protocol</a> (NTP) to synchronize their clock.<p>
  *
  * Example of use:
  * <pre>
  *  // Creates a new AlarmManager
  *  AlarmManager mgr = new AlarmManager();
  *
  *  // Date alarm (non repetitive)
  *  mgr.addAlarm(new Date(System.currentTimeMillis() + 300000),
  *               new AlarmListener() {
  *    public void handleAlarm(AlarmEntry entry) {
  *      System.out.println("5 minutes later");
  *    }
  *  });
  *
  *  Calendar cal = Calendar.getInstance();
  *  cal.add(Calendar.WEEK_OF_YEAR, 1);
  *  mgr.addAlarm(cal.getTime(), new AlarmListener() {
  *    public void handleAlarm(AlarmEntry entry) {
  *      System.out.println("One week later");
  *    }
  *  });
  *
  *  // Alarm with a delay (in minute) relative to the current time.
  *  mgr.addAlarm(1, true, new AlarmListener() {
  *    public void handleAlarm(AlarmEntry entry) {
  *      System.out.println("1 more minute ! (" + new Date() + ")");
  *    }
  *  });
  *
  *  // Cron-like alarm (minute, hour, day of month, month, day of week, year)
  *  // Repetitive when the year is not specified.
  *
  *  mgr.addAlarm(-1, -1, -1, -1, -1, -1, new AlarmListener() {
  *    public void handleAlarm(AlarmEntry entry) {
  *      System.out.println("Every minute (" + new Date() + ")");
  *    }
  *  });
  *
  *  mgr.addAlarm(5, -1, -1, -1, -1, -1, new AlarmListener() {
  *    public void handleAlarm(AlarmEntry entry) {
  *      System.out.println("Every hour at 5' (" + new Date() + ")");
  *    }
  *  });
  *
  *  mgr.addAlarm(00, 12, -1, -1, -1, -1, new AlarmListener() {
  *    public void handleAlarm(AlarmEntry entry) {
  *      System.out.println("Lunch time (" + new Date() + ")");
  *    }
  *  });
  *
  *  mgr.addAlarm(07, 14, 1, Calendar.JANUARY, -1, -1, new AlarmListener() {
  *    public void handleAlarm(AlarmEntry entry) {
  *      System.out.println("Happy birthday Lucas !");
  *    }
  *  });
  *
  *  mgr.addAlarm(30, 9, 1, -1, -1, -1, new AlarmListener() {
  *    public void handleAlarm(AlarmEntry entry) {
  *      System.out.println("On the first of every month at 9:30");
  *    }
  *  });
  *
  *  mgr.addAlarm(00, 18, -1, -1, Calendar.FRIDAY, -1, new AlarmListener() {
  *    public void handleAlarm(AlarmEntry entry) {
  *      System.out.println("On every Friday at 18:00");
  *    }
  *  });
  *
  *  mgr.addAlarm(00, 13, 1, Calendar.AUGUST, -1, 2001,  new AlarmListener() {
  *    public void handleAlarm(AlarmEntry entry) {
  *      System.out.println("2 years that this class was programmed !");
  *    }
  *  });
  * </pre>
  *
  * @author  Olivier Dedieu, David Sims, Jim Lerner
  * @version 1.4, 2001/01/16
  */

public class AlarmManager {

  protected AlarmWaiter waiter;
  protected SortedSet /* of AlarmEntry */ queue; // was a PriorityQueue
  private boolean debug = false;
  private boolean isAlive = false;
  private boolean waitingFlag = false;

  private void debug(String s) {
    if (debug)
      System.out.println("[" + Thread.currentThread().getName() + "] AlarmManager: " + s);
  }

  /**
    * Creates a new AlarmManager. The waiter thread will be started
    * only when the first alarm listener will be added.
    *
    * @param isDaemon true if the waiter thread should run as a daemon.
    * @param threadName the name of the waiter thread
    */
  public AlarmManager(boolean isDaemon, String threadName) {
    queue = (SortedSet) new TreeSet(); // PriorityQueue(false);
    waiter = new AlarmWaiter(this, isDaemon, threadName);
   }

  /**
    * Creates a new AlarmManager. The waiter thread will be started
    * only when the first alarm listener will be added. The waiter
    * thread will <i>not</i> run as a daemon.
    */
  public AlarmManager() {
    this(false, "AlarmManager");
  }

  /**
    * Adds an alarm for a specified date.
    *
    * @param date the alarm date to be added.
    * @param listener the alarm listener.
    * @return the AlarmEntry.
    * @exception PastDateException if the alarm date is in the past
    * or less than 1 second closed to the current date).
    */
  public synchronized AlarmEntry addAlarm(Date date,
                             AlarmListener listener) throws PastDateException {
    AlarmEntry entry = new AlarmEntry(date, listener);
    addAlarm(entry);
    return entry;
  }

  /**
    * Adds an alarm for a specified delay.
    *
    * @param delay the alarm delay in minute (relative to now).
    * @param isRepetitive <code>true</code> if the alarm must be
    * reactivated, <code>false</code> otherwise.
    * @param listener the alarm listener.
    * @return the AlarmEntry.
    * @exception PastDateException if the alarm date is in the past
    * (or less than 1 second closed to the current date).
    */
  public synchronized AlarmEntry addAlarm(int delay, boolean isRepetitive,
                             AlarmListener listener) throws PastDateException {
    AlarmEntry entry = new AlarmEntry(delay, isRepetitive, listener);
    addAlarm(entry);
    return entry;
  }

  /**
    * Adds an alarm for a specified date.
    *
    * @param minute minute of the alarm. Allowed values 0-59.
    * @param hour hour of the alarm. Allowed values 0-23.
    * @param dayOfMonth day of month of the alarm (-1 if every
    * day). This attribute is exclusive with
    * <code>dayOfWeek</code>. Allowed values 1-7 (1 = Sunday, 2 =
    * Monday, ...). <code>java.util.Calendar</code> constants can  be used.
    * @param month month of the alarm (-1 if every month). Allowed values
    * 0-11 (0 = January, 1 = February, ...). <code>java.util.Calendar</code>
    * constants can be used.
    * @param dayOfWeek day of week of the alarm (-1 if every
    * day). This attribute is exclusive with
    * <code>dayOfMonth</code>. Allowed values 1-31.
    * @param year year of the alarm. When this field is not set
    * (i.e. -1) the alarm is repetitive (i.e. it is rescheduled when
    *  reached).
    * @param listener the alarm listener.
    * @return the AlarmEntry.
    * @exception PastDateException if the alarm date is in the past
    * (or less than 1 second closed to the current date).
    */
  public synchronized AlarmEntry addAlarm(int minute, int hour,
                             int dayOfMonth, int month,
                             int dayOfWeek,
                             int year,
                             AlarmListener listener)
    throws PastDateException {

    AlarmEntry entry = new AlarmEntry(minute, hour,
                                      dayOfMonth, month,
                                      dayOfWeek,
                                      year,
                                      listener);
    addAlarm(entry);
    return entry;
  }

  /**
    * Adds an alarm for a specified AlarmEntry
    *
    * @param entry the AlarmEntry.
    * @exception PastDateException if the alarm date is in the past
    * (or less than one second too closed to the current date).
    */
   public synchronized void addAlarm(AlarmEntry entry) throws PastDateException {
     debug("Add a new alarm entry : " + entry);
     if (queue.add(entry)) {
       debug("This new alarm is the top one, update the waiter thread");
       waiter.update(((AlarmEntry)queue.first()).alarmTime);
//         waiter.update(((AlarmEntry)queue.getTop()).alarmTime);
     }
  }


  /**
    * Removes the specified AlarmEntry.
    *
    * @param entry the AlarmEntry that needs to be removed.
    * @return <code>true</code> if there was an alarm for this date,
    * <code>false</code> otherwise.
    */
  public synchronized boolean removeAlarm(AlarmEntry entry) {
    if (!queue.contains(entry)) {
      return false;
    } // if

    // remove the item from the queue
    if (queue.remove(entry)) {
      // do not update the waiter if there are no more items in the queue
      if (queue.size() > 0) {
        waiter.update(( (AlarmEntry) queue.first()).alarmTime);
      } // if
      //        waiter.update(((AlarmEntry)queue.getTop()).alarmTime);
    } // if

    return true;
  } // removeAlarm()

  /**
    * Removes all the alarms. No more alarms, even newly added ones, will
    * be fired.
    */
  public synchronized void removeAllAlarms() {
    waiter.stop();
    waiter = null;
    queue.clear();
  }

  /**
    Tests whether the supplied AlarmEntry is in the manager.

    @param AlarmEntry
    @return boolean whether AlarmEntry is contained within the manager
    */
  public synchronized boolean containsAlarm(AlarmEntry alarmEntry) {
    return queue.contains(alarmEntry);
  } // containsAlarm()

  /**
    * Returns a copy of all alarms in the manager.
    */
  public synchronized List /* AlarmEntry */ getAllAlarms() {
    final LinkedList result = new LinkedList();

    Iterator iterator = queue.iterator();
    while (iterator.hasNext()) {
      result.add(iterator.next());
    } // while

    return result;
  } // getAllAlarms()

  /**
    * This is method is called when an alarm date is reached. It
    * is only be called by the the AlarmWaiter or by itself (if
    * the next alarm is in less than 1 second.)
    */
  protected synchronized void notifyListeners() {
    debug("I receive an alarm notification");

    // if the queue is empty, there's nothing to do
    if (queue.isEmpty()) {
      return;
    } // if

    // Removes this alarm and notifies the listener
    AlarmEntry entry = (AlarmEntry) queue.first();
    queue.remove(entry);
    try {
      entry.listener.handleAlarm(entry);
    }
    catch(Exception e) {}

    // Reactivates the alarm if it is repetitive
    if (entry.isRepetitive) {
      entry.updateAlarmTime();
      queue.add(entry);
    }

    // Notifies the AlarmWaiter thread for the next alarm
    if (queue.isEmpty()) {
      debug("There is no more alarm to manage");
    }
    else {
      long alarmTime = ((AlarmEntry)queue.first()).alarmTime;
      if (alarmTime - System.currentTimeMillis() < 1000) {
        debug("The next alarm is very close, I notify the listeners now");
        notifyListeners();
      }
      else {
        debug("I update the waiter for " + queue.first());
        waiter.restart(alarmTime);
      }
    } // else
  } // notifyListeners()

  /**
    * Stops the waiter thread before ending.
    */
  public void finalize() {
    if (waiter != null)
      waiter.stop();
  }

  /**
   * The method checks the state of the waiter thread.
   * The waiter thread can have 3 states.
   * 1) Waiting
   * 2) Running
   * 3) Dead
   * The start time of an alarm can be reset if
   * 1) The thread is dead.
   * 2) The thread is alive but in waiting state.
   * The start time of an alarm cannot be reset if
   * the alarm is executing.
   * @return boolean false:if the thread can be reset
   * else true.
   */
  public boolean isAlive()
  {
      if(waiter != null)
      {
          isAlive = waiter.isRunning();
          if(isAlive)
          {
              waitingFlag = waiter.getWaitingFlag();
              if(waitingFlag)
              {
                  return false;
              }
              else
              {
                  return true;
              }
          }
      }
      return false;
  }


}




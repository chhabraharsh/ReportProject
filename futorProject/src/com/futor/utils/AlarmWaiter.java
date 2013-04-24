package com.futor.utils;

import com.futor.utils.AlarmManager;

/**
 * This class manages the thread which sleeps until the next alarm.
 * Methods are synchronized to prevent interference fromthe AlarmWaiter thread
 * and external threads.
 *
 * @author  Olivier Dedieu, David Sims, Jim Lerner
 * @version 1.4, 2001/01/16
 */
public class AlarmWaiter implements Runnable {
 protected AlarmManager mgr;
 protected Thread thread;
 private long sleepUntil = -1;
 private boolean debug = false;
 private boolean shutdown = false;

 private boolean isWaiting = false;
 private boolean isAlive = false;


 private void debug(String s) {
   if (debug)
     System.out.println("[" + Thread.currentThread().getName() + "] AlarmWaiter: " + s);
 }

 /**
   * Creates a new AlarmWaiter.
   *
   * @param isDaemon true if the waiter thread should run as a daemon.
   * @param threadName the name of the waiter thread
   */
 public AlarmWaiter(AlarmManager mgr, boolean isDaemon, String waiterName) {
   this.mgr = mgr;

   // start the thread
   thread = new Thread(this, waiterName);
   thread.setDaemon(isDaemon);
   thread.start();
 }

 /**
   * Updates the time to sleep.
   *
   * @param sleepUntil the new time to sleep until.
   */
 public synchronized void update(long sleepUntil) {
   this.sleepUntil = sleepUntil;
   debug("Update for " + sleepUntil); // timeToSleep);
   debug("I notify the thread to update its sleeping time");
   notify();
 }

 /**
   * Restarts the thread for a new time to sleep until.
   *
   * @param sleepUntil the new time to sleep until.
   */
 public synchronized void restart(long sleepUntil) {
   this.sleepUntil = sleepUntil;
   notify();
 }

 /**
   * Stops (destroy) the thread.
   */
 public synchronized void stop() {
   shutdown = true;
   notify();
 }


 public synchronized void run() {
   debug("I'm running");
   while(!shutdown) {
     try {
       // check if there's alarm here
       setWaitingFlag(true);
       if (sleepUntil <= 0) {
         // no alarm here. So wait for a new alarm to come along.

         wait();

       } // if
       else {
         // yes, there's an alarm here. Wait until the alarm is ready.
         long timeout = sleepUntil - System.currentTimeMillis();
         if (timeout > 0) {
           wait(timeout);
         } // if
       } // if

       setWaitingFlag(false);

       // now that we've awakened again, check if an alarm is due (within
       // 1 second)
       if (sleepUntil >= 0 && (sleepUntil - System.currentTimeMillis() < 1000)) {
         // yes, an alarm is ready. Notify the listeners.
         sleepUntil = -1;
         debug("notifying listeners");
         mgr.notifyListeners();
       } // if

     }
     catch(InterruptedException e) {
       debug("I'm interrupted");
     }
   }
   debug("I'm stopping");
 }
  /**
   * The method sets the flag before and after the
   * wait() being called in the run
   * @param waitingFlag
   */

 private synchronized void setWaitingFlag(boolean waitingFlag)
 {
    isWaiting = waitingFlag;
 }

 public boolean getWaitingFlag()
 {
     return isWaiting;

 }

 public boolean isRunning()
 {
     if(thread != null)
     {
         isAlive = thread.isAlive();
     }
     return isAlive;
 }

}



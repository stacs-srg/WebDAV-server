/*
 * Created on Dec 20, 2004 at 4:39:04 PM.
 */

package uk.ac.standrews.cs.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Queue of actions to be performed, serviced by a number of threads.
 * Queued actions may be performed in any order. Threads are created on
 * demand as actions arrive, up to a given limit. If a thread is idle
 * for more than a given period it dies off.
 *
 * @author graham
 */
public class ActionQueue {
    
    /******************** Static Fields *********************/
    
    // TODO general mechanism for exposing and controlling configuration parameters.
    
    /**
     * The default capacity of the queue.
     */
    public static final int DEFAULT_QUEUE_CAPACITY = 5;
    
    /**
     * The default maximum number of threads servicing the queue.
     */
    public static final int DEFAULT_MAX_THREADS = 2;
    
    /**
     * The default time-out in milliseconds after which an idle thread will die.
     */
    public static final int DEFAULT_IDLE_TIMEOUT = 5000;
    
    /************************ Fields ************************/
	
    private List action_list;
    private int number_of_threads, max_threads, idle_timeout, queue_capacity;
    
    private Semaphore mutex, not_empty, not_full;

    /********************* Constructors *********************/
    
    /**
     * Constructor with default capacity, number of service threads and idle time-out.
     */
    public ActionQueue() {
        
        this(DEFAULT_QUEUE_CAPACITY, DEFAULT_MAX_THREADS, DEFAULT_IDLE_TIMEOUT);
    }

    /**
     * Constructor with specified capacity and number of service threads.
     * 
     * @param queue_capacity the capacity of the queue
     * @param max_threads the maximum number of service threads
     * @param idle_timeout the time-out in milliseconds after which an idle thread will die
     */
    public ActionQueue(int queue_capacity, int max_threads, int idle_timeout) {
        
    	this.queue_capacity = queue_capacity;
        this.max_threads = max_threads;
        this.idle_timeout = idle_timeout;
        
        // No threads are created initially.
        number_of_threads = 0;
        
        // Used for mutual exclusion on shared data structures.
        mutex = new Semaphore(1);
        
        // Used for synchronisation on queue being empty.
        not_empty = new Semaphore(0);
        
        // Used for synchronisation on queue being full.
        not_full =  new Semaphore(queue_capacity);

        // Don't need a synchronized list (Collections.SynchronizedList()) since
        // all access to the ArrayList will be be synchronised.
        action_list = new ArrayList();
    }
    
    /*********************** Methods ************************/

    /**
     * Queues the given action to be performed as soon as possible. Returns as soon as there is
     * space in the queue to store the action.
     * 
     * @param action the action to be queued
     */
    public void enqueue(Action action) {
        
        // Wait until there is space in the queue.
        not_full.semWait();

        // Wait to enter critical section.
        mutex.semWait();
        
        // --------------- Start critical section ---------------
        
        // Add the action to the list.
        action_list.add(action);
        
        // If there are no idle threads, and the maximum number of threads hasn't been reached,
        // start a new one.        
        if (not_empty.numberWaiting() == 0 && number_of_threads < max_threads) {
            
            newServiceThread();
            
            number_of_threads ++;
        }
        
        // ---------------- End critical section ----------------
        
        // Leave critical section.
        mutex.semSignal();

        // Signal availability of a new action.
        not_empty.semSignal();
    }
    
    /**
     * Returns the next action in the queue. Delays until an action is available if the
     * queue is empty.
     * 
     * @return the next action
     */
    public Action dequeue() throws TimeoutException {
        
        // Wait until there an action is available in the queue, or the timeout is exceeded.
        not_empty.semWait(idle_timeout);

        // Wait to enter critical section.
        mutex.semWait();
     
        // --------------- Start critical section ---------------
        
        // May have entered critical section with an empty queue, due to timeout.
        // If so, clean up and throw exception.
        if (action_list.size() == 0) {
            
            mutex.semSignal();
            
            throw new TimeoutException("ActionQueue::dequeue - timeout period exceeded");
        }
        
        // Get the action at the front of the list.
        Action next_action = (Action) action_list.get(0);

        // Remove it from the list.
        action_list.remove(0);
   
        // ---------------- End critical section ----------------
        
        // Leave critical section.
        mutex.semSignal();

        // Signal availability of space in the queue.
        not_full.semSignal();
     
        return next_action;
    }

	/**
	 * Returns the number of free slots in the queue.
	 * 
	 * @return the number of free slots
	 */
	public int freeSpace() {

        // Wait to enter critical section.
        mutex.semWait();
     
        // --------------- Start critical section ---------------
        
        // Calculate number of free slots.
        int free_space = queue_capacity - action_list.size();
   
        // ---------------- End critical section ----------------

        // Leave critical section.
        mutex.semSignal();

		return free_space;
	}
	
	/******************** Utility Methods *********************/

    /**
     * Creates and starts a new thread to service the action queue.
     */
    private void newServiceThread() {

        Thread service_thread = new Thread("ActionQueue service thread") {
            
            public void run() {
                
                try {
                    // Loop indefinitely removing actions from the queue.
                    while (true) dequeue().performAction();
                    
                } catch (TimeoutException e) {
                    Diagnostic.trace("ActionQueue service thread is terminating",Diagnostic.RUN);
                    mutex.semWait();
                    not_empty.semSignal();
                    number_of_threads--;
                    mutex.semSignal();
                    // Let thread die since it's been idle for too long.
                }
            }
        };
        
        service_thread.start();
    }

}

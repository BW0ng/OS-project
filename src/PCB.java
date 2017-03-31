import java.util.ArrayList;

/**
 * Class that represents a PCB
 *
 * Contains all the information needed
 * to run each job.
 *
 * Added a couple of parameters:
 *      State
 *      currentQueue
 *      IORequestTime
 */

public class PCB {

    protected double IO_LENGTH = 10.0;              // Length of I/O

    // Enum of the current state
    public enum State {
        running, terminated
    }

    // Enum of the current subqueue
    public enum Queue {
        subqueue1, subqueue2, subqueue3, subqueue4
    }

    // Enum of when the I/O was requested
    public enum IORequestTime {
        beforeEnd, atEnd, never
    }

    private int ID;
    private int memSize;
    private int currentBurst;                   // Current Burst that is being worked on
    private int burstIndex;                     // Index of the current Burst in burstSizes
    private int cumulativeCPUTime;              // Total CPU time
    private int cpuShots;                       // Number of times the PCB has run on the CPU
    private ArrayList<Integer> burstSizes;

    private SYSTEM system;                      // Global instance of the system



    private double startTime;
    private double endTime;
    private double IOCompletion;                // Time that the I/O completes

    State currentState;
    Queue currentQueue;
    IORequestTime ioState;

    private int turnNum;
    private int runTime;                        // Time that the PCB last spent on the CPU

    /**
     * Constructor used to create an instance of a PCB
     * @param ID        ID of the PCB
     * @param memSize   memory size of the PCB
     * @param burstSizes ArrayList of the bursts of the PCB
     * @param system     instance of the system
     */
    public PCB (int ID, int memSize,
                ArrayList<Integer> burstSizes, SYSTEM system) {
        this.ID = ID;
        this.memSize = memSize;

        // Initialize the burst information
        this.burstIndex = 0;
        this.burstSizes = burstSizes;
        this.currentBurst = 0;
        this.cumulativeCPUTime = 0;
        this.cpuShots = 0;
        setNumBurstCurrent();

        // Initialize the time
        this.startTime = system.clock.getClock();
        this.IOCompletion = -1;

        // Initialize the state and queue
        this.currentState = State.running;
        this.currentQueue = Queue.subqueue1;
        this.turnNum = 0;
        runTime = 0;

        this.system = system;
    }

    /**
     * Returns the ID of the PCB
     * @return ID of the PCB
     */
    public int getID() {

        return ID;
    }

    /**
     * Returns the memory size of the PCB
     * @return memory size of the PCB
     */
    public int getMemSize() {

        return memSize;
    }

    /**
     * Returns the length left on the current burst
     * @return length left on the current burst
     */
    public int getCurrentBurst() {

        return currentBurst;
    }

    /**
     * Decrements the current burst by the time the PCB last ran on the CPU
     */
    public void decCurrentBurst() {
        currentBurst = currentBurst - runTime;
    }


    /**
     * Gets the next burst and saves it in currentBurst
     */
    public void setNumBurstCurrent() {

        if ( currentState != State.terminated &&
                currentBurst <= 0) {
            this.currentBurst = burstSizes.get(burstIndex);
            burstIndex++;
        }

    }

    /**
     * Sets the queue equal to the passed in parameter
     * @param queue queue that the PCB is currently in
     */
    public void setCurrentQueue(PCB.Queue queue) {
        currentQueue = queue;
        turnNum = 0;
    }

    /**
     * Sets the I/O completion time equal to the current time plus the length of I/O
     * @param requestedTime currentTime of the Clock
     */
    public void setIOCompletion(double requestedTime) {
        IOCompletion = requestedTime + IO_LENGTH;
    }

    /**
     * Sets the runTime equal to the last amount ran on the CPU
     * @param runTime
     */
    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    /**
     * Checks to see if the PCB has finished
     */
    public void setCurrentState() {

        if (burstIndex >= burstSizes.size() && currentBurst == 0) {
            currentState = State.terminated;
            endTime = system.clock.getClock();
        }
    }

    /**
     * Sets the I/O state equal to the passed in state
     * @param iostate Time the I/O was requested
     */
    public void setIoState(IORequestTime iostate) {
        this.ioState = iostate;
    }

    /**
     * Returns the current subqueue
     * @return the current subqueue of the PCB
     */
    public PCB.Queue getCurrentQueue() {
        return currentQueue;
    }

    /**
     * Returns the number of turns the PCB has spent in that subqueue
     * @return number of turns spent in that subqueue
     */
    public int getTurnNum() {
        return turnNum;
    }

    /**
     * Returns the time the I/O will finish
     * @return time of I/O completion
     */
    public double getIOCompletion() {
        return IOCompletion;
    }

    /**
     * Returns the time the PCB was created
     * @return PCB creation time
     */
    public double getStartTime() {
        return startTime;
    }

    /**
     * Returns the time the PCB finished
     * @return PCB termination time
     */
    public double getEndTime() {
        return endTime;
    }

    /**
     * Returns the total execution time of the PCB
     * @return bursts + I/O time
     */
    public double getExecutionTime() {
        double sum = burstSizes.get(0);

        for (int i = 1; i < burstSizes.size(); i++) {
            // The next burst size plus the I/O of the right before the
            // currently counted burst
            sum = sum + burstSizes.get(i) + IO_LENGTH;
            cumulativeCPUTime = cumulativeCPUTime + burstSizes.get(i);
        }
        return sum;
    }

    /**
     * Returns the number of times the PCB has run on the CPU
     * @return number of times the PCB has run on the CPU
     */
    public int getNumShots() {
        return cpuShots;
    }

    /**
     * Return the cumulative time spent on the CPU
     * @return cumulative time spent on CPU
     */
    public int getCumulativeCPUTime() {
        return cumulativeCPUTime;
    }

    /**
     * Returns the state of the PCB
     * @return {running, terminated} state of the PCB
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * Returns the I/O request time of the PCB
     * @return {beforeEnd, atEnd, never} time I/O was requested
     */
    public IORequestTime getIoState() {
        return ioState;
    }

    /**
     * Adds one the number of turns
     */
    public void addTurn() {
        turnNum = turnNum + 1;
    }

    /**
     * Adds one to the number of CPU shots
     */
    public void addShot() {
        cpuShots = cpuShots + 1;
    }

    /**
     * Zero the turns
     * Happens when it changes queues,
     * or when an I/O is requested before the end of its burst
     */
    public void zeroTurns() {
        turnNum = 0;
    }


    /**
     * Returns a string version of the PCB
     * @return string version of the PCB
     */
    public String toString() {
        return "ID: " + ID + ", Size: " + memSize + ", Index: " + burstIndex +
                ", Turns: " + turnNum + ", Current: " + currentBurst +
                ", Bursts: " + burstSizes + ", Queue: " + currentQueue +
                ", IO: " + IOCompletion;
    }
}

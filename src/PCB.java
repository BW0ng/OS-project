import java.util.ArrayList;

/**
 * Brandon Wong
 * OS_Project
 */

public class PCB {

    protected double IO_LENGTH = 10.0;

    public enum State {
        running, terminated
    }

    public enum Queue {
        subqueue1, subqueue2, subqueue3, subqueue4
    }

    public enum IORequestTime {
        beforeEnd, atEnd, never;
    }

    private int ID;
    private int memSize;
    private int numBurstPredicted;
    private int currentBurst;
    private int burstIndex;
    private int cumulativeCPUTime;
    private int cpuShots;
    private ArrayList<Integer> burstSizes;

    private SYSTEM system;



    private double startTime;
    private double endTime;
    private double IOCompletion;

    State currentState;
    Queue currentQueue;
    IORequestTime ioState;

    private int turnNum;
    private int runTime;

    // TODO Needs to save the current time
    public PCB (int ID, int memSize,
                ArrayList<Integer> burstSizes, SYSTEM system) {
        this.ID = ID;
        this.memSize = memSize;

        // Initialize the burst information
        this.burstIndex = 0;
        this.burstSizes = burstSizes;
        this.numBurstPredicted = burstSizes.size();
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

    public int getID() {

        return ID;
    }

    public int getMemSize() {

        return memSize;
    }

    public int getNumBurstPredicted() {

        return numBurstPredicted;
    }

    public int getCurrentBurst() {

        return currentBurst;
    }

    public void decCurrentBurst() {
        currentBurst = currentBurst - runTime;
    }


    public void setNumBurstCurrent() {

        if ( currentState != State.terminated &&
                currentBurst <= 0) {
            this.currentBurst = burstSizes.get(burstIndex);
            burstIndex++;
        }

    }

    public void setCurrentQueue(PCB.Queue queue) {
        currentQueue = queue;
        turnNum = 0;
    }

    public void setIOCompletion(double requestedTime) {
        IOCompletion = requestedTime + IO_LENGTH;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public void setCurrentState() {
        // TODO Delete this

        if (burstIndex >= burstSizes.size() && currentBurst == 0) {
            currentState = State.terminated;
            endTime = system.clock.getClock();
        }
    }

    public void setIoState(IORequestTime iostate) {
        this.ioState = iostate;
    }

    public PCB.Queue getCurrentQueue() {
        return currentQueue;
    }

    public int getTurnNum() {
        return turnNum;
    }

    public double getIOCompletion() {
        return IOCompletion;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }

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

    public int getNumShots() {
        return cpuShots;
    }

    public int getCumulativeCPUTime() {
        return cumulativeCPUTime;
    }

    public State getCurrentState() {
        return currentState;
    }

    public IORequestTime getIoState() {
        return ioState;
    }


    public void addTurn() {
        turnNum = turnNum + 1;
    }

    public void addShot() {
        cpuShots = cpuShots + 1;
    }

    public void zeroTurns() {
        turnNum = 0;
    }

    // TODO Change
    /*
    public String toString() {
        return "ID: " + ID + ", Size: " + memSize + " Index: " + burstIndex + ", Current: " + currentBurst
                + ", Bursts: " + burstSizes + ", IO: " + IOCompletion;
    }
    */

    public String toString() {
        return "ID: " + ID + ", Current: " + currentBurst + ", Queue: " + currentQueue +
                ", Turn: " + turnNum + ", Index: " + burstIndex
                + ", IO: " + IOCompletion;
    }

}

/**
 * Class representing the CPU.
 * PCB's are passed to it to be
 * run. This class increments the
 * global clock
 */
public class CPU {

    public SYSTEM system;               // Global instance of system
    private int numJobsDispatched;      // Global number of jobs that have been dispatched to the CPU

    /**
     * Constructor used to create an instance of CPU
     * Takes in the instance of system as a parameter
     * @param system The instance of the system
     */
    public CPU(SYSTEM system) {
        this.system = system;
        numJobsDispatched = 0;
    }

    /**
     * Adds 1 to the number of jobs dispatched
     * Calculates the runTime that it should run between
     * the time Quantum of the subqueue and the current burst of the
     * current PCB.
     * The runtime is saved within that PCB and the I/O request time is saved
     * Finally the clock is incremented by the runtime
     * @param pcb  PCB of the Job that is being run on the CPU
     */
    public void run(PCB pcb) {

        numJobsDispatched = numJobsDispatched + 1;


        int runTime = system.scheduler.ready_Q.getQuantum(pcb.currentQueue);


        if (runTime < pcb.getCurrentBurst()) {
            pcb.setIoState(PCB.IORequestTime.never);
        } else if (runTime == pcb.getCurrentBurst()) {
            pcb.setIoState(PCB.IORequestTime.atEnd);
        } else {
            pcb.setIoState(PCB.IORequestTime.beforeEnd);
            runTime = pcb.getCurrentBurst();
        }

        pcb.setRunTime(runTime);

        for (int i = 0; i < runTime; i++) {
            system.clock.tick();
        }
    }

    /**
     * Run the CPU for a desired length of time.
     * Used when waiting for I/O to complete
     * @param duration Length of time left for I/O to complete
     */
    public void run(double duration) {
        for (int i = 0; i < (int) duration; i++) {
            system.clock.tick();
        }
    }

    /**
     * Returns the number of jobs that have run on the CPU
     * @return number of jobs that have been run on the CPU
     */
    public int getNumJobsDispatched() {
        return numJobsDispatched;
    }
}

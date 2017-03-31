/**
 * Brandon Wong
 * CS 4323 - Operating System
 * Phase 1
 * February 28, 2017
 *
 *
 * Main routine of the program takes in the
 * input file name and the memory size as
 * parameters
 */

public class SYSTEM {
    private int maxPCB = 15;        // Global max PCB
    private int numPCB;             // Global counter of the number of PCB's in use

    // System parts
    protected Memory_Manager memoryManager;
    protected Loader loader;
    protected Scheduler scheduler;
    protected Clock clock;
    protected CPU cpu;

    // Debug boolean
    protected boolean debug = false;

    // System Queues
    JobQueue job_Q;


    /**
     * Constructor to create an instance of the SYSTEM
     * @param fileName name of the input file
     * @param maxMemory max amount of memory of the system
     */
    public SYSTEM(String fileName, int maxMemory) {
        memoryManager = new Memory_Manager(maxMemory, this);
        loader = new Loader(fileName, this);
        scheduler = new Scheduler(this);
        clock = new Clock(this);
        cpu = new CPU(this);
        job_Q = new JobQueue();
        numPCB = 0;

    }

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage Statement:");
            System.out.println("\tjava SYSTEM <file path> <max memory>");
        } else {
            SYSTEM system = new SYSTEM(args[0], Integer.parseInt(args[1]));       // Initialize the System

            system.memoryManager.printStats();
            system.callLoader();

            while (true) {

                // Return the pcb from the Ready Queue
                PCB pcb = system.scheduler.dispatch();

                /*
                    If Ready_Q retrieval failed call loader while
                        there is space available
                        there is less than 15 PCB's
                        the blocked_Q is empty or nothing can be moved from the blocked_Q
                */
                if (pcb == null &&
                        system.memoryManager.hasAvailableSpace() &&
                        system.numPCB < system.maxPCB &&
                        (system.scheduler.blocked_Q.size() == 0 ||
                                (system.scheduler.blocked_Q.size() > 0 &&
                                        system.scheduler.blocked_Q.peek().getIOCompletion() > system.clock.getClock()))
                        ) {
                    system.callLoader();
                    pcb = system.scheduler.dispatch();
                }

                /*
                    If no job can be obtained from the Ready_Q
                    Check if the program is done
                 */
                if (pcb == null) {
                    pcb = system.checkIfFinished();
                }

                /*
                    If the PCB isn't null run that PCB on the CPU and update it
                 */
                if (pcb != null) {
                    system.cpu.run(pcb);
                    system.completeIO(pcb);
                    system.scheduler.update(pcb);       // Update the information within the PCB
                }

                /*
                    Check the Blocked_Q to see if any PCB's can be moved into
                    the ready_Q
                */
                system.scheduler.checkBlockedQueue();
                system.loader.noJobs = false;           // Set the no current jobs flag false


                if (pcb != null && pcb.getCurrentState() == PCB.State.terminated) {
                    system.scheduler.stats(pcb);        // Print out the job stats to JOB_LOG
                    system.decPCB();                    // Decrement the global PCB counter
                    system.memoryManager.release(pcb.getMemSize()); // Release the memory
                    system.callLoader();                            // Load jobs
                }
            }
        }



    }

    /**
     * Return the number of used PCB
     * @return number of PCB's in use
     */
    public int getNumPCB() {
        return numPCB;
    }

    /**
     * Add one to the total number of PCB's
     */
    public void addPCB() {
        numPCB = numPCB + 1;
    }

    /**
     * Decrement one from the total number of PCB's
     */
    public void decPCB() {
        numPCB = numPCB - 1;
    }

    /**
     * Determines if the program is done by checking if the loader
     * has any more jobs to load from disk, if the Blocked_Q is empty,
     * if the Ready_Q is empty, and if the Job_Q is empty. If everything
     * is true, it exits the program. Else it checks and returns a PCB from
     * the non-empty Blocked_Q.
     *
     * @return PCB from the Ready_Q after waiting for I/O
     */

    public PCB checkIfFinished() {

        if (this.loader.isFinished() &&
                this.scheduler.blocked_Q.size() == 0 &&
                this.scheduler.ready_Q.size() == 0 &&
                this.job_Q.size() == 0) {

            this.scheduler.finish();
            this.memoryManager.finish();

            /* Exit if all the queues are empty and there
               is no more to load
             */

            if (debug) {
                System.out.println("End Time: " + clock.getClock());
                System.out.println("Total: " + loader.sum);
                System.out.println("Done");
            }
            System.exit(0);
        }
            return checkBlocked();

    }

    /**
     * Checks to see if the CPU needs to run while waiting for I/O to complete.
     * Increments the number of VTU's needed to complete I/O. Then calls the method
     * to move from the Blocked_Q to the Ready_Q and returns that PCB.
     *
     * @return PCB from the Ready_Q
     */

    public PCB checkBlocked() {

        if (this.scheduler.blocked_Q.size() != 0 &&
                this.scheduler.ready_Q.size() == 0 &&
                this.job_Q.size() == 0) {
            // Find the remaining time in order to complete I/O
            this.cpu.run(this.scheduler.blocked_Q.peek().getIOCompletion()
                    - this.clock.getClock());
            this.scheduler.checkBlockedQueue();
            return this.scheduler.dispatch();
        }
        return null;
    }

    /**
     * Calls the printStats in memory manager
     * Prints out:
     *         number of allocated units of memory (UM)
     *         number of free units of memory
     *         number of jobs in the Job_Q
     *         number of jobs in the blocked_Q
     *         number of jobs in the ready_Q
     *         number of jobs delivered
     *     to the SYS_LOG every 200 VTU's
     */

    public void printStats() {
        memoryManager.printStats();
    }

    /**
     * Calls the loader while:
     *      There is space available
     *      There is available PCB's to allocate
     *      There are jobs on the disk
     *      There is not a job in the Job_Q that is trying to be loaded
     *      The loader has not finished and the job_Q is not empty
     */
    public void callLoader() {

        this.loader.noSpace = false;

        while(this.memoryManager.hasAvailableSpace() && this.numPCB < this.maxPCB
                && !this.loader.noJobs && !this.loader.noSpace && !this.loader.isFinished) {
            this.loader.loadNextJob();
        }
    }


    /**
     * Complete IO
     * @param pcb PCB of the job that needs IO
     */
    public void completeIO(PCB pcb) {
        if (pcb.getIoState() == PCB.IORequestTime.beforeEnd) {

        } else if (pcb.getIoState() == PCB.IORequestTime.atEnd) {

        }
    }
}

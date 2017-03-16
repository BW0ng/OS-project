/**
 * Brandon Wong
 * CS 4323
 * Phase 1
 * February 28, 2017
 *
 *  65803 VTU
 */

public class SYSTEM {
    protected int counter = 1;
    protected int maxPCB = 15;
    private int numPCB;

    // System parts
    protected Memory_Manager memoryManager;
    protected Loader loader;
    protected Scheduler scheduler;
    protected Clock clock;
    protected CPU cpu;

    // Debug boolean
    protected boolean debug = true;
    protected static boolean testing = true;

    private boolean isFinished = false;

    /*
          Decided to use Queues as the data structure for
          my queue since
     */
    // System Queues
    JobQueue job_Q;




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
            // TODO Need to check and wait if it needs to wait for blocked queue

            while (true && (!testing || system.counter <= 200)) {

                system.callLoader();

                // Return the pcb from the Ready Queue
                PCB pcb = system.scheduler.dispatch();

                // If Ready_Q retrieval failed call loader
                if (pcb == null &&
                        system.scheduler.blocked_Q.size() > 0 &&
                        (system.scheduler.blocked_Q.peek().getIOCompletion() > system.clock.getClock()) &&
                        system.memoryManager.checkAvailableMemory() == 1) {
                    system.callLoader();
                }

                if (pcb == null) {
                    pcb = system.checkIfFinished();
                }

                if (!testing && pcb != null && pcb.getID() == 8) {
                    System.out.println("Counter " + system.counter + ") Clock: : " +
                            system.clock.getClock() + "\n\t" + pcb);
                }

                system.cpu.run(pcb);
                system.scheduler.update(pcb);



                if (testing) {

                    System.out.println("Iteration: " + system.counter + ", Clock: " + system.clock.getClock());
                    System.out.println(system.scheduler.ready_Q);
                }

                if (pcb.getCurrentState() == PCB.State.terminated) {
                    system.scheduler.stats(pcb);
                    system.decPCB();
                    system.memoryManager.release(pcb.getMemSize());
                }
                system.counter++;
            }
        }



    }
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
            System.out.println("Done");
            System.exit(0);
        }
            return checkBlocked();

    }

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
        System.out.println("Returning Null");
        return null;
    }

    public void printStats() {
        memoryManager.printStats();
    }

    public void callLoader() {
        while(this.memoryManager.hasAvailableSpace() && this.numPCB < this.maxPCB
                && !this.loader.isFinished()) {
            this.loader.loadNextJob();
        }
    }
}

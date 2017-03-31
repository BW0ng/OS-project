import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

/**
 * Schedules various PCB's to run on the CPU
 * and outputs the completed jobs to JOB_LOG
 */
public class Scheduler {

    private SYSTEM system;

    // Creating variables for the Queues
    Ready_Queue ready_Q;
    Block_Queue blocked_Q;

    // Header strings for the output file JOB_LOG
    String[] statsStrings = {"ID     ", "  Entered (VTU)", "   Exited (VTU)", "    Total (VTU)", "  # Shots"};

    PrintWriter writer;


    /**
     * Constructor used to create an instance of the Scheduler
     * @param system
     */
    public Scheduler(SYSTEM system) {
        this.system = system;
        ready_Q = new Ready_Queue(3, 5, 6,
                                    20, 30, 50, 60);
        blocked_Q = new Block_Queue();

        initializeFile();
    }


    /**
     * Returns a newly created PCB from the inputted Job
     * @param job Job that contains the information to create the PCB
     * @return PCB created from the inputted job, returns null if job memory size is 0.
     */
    public PCB setup(Job job) {
        if (job.getMemorySize() != 0) {
            PCB pcb = new PCB(job.getID(), job.getMemorySize(), job.getBursts(), system);
            system.addPCB();
            ready_Q.push(pcb);
            return pcb;
        } else {
            return null;
        }
    }

    /**
     * Updates the information in the PCB after it runs on the CPU
     * @param pcb PCB that just ran on the CPU
     */
    public void update(PCB pcb) {

        pcb.decCurrentBurst();      // Change the burst left
        pcb.addShot();              // Add a CPU shot
        pcb.setCurrentState();      // Sets the current state { running, terminated }
        pcb.setNumBurstCurrent();   // Saves the next burst if needed


        if (pcb.getIoState() == PCB.IORequestTime.never &&
                pcb.getCurrentState() != PCB.State.terminated) {

            // Add a turn if the I/O wasn't requested
            pcb.addTurn();

            // Change the subqueue if number of turns exceeds the number of turns of the subqueue
            if (pcb.getTurnNum() >= ready_Q.getNumTurns(pcb.getCurrentQueue())) {
                switch (pcb.getCurrentQueue()) {
                    case subqueue1:
                        pcb.setCurrentQueue(PCB.Queue.subqueue2);
                        break;
                    case subqueue2:
                        pcb.setCurrentQueue(PCB.Queue.subqueue3);
                        break;
                    case subqueue3:
                        pcb.setCurrentQueue(PCB.Queue.subqueue4);
                        break;
                    default:
                        pcb.setCurrentQueue(PCB.Queue.subqueue4);
                        break;
                }
            }
            ready_Q.push(pcb, pcb.getCurrentQueue());

        } else if (pcb.getIoState() == PCB.IORequestTime.beforeEnd
                 && pcb.getCurrentState() != PCB.State.terminated) {


            // Change to subqueue 1 is the I/O is requested from subqueue 4
            if (pcb.getCurrentQueue() == PCB.Queue.subqueue4) {
                pcb.setCurrentQueue(PCB.Queue.subqueue1);
            }

            // Save the time I/O will finish
            pcb.setIOCompletion(system.clock.getClock());

            // Zero the turns since I/O was requested before the end of the burst
            pcb.zeroTurns();

            // Add the PCB to the Blocked_Q
            blocked_Q.push(pcb);


        } else if (pcb.getCurrentState() != PCB.State.terminated){
            /*
                   If I/O is requested at the end
                   stay in the queue without incrementing
                   the turn number
             */
            pcb.setIOCompletion(system.clock.getClock());       // Save I/O completion time
            blocked_Q.push(pcb);                                // Add the PCB to the Blocked_Q
        }
    }

    /**
     * Return the first available PCB from the Ready_Q
     * @return
     */
    public PCB dispatch() {
        return ready_Q.pop();
    }

    /**
     * Initialize the output file, JOB_LOG, with the header strings
     */
    public void initializeFile() {

        try {

            File file = new File("JOB_LOG");

            if (file.exists()) {
                file.delete();
            }
            writer = new PrintWriter(new FileWriter("JOB_LOG", true));

            writer.printf(" -------------------------------------------------------------------\n");

            writer.printf("| %s %s %s %s %s |\n",
                    statsStrings[0], statsStrings[1], statsStrings[2], statsStrings[3], statsStrings[4]);
           writer.println(" -------------------------------------------------------------------\n");
        } catch (IOException exception) {
            if (system.debug) {
                System.out.println("Could not find file: JOB_LOG");
            }
        }
    }

    /**
            Print job stats to JOB_LOG

            job ID
            time job entered system
            time job is leaving system
            execution time
            number of CPU shots used by the job
     */
    public void stats(PCB pcb) {

        writer.printf("  %-7d %15.0f %15.0f %15.0f %9d%n",
                pcb.getID(), pcb.getStartTime(), pcb.getEndTime(), pcb.getExecutionTime(), pcb.getNumShots());
        writer.flush();
    }

    /**
     * Close the file
     */
    public void finish() {
        writer.close();
    }

    /**
     * Check the Blocked_Q to see if there are any PCB's that have completed I/O
     * If so, move them to the ready_Q
     */
    public void checkBlockedQueue() {
        while (blocked_Q.size() > 0 &&
                (blocked_Q.peek()).getIOCompletion() <= system.clock.getClock()) {
            PCB tempPCB = blocked_Q.pop();
            ready_Q.push(tempPCB, tempPCB.getCurrentQueue());
        }
    }
}

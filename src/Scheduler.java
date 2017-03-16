import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

/**
 * Brandon Wong
 * OS_Project
 */
public class Scheduler {

    private SYSTEM system;

    // Creating variables for the Queues
    Ready_Queue ready_Q;
    Block_Queue blocked_Q;

    String[] statsStrings = {"ID     ", "  Entered (VTU)", "   Exited (VTU)", "    Total (VTU)", "  # Shots"};

    private int numJobsDispatched;

    PrintWriter writer;


    public Scheduler(SYSTEM system) {
        this.system = system;
        ready_Q = new Ready_Queue(3, 5, 6,
                                    20, 30, 50, 60);
        blocked_Q = new Block_Queue();

        numJobsDispatched = 0;

        initializeFile();
    }


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

    // TODO updates the info in the PCB
    public void update(PCB pcb) {

        pcb.decCurrentBurst();
        pcb.addShot();
        pcb.setCurrentState();
        pcb.setNumBurstCurrent();


        if (pcb.getIoState() == PCB.IORequestTime.never &&
                pcb.getCurrentState() != PCB.State.terminated) {
            pcb.addTurn();
            if (pcb.getTurnNum() > ready_Q.getNumTurns(pcb.getCurrentQueue())) {
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
            if (pcb.getCurrentQueue() == PCB.Queue.subqueue4) {
                pcb.setCurrentQueue(PCB.Queue.subqueue1);
            }
            pcb.setIOCompletion(system.clock.getClock());
            pcb.zeroTurns();
            blocked_Q.push(pcb);


        } else if (pcb.getCurrentState() != PCB.State.terminated){
            /*
                   If I/O is requested at the end
                   stay in the queue without incrementing
                   the turn number
             */
            pcb.setIOCompletion(system.clock.getClock());
            blocked_Q.push(pcb);
        }

        checkBlockedQueue();
    }

    public PCB dispatch() {
        return ready_Q.pop();
    }

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

    /*
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

    public void finish() {
        writer.close();
    }

    public void checkBlockedQueue() {
        while (blocked_Q.size() > 0 &&
                (blocked_Q.peek()).getIOCompletion() <= system.clock.getClock()) {
            PCB tempPCB = blocked_Q.pop();
            ready_Q.push(tempPCB, tempPCB.getCurrentQueue());
        }
    }
}

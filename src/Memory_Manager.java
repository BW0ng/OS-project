import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Brandon Wong
 * OS_Project
 */
public class Memory_Manager {
    private int currentMemory;
    private int maxMemory;
    private int memoryBuffer;

    private int totalNumJobs;

    private SYSTEM system;

    private PrintWriter writer;

    String[] statsStrings = {"Time (VTU's)", "Used (UM)", "Free (UM)",
                                "    # Job_Q", "  # Blocked_Q", "  # Ready_Q", "  Total Jobs"};


    public Memory_Manager(int maxMemory, SYSTEM system) {
        this.currentMemory = maxMemory;
        this.maxMemory = maxMemory;

        // Makes sure that there is at least 10% of memory
        this.memoryBuffer = (int) (maxMemory * 0.1);

        this.totalNumJobs = 0;

        this.system = system;

        if (!system.debug) {

            try {
                writer = new PrintWriter(new File("SYS_LOG"));
            } catch (IOException error) {
                if (system.debug) {
                    System.out.println("File not found");
                }
            }
        }

        initializeFile();
    }

    /**
     * Checks to see if there is enough memory available.
     * @param memoryNeeded
     * @return
     *          -1 if the amount of memory needed exceeds the max memory.
     *          0 if the amount of memory needed exceeds the current amount of memory.
     *          1 if the amount of memory needed will fit.
     */
    public int allocate(int memoryNeeded) {
        if ((memoryNeeded > maxMemory)) {
            return -1;
        } else if (memoryNeeded <= currentMemory) {
            currentMemory = currentMemory - memoryNeeded;
            totalNumJobs = totalNumJobs + 1;
            return 1;
        } else {
            return 0;
        }
    }

    public int checkAvailableMemory() {
        return currentMemory;
    }

    public int allocatedMemory() {
        return (maxMemory - currentMemory);
    }

    public int getMaxMemory() { return maxMemory; }

    public boolean hasAvailableSpace() {
        return (currentMemory >= memoryBuffer);
    }

    /**
     * This method is designed to deallocate the memory
     * and add it back to the memory available for use.
     * @param deallocatedMemory
     * Size of the memory that has been deallocated.
     */

    public void release (int deallocatedMemory) {
        currentMemory = currentMemory + deallocatedMemory;
    }

    public void setup() {
        // TODO implement setup
    }

    /**
     * This method is designed to print out the memory stats
     * every 200 VTU's (Virtual Time Units) to the file SYS_LOG
     *
     * Prints:
     *         number of allocated units of memory (UM)
     *         number of free units of memory
     *         number of jobs in the Job_Q
     *         number of jobs in the blocked_Q
     *         number of jobs in the ready_Q
     *         number of jobs delivered
     *
     */

    public void initializeFile() {
        try {

            File file = new File("SYS_LOG");

            if (file.exists()) {
                file.delete();
            }

            writer = new PrintWriter(new FileWriter("SYS_LOG", true));
            writer.println(" ----------------------------------------" +
                    "---------------------------------------------");
            writer.printf("| %s %s %s %s %s %s %s |%n", statsStrings[0], statsStrings[1],
                    statsStrings[2], statsStrings[3], statsStrings[4], statsStrings[5], statsStrings[6]);
            writer.println(" ----------------------------------------" +
                    "---------------------------------------------\n");
        } catch (IOException exception) {
            if (system.debug) {
                System.out.println("Couldn't find file: SYS_LOG");
            }
        }
    }

    public void printStats() {

        writer.printf("  %12.0f %9d %9d %11d %13d %11d %12d%n",
                system.clock.getClock(),
                checkAvailableMemory(), allocatedMemory(),
                system.job_Q.size(), system.scheduler.ready_Q.size(), system.scheduler.blocked_Q.size(),
                system.cpu.getNumJobsDispatched());
        writer.flush();

    }

    public void finish() {
        writer.close();
    }

}

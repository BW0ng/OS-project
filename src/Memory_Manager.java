import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Manages the memory by allocating and
 * deallocating memory for the various PCBs
 *
 * Outputs the system stats to SYS_LOG every
 * 200 VTU's (Virtual Time Units)
 *
 * Changed where the allocate method will return a
 * -1, 0, 1 because there might be the case where
 * a job's memory size exceeds the total memory
 */
public class Memory_Manager {
    private int currentMemory;      // Current amount of memory available
    private int maxMemory;          // Max amount of memory available
    private int memoryBuffer;       // Memory buffer to prevent overhead of search for a job

    private int totalNumJobs;       // Total number of PCB created

    private SYSTEM system;          // Global instance of system

    private PrintWriter writer;     // Local instance of the stream to output stats to print

    // Header strings used to output the stats to SYS_LOG
    String[] statsStrings = {"Time (VTU's)", "Used (UM)", "Free (UM)",
                                "    # Job_Q", "  # Blocked_Q", "  # Ready_Q", "  Total Jobs"};


    /**
     * Constructor used to create an instance of Memory Manger
     * @param maxMemory max amount of memory in the system
     * @param system Instance of the system
     */
    public Memory_Manager(int maxMemory, SYSTEM system) {
        this.currentMemory = maxMemory;
        this.maxMemory = maxMemory;

        // Makes sure that there is at least 10% of memory free
        this.memoryBuffer = (int) (maxMemory * 0.1);

        this.totalNumJobs = 0;

        this.system = system;

        initializeFile();
    }

    /**
     * Checks to see if there is enough memory available before allocating memory
     *
     * Used -1, 0, 1 in the case that there is a job that exceeds the max memory
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

    /**
     * Returns the amount of available memory
     * @return amount of available memory
     */
    public int checkAvailableMemory() {
        return currentMemory;
    }

    /**
     * Returns the amount of memory being used
     * @return amount of allocated memory
     */
    public int allocatedMemory() {
        return (maxMemory - currentMemory);
    }

    /**
     * Returns the max amount of memory in the system
     * @return
     */
    public int getMaxMemory() { return maxMemory; }

    /**
     * Returns true if there is more available space than the memory buffer.
     * @return boolean value of if there is more available space than the buffer
     */
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

    /**
     * Initializes a file, SYS_LOG, and creates the header for the file.
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
    public void printStats() {

        writer.printf("  %12.0f %9d %9d %11d %13d %11d %12d%n",
                system.clock.getClock(),
                checkAvailableMemory(), allocatedMemory(),
                system.job_Q.size(), system.scheduler.ready_Q.size(), system.scheduler.blocked_Q.size(),
                system.cpu.getNumJobsDispatched());
        writer.flush();

    }

    /**
     * Closes the File
     */
    public void finish() {
        writer.close();
    }

}

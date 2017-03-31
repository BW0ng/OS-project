import java.util.ArrayList;

/**
 * Class representing a Job.
 *
 * Used to compile all the data from the
 * input file to store in the Job_Q or to
 * create a PCB from.
 */
public class Job {

    private int ID;                         // ID of the job
    private int memorySize;                 // Memory size of the job
    private ArrayList<Integer> burstSize;   // List of the Burst sizes

    public Job(int ID, int memorySize, ArrayList<Integer> burstSizes) {
        this.ID = ID;
        this.memorySize = memorySize;
        this.burstSize = burstSizes;
    }


    /**
     * Returns the job ID.
     * @return the id of the job
     */
    public int getID() {

        return ID;
    }

    /**
     * Returns the memory size of the job
     * @return memorySize of the job
     */
    public int getMemorySize() {

        return memorySize;
    }

    /**
     * Returns an ArrayList of the burst sizes of the job
     * @return burstSize
     *
     */
    public ArrayList<Integer> getBursts() {

        return burstSize;
    }

    /**
     * ToString method to return a string version of the Job
     * @return string version of the job
     */
    public String toString() {

        return "ID: " + ID + ", Memory Size: " + memorySize +
                ", Burst Sizes: " + burstSize.toString();
    }
}

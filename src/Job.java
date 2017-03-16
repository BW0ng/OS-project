import java.util.ArrayList;

/**
 * Brandon Wong
 * OS_Project
 */
public class Job {

    private int ID;
    private int memorySize;
    private ArrayList<Integer> burstSize;
    private int currentBurst;

    private SYSTEM system;

    public Job(int ID, int memorySize, ArrayList<Integer> burstSizes, SYSTEM system) {
        this.ID = ID;
        this.memorySize = memorySize;
        this.burstSize = burstSizes;
        this.currentBurst = 0;
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

    public int getBurstSize() {
        return burstSize.get(currentBurst++);
    }

    public String toString() {

        return "ID: " + ID + ", Memory Size: " + memorySize +
                ", Burst Sizes: " + burstSize.toString();
    }
    public PCB toPCB() {
        return new PCB(ID, memorySize, burstSize, system);
    }

}

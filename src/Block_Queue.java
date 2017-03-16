/**
 * Brandon Wong
 * OS_Project
 */
import java.util.ArrayList;

public class Block_Queue {

    ArrayList<PCB> list = new ArrayList<PCB>();
    private SYSTEM system;

    /**
     * Adds the specified Job to the end of the list.
     * @param pcb Job to be appeneded to this list
     */
    public void push (PCB pcb) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIOCompletion() > pcb.getIOCompletion()) {
                list.add(i, pcb);
                return;
            }
        }
        list.add(pcb);
    }

    /**
     * Removes and returns the head of the list, or null if empty.
     * @return PCB at the head of the list.
     */
    public PCB pop () {
        if (list.size() <= 0) {
            return null;
        } else {
            return list.remove(0);
        }
    }

    public PCB peek() {
        return list.get(0);
    }

    /**
     * Finds and returns the PCBs that have finished I/O
     * If no job can be found, null is returned.
     * @param currentTime The current time to check against
     * @return Returns a job that fits the memory constraint.
     */

    public PCB findJob(double currentTime) {

        // Checks to see if there is elements
        // Then checks to see if any jobs are completed
        // PCB I/O completion <= clock Time then it is finished
        if (list.size() > 0 && list.get(0).getIOCompletion() <= currentTime) {
            return list.remove(0);
        } else {
            return null;
        }
    }

    /**
     * Returns the number of elements in this list.
     * @return the number of elements in this list
     */
    public int size() {
        return list.size();
    }

    public String toString() {
        String string = "";

        for ( int i = 0; i < list.size(); i++) {
            string += list.get(i).toString() + "\n";
        }
        return string;
    }

}

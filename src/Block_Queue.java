/**
 * Brandon Wong
 * OS_Project
 */
import java.util.ArrayList;

/**
 * Class that represents the Blocked_Q.
 * Contains various methods used to organize
 * the Blocked_Q and keep it sorted by IO
 * completion time.
 */
public class Block_Queue {

    /*
            Used an ArrayList due to the fast retrieval of Objects
            as well as when an Object is added at a specific index
            manual shifting does not need to be done as with an array.

            An array could be used to make it more memory efficient
            however many of the operations already built into an ArrayList
            would have to be implemented.
     */
    ArrayList<PCB> list = new ArrayList<PCB>();     // ArrayList of PCB
    private SYSTEM system;                          // Global variable of the instance of System

    /**
     * Adds the specified Job to the end of the list.
     * Sorted by the I/O completion time.
     *
     * Insertion is O(n) but retrieval is constant.
     * Keeping a sorted list allowed for the easy checking
     * of I/O completion.
     *
     * @param pcb PCB to be appended to this list
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

    /**
     * Returns the PCB at the head of the list
     * without removal
     * @return PCB at the head of the list
     */
    public PCB peek() {
        return list.get(0);
    }

    /**
     * Returns the number of elements in this list.
     * @return the number of elements in this list
     */
    public int size() {
        return list.size();
    }

    /**
     * Returns a string version of the Queue
     * Used for Debugging and print statements
     *
     * @return String version of the Queue
     */
    public String toString() {
        String string = "";

        for ( int i = 0; i < list.size(); i++) {
            string += list.get(i).toString() + "\n";
        }
        return string;
    }

}

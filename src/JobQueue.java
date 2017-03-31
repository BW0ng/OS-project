import java.util.ArrayList;

/**
 * Class that represents the JOB_Q.
 *
 * Contains various methods that keep
 * the queue sorted by memory so that
 * time isn't spent searching for the perfect job.
 */

public class JobQueue {
    /*
        Used an ArrayList due to the fast retrieval of Objects
        as well as when an Object is added at a specific index
        manual shifting does not need to be done as with an array.

         An array could be used to make it more memory efficient
         however many of the operations already built into an ArrayList
         would have to be implemented.
     */
    ArrayList<Job> list = new ArrayList<Job>();


    /**
     * Adds the specified Job to the sorted list.
     * List is sorted from least amount of memory to greater
     * @param job Job to be appended to this list
     */
    public void push (Job job) {

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMemorySize() > job.getMemorySize()) {
                list.add(i, job);
                return;
            }
        }
        list.add(job);
    }

    /**
     * Removes and returns the head of the list, or null if empty.
     * @return Job at the head of the list.
     */
    public Job pop () {
        if (list.size() <= 0) {
            return null;
        } else {
            return list.remove(0);
        }
    }

    /**
     * Returns the head of the list, without removal.
     * @return Job at the head of the list
     */
    public Job peek() {
        return list.get(0);
    }

    /**
     * Returns the number of elements in this list.
     * @return the number of elements in this list
     */
    public int size() {
        return list.size();
    }

}

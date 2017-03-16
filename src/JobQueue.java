import java.util.ArrayList;

/**
 * Brandon Wong
 * OS_Project
 *
 * Created another class to make pushing and popping
 * off the queue easier, as well as finding a job that
 * fits the memory requirements.
 */
public class JobQueue {

    ArrayList<Job> list = new ArrayList<Job>();


    /**
     * Adds the specified Job to the sorted list.
     * List is sorted from least amount of memory to greater
     * @param job Job to be appeneded to this list
     */
    public void push (Job job) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMemorySize() >= job.getMemorySize()) {
                list.add(i, job);
            }
        }
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

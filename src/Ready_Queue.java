import java.util.ArrayList;

/**
 * Class that represents the Ready_Q.
 *
 * Contains various methods that keep
 * the queue separated by subqueue.
 * Added methods that return the first
 * available PCB from the first non-empty
 * subqueue. And it contains the residency
 * rules
 */
public class Ready_Queue {

    private ArrayList<PCB> subqueue1;
    private ArrayList<PCB> subqueue2;
    private ArrayList<PCB> subqueue3;
    private ArrayList<PCB> subqueue4;

    private int subQueue1NumTurns;
    private int subQueue2NumTurns;
    private int subQueue3NumTurns;

    private int subQueue1Quantum;
    private int subQueue2Quantum;
    private int subQueue3Quantum;
    private int subQueue4Quantum;


    /**
     * Constructor used to create an instance of the Ready_Q
     * @param subQueue1NumTurns max number of turns of subqueue 1
     * @param subQueue2NumTurns max number of turns of subqueue 2
     * @param subQueue3NumTurns max number of turns of subqueue 3
     * @param subQueue1Quantum  time quantum of subqueue 1
     * @param subQueue2Quantum  time quantum of subqueue 2
     * @param subQueue3Quantum  time quantum of subqueue 3
     * @param subQueue4Quantum  time quantum of subqueue 4
     */
    public Ready_Queue(int subQueue1NumTurns, int subQueue2NumTurns,
                            int subQueue3NumTurns, int subQueue1Quantum,
                            int subQueue2Quantum, int subQueue3Quantum,
                            int subQueue4Quantum) {

        this.subqueue1 = new ArrayList<>();
        this.subqueue2 = new ArrayList<>();
        this.subqueue3 = new ArrayList<>();
        this.subqueue4 = new ArrayList<>();

        this.subQueue1NumTurns = subQueue1NumTurns;
        this.subQueue2NumTurns = subQueue2NumTurns;
        this.subQueue3NumTurns = subQueue3NumTurns;

        this.subQueue1Quantum = subQueue1Quantum;
        this.subQueue2Quantum = subQueue2Quantum;
        this.subQueue3Quantum = subQueue3Quantum;
        this.subQueue4Quantum = subQueue4Quantum;
    }

    /**
     * Return the first element of the first
     * non-empty subqueue.
     * @return the head of the first non-empty subqueue, else return null
     */
    public PCB pop() {
        if (subqueue1.size() > 0) {
            return subqueue1.remove(0);
        } else if (subqueue2.size() > 0) {
            return subqueue2.remove(0);
        } else if (subqueue3.size() > 0) {
            return subqueue3.remove(0);
        } else if (subqueue4.size() > 0) {
            return subqueue4.remove(0);
        } else {
            return null;
        }
    }

    /**
     * Add the PCB to the end of subqueue1
     * @param pcb PCB to be added
     */
    public void push(PCB pcb) {
        subqueue1.add(pcb);
    }

    /**
     * Add the PCB to the end of the inputted subqueue
     * @param pcb   PCB to be added
     * @param queue Subqueue to add the PCB to
     */
    public void push(PCB pcb, PCB.Queue queue) {
        switch (queue) {
            case subqueue1:
                subqueue1.add(pcb);
                break;
            case subqueue2:
                subqueue2.add(pcb);
                break;
            case subqueue3:
                subqueue3.add(pcb);
                break;
            case subqueue4:
                subqueue4.add(pcb);
                break;


        }
    }

    /**
     * Return the total size of the Ready_Q
     * @return total size of Ready_Q
     */
    public int size() {
        return subqueue1.size() + subqueue2.size() +
                subqueue3.size() + subqueue4.size();
    }

    /**
     * Return the time quantum of the requested subqueue
     * @param currentQueue  requested subqueue
     * @return time quantum of the requested subqueue
     */
    public int getQuantum(PCB.Queue currentQueue) {
        switch (currentQueue) {
            case subqueue1:
                return subQueue1Quantum;
            case subqueue2:
                return subQueue2Quantum;
            case subqueue3:
                return subQueue3Quantum;
            case subqueue4:
                return subQueue4Quantum;
            default:
                return 0;
        }
    }

    /**
     * Return the max number of turns that can be spent in the subqueue
     * @param currentQueue  requested subqueue
     * @return      max number of turns that can be spent in the subqueue
     */
    public int getNumTurns(PCB.Queue currentQueue) {
        switch (currentQueue) {
            case subqueue1:
                return subQueue1NumTurns;
            case subqueue2:
                return subQueue2NumTurns;
            case subqueue3:
                return subQueue3NumTurns;
            case subqueue4:
                return 999999999;
            default:
                return 0;
        }
    }

    /**
     * Remove the PCB from the subqueue
     * @param pcb   PCB to be removed
     */
    public void remove(PCB pcb) {
        switch (pcb.getCurrentQueue()) {
            case subqueue1:
                subqueue1.remove(pcb);
                break;
            case subqueue2:
                subqueue2.remove(pcb);
                break;
            case subqueue3:
                subqueue3.remove(pcb);
                break;
            case subqueue4:
                subqueue4.remove(pcb);
                break;
        }
    }

    /**
     * Returns string version of the Ready_Q
     * @return string version of the Ready_Q
     */
    public String toString() {
        String string = "\tReady Queue\n";

        if (subqueue1.size() > 0) {
            string = string + "\t\tSubqueue 1: \n";
        }
        for (int i = 0; i < subqueue1.size(); i++) {
            string = string + "\t\t\t" + subqueue1.get(i) + "\n";
        }

        if (subqueue2.size() > 0 ) {
            string = string + "\t\tSubqueue 2: \n";
        }
        for (int i = 0; i < subqueue2.size(); i++) {
            string = string + "\t\t\t" + subqueue2.get(i) + "\n";
        }

        if (subqueue3.size() > 0 ) {
            string = string + "\t\tSubqueue 3: \n";
        }
        for (int i = 0; i < subqueue3.size(); i++) {
            string = string + "\t\t\t" + subqueue3.get(i) + "\n";
        }

        if (subqueue4.size() > 0 ) {
            string = string + "\t\tSubqueue 4: \n";
        }
        for (int i = 0; i < subqueue4.size(); i++) {
            string = string + "\t\t\t" + subqueue4.get(i) + "\n";
        }

        return string;
    }

}

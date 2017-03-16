import java.util.ArrayList;

/**
 * Brandon Wong
 * OS_Project
 */
public class Ready_Queue {

    ArrayList<PCB> subqueue1;
    ArrayList<PCB> subqueue2;
    ArrayList<PCB> subqueue3;
    ArrayList<PCB> subqueue4;

    int subQueue1NumTurns;
    int subQueue2NumTurns;
    int subQueue3NumTurns;

    int subQueue1Quantum = 20;
    int subQueue2Quantum = 30;
    int subQueue3Quantum = 50;
    int subQueue4Quantum = 80;


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
     * @return PCB
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

    public void push(PCB pcb) {
        subqueue1.add(pcb);
    }

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

    public int size() {
        return subqueue1.size() + subqueue2.size() +
                subqueue3.size() + subqueue4.size();
    }

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

    public String toString() {
        String string = "Ready Queue\n";

        string = string + "\tSubqueue 1: \n";
        for (int i = 0; i < subqueue1.size(); i++) {
            string = string + "\t\t" + subqueue1.get(i) + "\n";
        }

        if (subqueue2.size() > 0 ) {
            string = string + "\n\tSubqueue 2: \n";
        }
        for (int i = 0; i < subqueue2.size(); i++) {
            string = string + "\t\t" + subqueue2.get(i) + "\n";
        }

        if (subqueue3.size() > 0 ) {
            string = string + "\n\tSubqueue 3: \n";
        }
        for (int i = 0; i < subqueue3.size(); i++) {
            string = string + "\t\t" + subqueue3.get(i) + "\n";
        }

        if (subqueue4.size() > 0 ) {
            string = string + "\n\tSubqueue 4: \n";
        }
        for (int i = 0; i < subqueue4.size(); i++) {
            string = string + "\t\t" + subqueue4.get(i) + "\n";
        }

        return string;
    }

}

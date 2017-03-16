/**
 * Brandon Wong
 * OS_Project
 */
public class CPU {

    public SYSTEM system;
    private int numJobsDispatched;

    public CPU(SYSTEM system) {
        this.system = system;
        numJobsDispatched = 0;
    }

    public void run(PCB pcb) {

        numJobsDispatched = numJobsDispatched + 1;

        int runTime = system.scheduler.ready_Q.getQuantum(pcb.currentQueue);


        if (runTime < pcb.getCurrentBurst()) {
            pcb.setIoState(PCB.IORequestTime.never);
        } else if (runTime == pcb.getCurrentBurst()) {
            pcb.setIoState(PCB.IORequestTime.atEnd);
        } else {
            pcb.setIoState(PCB.IORequestTime.beforeEnd);
            runTime = pcb.getCurrentBurst();
        }

        pcb.setRunTime(runTime);

        for (int i = 0; i < runTime; i++) {
            system.clock.tick();
        }
    }

    public void run(double duration) {
        for (int i = 0; i < (int) duration; i++) {
            system.clock.tick();
        }
    }

    public int getNumJobsDispatched() {
        return numJobsDispatched;
    }
}

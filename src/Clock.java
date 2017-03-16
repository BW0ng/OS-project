/**
 * Brandon Wong
 * OS_Project
 */
public class Clock {

    private double clock;

    private SYSTEM system;

    public Clock(SYSTEM system) {
        this.system = system;
        clock = 0;
    }

    public double getClock() {
        return clock;
    }

    public void tick() {

        if (clock % 200 == 0) {
            system.printStats();
        }

        clock = clock + 1;
    }
}

/**
 * Class that represents the Clock.
 *
 * Contains various methods and will
 * check if the clock is a multiple of
 * 200.
 */
public class Clock {

    private double clock;       // Counter to keep track of the current time

    private SYSTEM system;      // Global instance of system

    /**
     * Constructs an clock and taking an instance of the
     * current System as a parameter
     * @param system The instance of the system
     */
    public Clock(SYSTEM system) {
        this.system = system;
        clock = 0;
    }

    /**
     * Returns the current time on the clock
     * @return The current time on the clock
     */
    public double getClock() {
        return clock;
    }

    /**
     * Adds one to the clock
     *
     * Checks to see if the clock is a multiple of 200
     * If so then it calls printStats to print the current
     * stats to SYS_LOG
     */
    public void tick() {

        if (clock % 200 == 0) {
            system.printStats();
        }

        clock = clock + 1;
    }
}

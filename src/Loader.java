import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * Class that represents the loader
 *
 * Uses the information from the input
 * file to load jobs into the Ready_Q or
 * the Job_Q depending on how much current
 * memory there is.
 */
public class Loader {

    private String fileName;                // File name of the input file
    private long currentLineNum;            // Records the place of the loader in the file
    private SYSTEM system;                  // Global instance of the system

    protected boolean isFinished = false;   // Flag if the loader is done
    protected boolean endFile = false;      // Flag if the end of the file has been reached
    protected boolean noJobs = false;       // Flag is there is no current jobs from the file
    protected boolean noSpace = false;      // Flag if a job cannot be loaded from the Job_Q

    protected int sum = 0;                  // Sum detailing what the total bursts are

    /**
     * Constructor that creates an instance of the Loader
     * @param fileName  File name of the input file
     * @param system    Instance of the system
     */
    public Loader(String fileName, SYSTEM system) {
        this.fileName = fileName;
        this.system = system;
        this.currentLineNum = 0;

    }

    /**
     * Loads the next available job into the ready_Q
     * First checks to see if any jobs can be loaded from Job_Q
     * if not then it tries to load from the disk.
     */
    public void loadNextJob() {

        /*
            Checks to see:
                if the job can be loaded from Job_Q
                if there's enough memory
                if there are enough PCB's available
             before loading from the disk
        */
        if ((!checkJobQueue()
                && system.memoryManager.hasAvailableSpace() &&
                system.getNumPCB() < 15) && !endFile) {
            loadFromDisk();
        }

    }


    /**
     * Checks to see if a job can be loaded from the Job_Q
     * If one can be memory is allocated and a PCB is created
     * else if there is no room in memory then the no space flag is set
     * else it checks to see if the loader is finished
     * @return Boolean telling if a job was loaded from the Job_Q
     */
    public boolean checkJobQueue() {
        if ((system.job_Q.size() > 0) &&
                system.memoryManager.allocate(system.job_Q.peek().getMemorySize()) == 1) {
            system.scheduler.setup(system.job_Q.pop());
            return true;
        } else if ((system.job_Q.size() > 0) &&
                system.memoryManager.checkAvailableMemory() < system.job_Q.peek().getMemorySize()) {
            noSpace = true;
        } else {
            isFinished = endFile && (system.job_Q.size() == 0);
        }
        return false;
    }

    /**
     * Loads a Job from the input file based on the current line number
     * It will create a instance of a job based on data from the input file.
     *
     * If there is enough space in memory then a PCB is created and pushed on the
     * Ready_Q. Else the job instance is put on the Job_Q
     */
    public void loadFromDisk() {
        // Gets the line of the file based on currentLineNum
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            String line = lines.skip(currentLineNum).findFirst().get();

            // Splits the String into tokens based on whitespace
            String[] temp = line.trim().split("\\D+");

            // Create the Job object based on the inputted data
            Job job = new Job(Integer.parseInt(temp[0]),
                                Integer.parseInt(temp[1]),
                                convertBurstSizes(temp));
            currentLineNum++;


            if (job.getMemorySize() == 0) {
                // Sets the no jobs flag if there are no jobs available
                noJobs = true;
            } else if(system.memoryManager.allocate(job.getMemorySize()) == 1) {
                // Allocate the memory and create the PCB
                system.scheduler.setup(job);
            } else if (system.memoryManager.allocate(job.getMemorySize()) == 0) {
                // Push the job instance onto the Job_Q
                system.job_Q.push(job);
            }

        } catch (IOException error) {
            if (system.debug) {
                System.out.println("Loading error: " + error);
            }
        } catch (NoSuchElementException error) {
            if (system.debug) {
                System.out.println("Reached the end of the file");
            }
            endFile = true; // Sets the end of file flag to be true
        }
    }

    /**
     * Converts a string array of integers into an ArrayList
     * The bursts are also added up in sum.
     * @param array Array of bursts taken from the input file
     * @return  An ArrayList of the bursts
     */
    public ArrayList<Integer> convertBurstSizes(String[] array) {
        ArrayList<Integer> burstSizes = new ArrayList<>();


        for (int i = 2; i < array.length; i++) {
            burstSizes.add(Integer.parseInt(array[i]));
            sum = sum + Integer.parseInt(array[i]);
        }
        return burstSizes;
    }

    /**
     * Returns the variable isFinished
     *      true - it has reached the end of file and the Job_Q is empty
     * @return isFinished flag if the loader is done.
     */
    public boolean isFinished() {
        return isFinished;
    }


}

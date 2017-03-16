import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * Brandon Wong
 * OS_Project
 */
public class Loader {

    private String fileName;
    private long currentLineNum;
    private SYSTEM system;

    protected boolean isFinished = false;

    public Loader(String fileName, SYSTEM system) {
        this.fileName = fileName;
        this.system = system;
        this.currentLineNum = 0;

    }

    // TODO implement loader
    /*
        Check to see if less than 15 PCB's
        Check if enough memory

        Call Memory_Manager(allocate)
        if enough memory, scheduler(setup)
     */
    public void loadNextJob() {


        if (!checkJobQueue() && system.memoryManager.hasAvailableSpace() &&
                system.getNumPCB() < 15) {
            loadFromDisk();
        }

    }

    // TODO Check the Job Queue before loading
    public boolean checkJobQueue() {
        if ((system.job_Q.size() > 0) &&
                system.memoryManager.allocate(system.job_Q.peek().getMemorySize()) == 1) {
            system.scheduler.setup(system.job_Q.pop());
            return true;
        }
        return false;
    }

    public void loadFromDisk() {
        // Gets the line of the file based on currentLineNum
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            String line = lines.skip(currentLineNum).findFirst().get();

            // Splits the String into tokens based on whitespace
            String[] temp = line.trim().split("\\D+");

            // Create the Job object based on the inputted data
            Job job = new Job(Integer.parseInt(temp[0]),
                                Integer.parseInt(temp[1]),
                                convertBurstSizes(temp),
                                system);
            currentLineNum++;

            if(system.memoryManager.allocate(job.getMemorySize()) == 1) {
                system.scheduler.setup(job);
            } else if (system.memoryManager.allocate(job.getMemorySize()) == 0) {
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
            isFinished = true;
        }
    }

    public ArrayList<Integer> convertBurstSizes(String[] array) {
        ArrayList<Integer> burstSizes = new ArrayList<>();


        for (int i = 2; i < array.length; i++) {
            burstSizes.add(Integer.parseInt(array[i]));
        }
        return burstSizes;
    }

    public void printArray(String[] temp) {

        System.out.println("Printing Array");
        System.out.print("\t {");
        for (int i = 0; i < temp.length - 1; i++) {
            System.out.print(temp[i] + ", ");
        }
        System.out.println(temp[temp.length -1] + " }");
    }

    public boolean isFinished() {
        return isFinished;
    }


}

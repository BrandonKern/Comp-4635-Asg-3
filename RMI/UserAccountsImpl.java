import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.util.ArrayList;

public class UserAccountsImpl extends UnicastRemoteObject implements UserAccounts {
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Constructor for UserAccountsImpl class.
     * RemoteException if an error occurs during remote method invocation.
     */
    protected UserAccountsImpl() throws RemoteException {
        super();
    }

    @Override
    public Boolean checkUser(String user_id) throws RemoteException {
    lock.writeLock().lock(); // Acquire write lock to modify the file
    try {
        List<String> lines = new ArrayList<>(); // To store lines from the file
        boolean userExists = false;
        boolean userActive = false;

        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineParts = line.trim().split(" ");

                if (lineParts.length >= 3 && lineParts[0].equalsIgnoreCase(user_id)) {
                    userExists = true;
                    userActive = lineParts[2].equals("1"); // Check if user is active
                    if (userActive) {
                        return false; // User already active, cannot log in again
                    }
                    line = lineParts[0] + " " + lineParts[1] + " 1"; // Set active flag to 1
                }
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Error occurred while reading the file
        }

        if (!userExists) {
            lines.add(user_id + " 0 1"); // User doesn't exist, add with active flag 1
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt"))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Error occurred while writing to the file
        }

        return true; // User exists and updated successfully
    } finally {
        lock.writeLock().unlock(); // Release the write lock
    }
}

    public Boolean setUserInactive(String user_id) throws RemoteException {
    lock.writeLock().lock(); // Acquire write lock to modify the file
    try {
        List<String> lines = new ArrayList<>(); // To store lines from the file
        boolean userFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineParts = line.trim().split(" ");

                if (lineParts.length >= 3 && lineParts[0].equalsIgnoreCase(user_id)) {
                    line = lineParts[0] + " " + lineParts[1] + " 0"; // Set active flag to 0
                    userFound = true;
                }
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Error occurred while reading the file
        }

        if (!userFound) {
            return false; // User not found, unable to set inactive
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt"))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Error occurred while writing to the file
        }

        return true; // User set to inactive successfully
    } finally {
        lock.writeLock().unlock(); // Release the write lock
    }
}

    @Override
    public String checkUserScore(String user_id) throws RemoteException {
        lock.readLock().lock();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] lineParts = line.trim().split(" ");

                    if (lineParts[0].equalsIgnoreCase(user_id)) {
                        return user_id + "'s score is: " + lineParts[1];
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error occurred while checking the userId";
            }

            return user_id + " score not found";
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String updateUserScore(String user_id) throws RemoteException {

        lock.writeLock().lock();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
                StringBuilder fileContent = new StringBuilder();
                String line;

                boolean changed = false;
                // Iterate through each line in the "users.txt" file
                while ((line = br.readLine()) != null) {
                    String[] lineParts = line.trim().split(" ");

                    if (lineParts[0].equalsIgnoreCase(user_id)) {
                        changed = true;
                        int userScore = Integer.parseInt(lineParts[1]) + 1;
                        fileContent.append(user_id).append(" ").append(userScore).append(" 1");
                    } else {
                        fileContent.append(line); // Keep the existing line
                    }
                    fileContent.append(System.lineSeparator()); // Add newline character
                }

                // Update the file with the modified content
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt"))) {
                    bw.write(fileContent.toString());
                }
                if (changed) {
                    return user_id + " score updated";
                } else {
                    return user_id + " score not updated";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error occurred while updating score";
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}

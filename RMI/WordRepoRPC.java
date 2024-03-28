import java.net.Inet4Address;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 * WordRepoRPC class sets up a server for the WordRepo using Remote Method Invocation (RMI).
 */

public class WordRepoRPC {
    public static void main(String[] args) {
        try {
            final int serverPort = 1099;
            // Create and export the remote object
            WordRepo wordRepo = new WordRepoImpl();
            // WordRepo exportObject = (WordRepo) UnicastRemoteObject.exportObject(wordRepo, 0);

            // Start the RMI registry on port 1099
            try {
                LocateRegistry.getRegistry(serverPort).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(serverPort);
            }

            // Bind the remote object to the registry with the name "WordRepo"
            String URL = "rmi://" + InetAddress.getLocalHost().getHostAddress() + ":1099/WordRepo";
            Naming.rebind(URL, wordRepo);

            System.out.println("WordRepo Server is running.");
        } catch (Exception e) {
            System.err.println("WordRepo Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

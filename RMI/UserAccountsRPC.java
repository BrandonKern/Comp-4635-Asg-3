import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
/**
 * UserAccountsRPC class sets up a server for the UserAccounts using Remote Method Invocation (RMI).
 * It tries to connect, and then binds the remote object to the RMI registry and begins the server.
 */
public class UserAccountsRPC {
    private static final String HOST = "localhost";
    public static void main(String[] args) {
        try {
            final int serverPort = 1099;

            UserAccountsImpl server = new UserAccountsImpl();

            try {
                LocateRegistry.getRegistry(serverPort).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(serverPort);
            }
            String URL = "rmi://" + InetAddress.getLocalHost().getHostAddress() + ":1099/UserAccounts";
            Naming.rebind(URL, server);
            System.out.println("UserAccountServer is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 * ServerRPC class sets up a server using Remote Method Invocation (RMI).
 * It initializes a remote object representing the server, binds it to the RMI registry, and awaits client connections.
 */
public class ServerRPC {
    public  static void  main (String [] args) {
        try {
            final int serverPort = 1099;
            CrissCrossPuzzleServerImpl connection = new CrissCrossPuzzleServerImpl();
            try {
                LocateRegistry.getRegistry(serverPort).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(serverPort);
            }

            String serverURL = "rmi://" + InetAddress.getLocalHost().getHostAddress() + ":1099/CrissCrossPuzzleServer";
            Naming.rebind(serverURL,connection);
            System.out.println("waiting for the client");
        }catch (RemoteException | UnknownHostException | MalformedURLException e)
        {
            e.printStackTrace();
            System.out.println("Error while trying to connect to client object");

        }


    }
}

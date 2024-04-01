import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
// import java.rmi.Naming;
// import java.rmi.RemoteException;
// import java.rmi.registry.LocateRegistry;
// import java.rmi.server.UnicastRemoteObject;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ServerRPC class sets up a server using Remote Method Invocation (RMI).
 * It initializes a remote object representing the server, binds it to the RMI registry, and awaits client connections.
 */
public class ServerRPC {
    private static final int TIMELIMIT_SECONDS = 10;

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

            while (true) {
                TimeUnit.SECONDS.sleep(TIMELIMIT_SECONDS);
                System.out.println("Checking for disconnected clients...");
                Iterator<Map.Entry<Integer,ClientStateRecord>> it = connection.getEntrySet();
                if (it == null || it.hasNext() == false) {
                    System.out.println(" No clients!");
                    continue;
                }
                int cntAlive = 0, cntDead = 0;
                ArrayList<Integer> removeList = new ArrayList<>();
                while (it.hasNext()) {
                    Map.Entry<Integer, ClientStateRecord> pair = it.next();
                    int user_id = pair.getKey();
                    ClientStateRecord r = pair.getValue();
                    if (r.getIsActive()) {
                        r.setIsActive(false);
                        cntAlive++;
                    }		        
                    else {
                        removeList.add(user_id);
                        cntDead++;
                    }
                }
                for (int user_id:removeList)
		    	    connection.removeClientRecord(user_id);
	            System.out.println("Removed " + cntDead + ", " + cntAlive + " still alive!");
            }


        }catch (RemoteException | UnknownHostException | InterruptedException | MalformedURLException e)
        {
            e.printStackTrace();
            System.out.println("Error while trying to connect to client object");

        }


    }
}

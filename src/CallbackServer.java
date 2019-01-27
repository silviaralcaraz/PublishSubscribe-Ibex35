import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by silvia on 5/04/18.
 */
public class CallbackServer implements Serializable {
    public static void main(String args[]) {
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        String portNum = "2222", registryURL;
        try {
            System.out.println("Enter the RMIregistry port number:");
            portNum = (br.readLine()).trim();
            int RMIPortNum = Integer.parseInt(portNum);
            startRegistry(RMIPortNum);
            CallbackServerImpl exportedObj = new CallbackServerImpl();
            registryURL = "rmi://localhost:" + portNum + "/callback";
            Naming.rebind(registryURL, exportedObj);
            System.out.println("Callback Server ready.\n");
            while(true) {
                exportedObj.getWebInfo(); // obtengo la info del ibex35
                exportedObj.notifyAlerts(); // compruebo y notifico las alarmas
                Thread.sleep(1000 * 60); // espero 1 min
            }
        } catch (Exception re) {
            System.out.println("Exception in CallbackServer: " + re);
        }
    }

    public static void startRegistry(int RMIPortNum) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list();
        } catch (RemoteException e) {
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
        }
    }
}
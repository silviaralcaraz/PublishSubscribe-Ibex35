import java.io.*;
import java.rmi.*;

import static java.lang.System.exit;

public class CallbackClient {
    public static void main(String args[]) {
        try {
            int RMIPort = 2222;
            String hostName;
            InputStreamReader is = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(is);
            System.out.println("Enter the RMIRegistry host namer:");
            hostName = br.readLine();
            System.out.println("Enter the RMIregistry port number:");
            String portNum = br.readLine();
            RMIPort = Integer.parseInt(portNum);
            String registryURL = "rmi://localhost:" + portNum + "/callback";
            // find the remote object and cast it to an interface object
            CallbackServerInterface h = (CallbackServerInterface) Naming.lookup(registryURL);
            System.out.println("Lookup completed ");
            System.out.println("Server said " + h.sayHello());
            CallbackClientInterface callbackObj = new CallbackClientImpl();
            // register for callback
            h.registerForCallback(callbackObj);
            System.out.println("Registered for callback.");

            String option = "";
            while (option != "d") {
                System.out.println("\nWhat do you want to do?\na) Registry alarms\nb) Unregistry alarms\nc)Show your alarms\nd)Exit");
                option = br.readLine();
                switch (option) {
                    case "a":
                        System.out.println("-- REGISTRY ALARMS --");
                        System.out.println("Company name: (in uppercase)");
                        String name = br.readLine();
                        System.out.println("Alarm value: ");
                        Float value = Float.parseFloat(br.readLine());
                        System.out.println("Alarm type: (buy/sell)");
                        String type = br.readLine();
                        Alert alert = new Alert(type, value, callbackObj, name);
                        h.alertRegistry(alert); //registro la alerta
                        break;
                    case "b":
                        System.out.println("-- UNREGISTRY ALARMS --");
                        System.out.println("Company name (in uppercase): ");
                        String name2 = br.readLine();
                        System.out.println("Alarm value: ");
                        Float value2 = Float.parseFloat(br.readLine());
                        System.out.println("Alarm type: (buy/sell)");
                        String type2 = br.readLine();
                        Alert alert2 = new Alert(type2, value2, callbackObj, name2);
                        h.alertUnregistry(alert2); // elimino la alerta
                        break;
                    case "c":
                        h.showAlerts(callbackObj);  // muestro las alertas realizadas por el cliente
                        break;
                    case "d":
                        h.cleanAlerts(callbackObj); // elimino todas las alertas del cliente
                        h.unregisterForCallback(callbackObj); // elimino el registro al cliente del servidor
                        System.out.println("You exit succesfully.");
                        exit(0);
                        break;
                    default:
                        System.out.println("Invalid option. Try again.");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println(
                    "Exception in CallbackClient: " + e);
        }
    }
}
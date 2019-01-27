import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class CallbackServerImpl extends UnicastRemoteObject implements CallbackServerInterface, Serializable {
    private Vector clientList;
    private HashMap<String, Float> companies;
    private ArrayList<Alert> alerts;

    public CallbackServerImpl() throws RemoteException {
        super();
        clientList = new Vector();
        companies = new HashMap<>();
        alerts = new ArrayList<>();
    }

    /*Metodo que se encarga de obtener y almacenar los valores del IBEX35*/
    public synchronized void getWebInfo() throws RemoteException {
        try {
            /* Si la web esta caida descomentar las dos primeras lineas y comentar las dos siguientes para leer de fichero: */
            //File file = new File("page.html");
            //Document doc = Jsoup.parse(file, "UTF-8", "http://www.bolsademadrid.es/esp/aspx/Mercados/Precios.aspx?indice=ESI100000000&punto=indice");
            URL url = new URL("http://www.bolsademadrid.es/esp/aspx/Mercados/Precios.aspx?indice=ESI100000000&punto=indice");
            Document doc = Jsoup.parse(url, 3000);
            Elements tables = doc.select("table [id*=ctl00_Contenido_tblAcciones]");
            Elements rows = tables.select("tr");
            for (int i = 1; i < rows.size(); i++) {
                Elements columns = rows.get(i).select("td");
                String name = columns.get(0).text();
                Float value = Float.parseFloat(columns.get(1).text().replace(",", "."));
                if (!companies.containsKey(name)) { // Almaceno los valores de las empresas
                    companies.put(name, value);
                } else {
                    companies.replace(name, value);
                }
            }
            System.out.println("Updated data.");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (HttpStatusException e) {
            System.out.println("\nERROR: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void alertRegistry(Alert alert) throws java.rmi.RemoteException {
        if (!alertExist(alert) && checkAlert(alert)) {
            alerts.add(alert);
            alert.getClient().notifyMe("Alert done.");
        } else {
            alert.getClient().notifyMe("Invalid alert.");
        }
    }

    @Override
    public synchronized void alertUnregistry(Alert alert) throws java.rmi.RemoteException {
        ArrayList<Alert> alertsToRemove = new ArrayList<Alert>();
        if (alertExist(alert)) {
            for (Alert a : alerts) {
                if (a.getClient().equals(alert.getClient()) && a.getType().equals(alert.getType()) && a.getCompany().equals(alert.getCompany()) && a.getValue() == alert.getValue()) {
                    alertsToRemove.add(a);
                }
            }
            alerts.removeAll(alertsToRemove);
        } else {
            alert.getClient().notifyMe("Invalid alert");
        }
    }

    /*Metodo para comprobar si todos los campos de la alarma del cliente son validos*/
    public Boolean checkAlert(Alert alert) {
        Boolean approved = false;
        if (companies.containsKey(alert.getCompany().trim()) && alert.getValue() > 0) {
            if (alert.getType().trim().equals("sell") || alert.getType().trim().equals("buy")) {
                approved = true;
            }
        }
        return approved;
    }

    public Boolean alertExist(Alert alert) {
        Boolean exist = false;
        for (Alert a : alerts) {
            if (a.getClient().equals(alert.getClient()) && a.getType().equals(alert.getType()) && a.getCompany().equals(alert.getCompany()) && a.getValue() == alert.getValue()) {
                exist = true;
            }
        }
        return exist;
    }

    /*Metodo que comprueba si se cumple alguna alerta, notificandolo y eliminandola*/
    public synchronized void notifyAlerts() throws RemoteException {
        ArrayList<Alert> alertsToRemove = new ArrayList<Alert>();
        System.out.println("Checking alerts...");
        for (Alert alert : alerts) {
            if (alert.getType().trim().equals("buy")) { // reviso las alertas de compra
                if (alert.getValue() >= companies.get(alert.getCompany())) {
                    alert.getClient().notifyMe("BUY ALERT ACCOMPLISHED -> Company: " + alert.getCompany() + " Value: " + alert.getValue());
                    alertsToRemove.add(alert);
                }
            } else if (alert.getType().trim().equals("sell")) {
                if (alert.getValue() <= companies.get(alert.getCompany())) {
                    alert.getClient().notifyMe("SELL ALERT ACCOMPLISHED -> Company: " + alert.getCompany() + " Value: " + alert.getValue());
                    alertsToRemove.add(alert);
                }
            }
        }
        alerts.removeAll(alertsToRemove);
    }

    /*Metodo para mostrar al cliente que alarmas ha registrado*/
    public synchronized void showAlerts(CallbackClientInterface remoteClientObject) throws RemoteException {
        String alarmsInfo = "\nYOUR ALARMS: \n";
        for (Alert alert : alerts) {
            if (alert.getClient().equals(remoteClientObject)) {
                alarmsInfo = alarmsInfo + "- " + alert.getCompany() + ", " + alert.getType() + ", " + alert.getValue() + "\n";
            }
        }
        remoteClientObject.notifyMe(alarmsInfo);
    }

    /*Metodo para eliminar todas las alertas de un cliente una vez se desconecta*/
    public synchronized void cleanAlerts(CallbackClientInterface remoteClientObject) throws java.rmi.RemoteException {
        ArrayList clientAlerts = new ArrayList<Alert>();
        for (Alert alert : alerts) {
            if (alert.getClient().equals(remoteClientObject)) {
                clientAlerts.add(alert);
            }
        }
        alerts.removeAll(clientAlerts);
        remoteClientObject.notifyMe("All your alerts were deleted.");
    }

    //--------------------------------------------------------------------------------------
    public String sayHello() throws java.rmi.RemoteException {
        return ("hello");
    }

    public synchronized void registerForCallback(CallbackClientInterface callbackClientObject) throws java.rmi.RemoteException {
        // store the callback object into the vector
        if (!(clientList.contains(callbackClientObject))) {
            clientList.addElement(callbackClientObject);
            System.out.println("Registered new client ");
            doCallbacks();
        }
    }

    public synchronized void unregisterForCallback(CallbackClientInterface callbackClientObject) throws java.rmi.RemoteException {
        if (clientList.removeElement(callbackClientObject)) {
            System.out.println("Unregistered client ");
        } else {
            System.out.println("unregister: client wasn't registered.");
        }
    }

    private synchronized void doCallbacks() throws java.rmi.RemoteException {
        // make callback to each registered client
        System.out.println("**************************************\nCallbacks initiated ---");
        for (int i = 0; i < clientList.size(); i++) {
            System.out.println("doing " + (i + 1) + "-th callback\n");
            // convert the vector object to a callback object
            CallbackClientInterface nextClient = (CallbackClientInterface) clientList.elementAt(i);
            // invoke the callback method
            nextClient.notifyMe("Number of registered clients=" + clientList.size());
        }
        System.out.println("********************************\nServer completed callbacks ---");
    }
}
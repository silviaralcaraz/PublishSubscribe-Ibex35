import java.rmi.*;

public interface CallbackServerInterface extends Remote {
    public void alertRegistry(Alert alert) throws java.rmi.RemoteException;
    public void alertUnregistry(Alert alert) throws java.rmi.RemoteException;
    public void showAlerts(CallbackClientInterface remoteClientObject) throws  java.rmi.RemoteException;
    public void cleanAlerts(CallbackClientInterface remoteClientObject) throws java.rmi.RemoteException;
    //-------------------------------------------------------------------------
    public String sayHello( ) throws java.rmi.RemoteException;
    public void registerForCallback(CallbackClientInterface callbackClientObject) throws java.rmi.RemoteException;
    public void unregisterForCallback(CallbackClientInterface callbackClientObject) throws java.rmi.RemoteException;
}
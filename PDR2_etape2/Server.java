//import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;



public class Server extends UnicastRemoteObject implements Server_itf{

    public static final String URL = "//localhost:4080/Server";
    public static final Integer port = 4080;
    private Map<String, Integer> identifiantsObj;
    private Map<Integer, ServerObject> servObj;

    public Server() throws RemoteException{
        super();
        identifiantsObj = new HashMap<String, Integer>();
        servObj = new HashMap<Integer, ServerObject>();
    }

    @Override
    public int lookup(String name) throws RemoteException {
        if (identifiantsObj.get(name) == null) {
            return -1;
        } else {
            return identifiantsObj.get(name);
        }
    }

    @Override
    public void register(String name, int id) throws RemoteException {
        identifiantsObj.put(name, id);
    }

    @Override
    public int create(Object o) throws RemoteException {
        int id = servObj.size();
        ServerObject sobj = new ServerObject(o,id);
        servObj.put(id, sobj);
        return id;
    }

    @Override
    public Object lock_read(int id, Client_itf client) throws RemoteException {
		ServerObject sro = servObj.get(id);
		return sro.lock_read(client);
    }

    @Override
    public Object lock_write(int id, Client_itf client) throws RemoteException {
        ServerObject sro = servObj.get(id);
        return sro.lock_write(client);
    }

    @Override
    public void unlock(int id, Object obj, Client_itf client) throws RemoteException {
       ServerObject sro = servObj.get(id);
       sro.unlock(client, obj);
    }

    @Override
    public void subscribe(int id, Client_itf client) throws RemoteException {
        ServerObject sro = servObj.get(id);
        sro.subscribe(client);
    }

    @Override
    public void unsubscribe(int id, Client_itf client) throws RemoteException {
        ServerObject sro = servObj.get(id);
        sro.unsubscribe(client);
    }

    @Override
    public void track(int id, Client_itf client) throws RemoteException {
        ServerObject sro = servObj.get(id);
        sro.track(client);
    }

    @Override
    public void leave_track(int id, Client_itf client) throws RemoteException {
        ServerObject sro = servObj.get(id);
        sro.leave_track(client);
    }

    
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            Server serveur = new Server();;
            Naming.rebind(Server.URL, serveur);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}

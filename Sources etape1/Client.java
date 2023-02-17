import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {

	private static Map<Integer, SharedObject> objetsPartages;

	public Client() throws RemoteException {
		super();
	}


///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
		Client.objetsPartages = new HashMap<Integer, SharedObject>();
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {
		try {
			Server_itf serv = (Server_itf) Naming.lookup(Server.URL);
			int id = serv.lookup(name);

			SharedObject so = objetsPartages.get(id);
			if (id != -1 && so == null) {
				so = new SharedObject(null, id);
				objetsPartages.put(id, so);
			}
			return so;
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
			return null;
		}
	}		
	
	// binding in the name server
	public static void register(String name, SharedObject_itf so) {
		SharedObject s = (SharedObject) so;
		try {
			Server_itf serv = (Server_itf) Naming.lookup(Server.URL);
			serv.register(name, s.id);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
			throw new ServiceConfigurationError("Echec de l'enregistrement de l'objet");
		}
		
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		try {
			Server_itf serv = (Server_itf) Naming.lookup(Server.URL);
			int id = serv.create(o);
			SharedObject so = new SharedObject(o, id);
			objetsPartages.put(id, so);
			return so;
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
			throw new ServiceConfigurationError("Echec de la création de l'objet");
		}
		
		
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id) {
		try {
			Server_itf serv = (Server_itf) Naming.lookup(Server.URL);
			return serv.lock_read(id, new Client());	
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
			throw new ServiceConfigurationError("Echec lock_read Client");
		}
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
		try {
			Server_itf serv = (Server_itf) Naming.lookup(Server.URL);	
			return serv.lock_write(id, new Client());
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
			throw new ServiceConfigurationError("Echec lock_write Client");
		}
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		SharedObject so = objetsPartages.get(id);
		return so.reduce_lock();
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
		SharedObject so = objetsPartages.get(id);
		so.invalidate_reader();	
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		SharedObject so = objetsPartages.get(id);
		return so.invalidate_writer();
	}
}

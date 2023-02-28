public interface Server_itf extends java.rmi.Remote {
	public int lookup(String name) throws java.rmi.RemoteException;
	public void register(String name, int id) throws java.rmi.RemoteException;
	public int create(Object o) throws java.rmi.RemoteException;
	public Object lock_read(int id, Client_itf client) throws java.rmi.RemoteException;
	public Object lock_write(int id, Client_itf client) throws java.rmi.RemoteException;
	public void unlock(int id, Object obj, Client_itf client) throws java.rmi.RemoteException;
	public void subscribe(int id, Client_itf client) throws java.rmi.RemoteException;
	public void unsubscribe(int id, Client_itf client) throws java.rmi.RemoteException;
	public void track(int id, Client_itf client) throws java.rmi.RemoteException;
	public void leave_track(int id, Client_itf client) throws java.rmi.RemoteException;
}

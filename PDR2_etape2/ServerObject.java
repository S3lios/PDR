import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ServerObject implements Remote {

    public Object obj;
	public int id;
    enum VERROU {
        NL,
        RL,
        WL
    }
	
	public VERROU lock;	// NL : no local lock
						// RL : read lock
						// WL : write lock

    private Client_itf writer;
    private List<Client_itf> readers;
    private List<Client_itf> subscribers;
    private List<Client_itf> tracers;

    // constructor
	public ServerObject(Object o, int ident){
		this.obj = o;
		this.id = ident;
        this.lock = VERROU.NL;
        this.writer = null;
        this.readers = new ArrayList<>();
        this.subscribers = new ArrayList<>();
        this.tracers = new ArrayList<>();

        Thread debug = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //System.out.println("id: " + id + " lock: " + lock + " writer: " + writer + " readers: " + readers);
                }
            }
        });
        debug.start();
	}

    
    public Object lock_read(Client_itf c) {
        switch (lock) {
			case NL:
				lock = VERROU.RL;
				break;
            case RL:
                break;
			case WL:
                try {
                    this.obj = writer.reduce_lock(id);
                    readers.add(writer);
                    writer = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
				lock = VERROU.RL;
				break;
			default:
				break;
		}
        readers.add(c);
        return this.obj;
    }

   
    public Object lock_write(Client_itf c) {
        try {
            switch (lock) {
                case NL:
                    lock = VERROU.WL;
                    writer = c;
                    break;
                case RL:
                    writer = c;
                    lock = VERROU.WL;
                    for (Client_itf i : readers) {
                        i.invalidate_reader(id);
                    }
                    readers.clear();  
                    break;
                case WL:
                    this.obj = writer.invalidate_writer(id);
                    writer = c;
                    break;
                default:
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return this.obj;
    }

    public void unlock(Client_itf client, Object o) {
        // Enlever le client du write et l'ajouter au read et repasser en read
        if (client.equals(writer)) {
            writer = null;
            readers.add(client);
            lock = VERROU.RL;
        }

        // Mise a jour de l'objet
        this.obj = o;

        for (Client_itf subscriber : subscribers) {
            if (subscriber.equals(client)) {
                continue;
            }
            // Rappel dans un thread pour ne pas bloquer le serveur
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        subscriber.callBack(id);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }

        for (Client_itf tracer : tracers) {
            if (tracer.equals(client)) {
                continue;
            }
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        tracer.trace(id, o);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }

    public void subscribe(Client_itf client) {
        if (!subscribers.contains(client)) {
            subscribers.add(client);
        }
    }

    public void unsubscribe(Client_itf client) {
        subscribers.remove(client);
    }

    public void track(Client_itf client) {
        if (!tracers.contains(client)) {
            tracers.add(client);
        }
    }

    public void leave_track(Client_itf client) {
        readers.add(client);
        tracers.remove(client);
    }
}

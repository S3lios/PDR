import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {

	public Object obj;
	public int id;

	private CallBack callBack;

	enum VERROU {
		NL,
		RLC,
		WLC,
		RLT,
		WLT,
		RLT_WLC
	}
	
	public VERROU lock;	// NL : no local lock
						// RLC : read lock cached (not taken)
						// WLC : write lock cached
						// RLT : read lock taken
						// WLT : write lock taken
						// RLT_WLC : read lock taken and write lock cached

	// constructor
	public SharedObject(Object o, int ident){
		this.obj = o;
		this.id = ident;
		this.lock = VERROU.NL;
	}
	
	// invoked by the user program on the client node
	public void lock_read() {
		switch (lock) {
			case NL:
				this.obj = Client.lock_read(id);
				lock = VERROU.RLT;
				break;
			case RLC:
				lock = VERROU.RLT;
				break;
			case WLC:
				lock = VERROU.RLT_WLC;
				break;
			default:
				break;
		}
	}

	// invoked by the user program on the client node
	public void lock_write() {
		switch (lock) {
			case NL:
				this.obj = Client.lock_write(id);
				lock = VERROU.WLT;
				break;
			case RLC:
				this.obj = Client.lock_write(id);
				lock = VERROU.WLT;
				break;
			case WLC:
				lock = VERROU.WLT;
				break;
			default:
				break;
		}
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		switch (lock) {
			case RLT:
				lock = VERROU.RLC;
				break;
			case WLT:
				lock = VERROU.WLC;
				Client.unlock(this.id);
				break;
			case RLT_WLC:
				lock = VERROU.WLC;
				break;
			default:
				break;
		}
		this.notify();
	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		try {
			switch (lock) {
				case WLT:
					this.wait();
					lock = VERROU.RLC;
					break;
				case RLT_WLC:
					lock = VERROU.RLT;
					break;
				case WLC:
					lock = VERROU.RLC;
					break;
				default:
					break;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return this.obj;
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
		switch (lock) {
			case RLT:
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lock = VERROU.NL;
				break;
			case RLC:
				lock = VERROU.NL;
				break;
			default:
				break;
		}
	}

	public synchronized Object invalidate_writer() {
		// synchronized(this) { CODE A SYNCHRO }
		try {
		switch (lock) {
			case WLT:
				this.wait();
				lock = VERROU.NL;
				break;
			case WLC:
				lock = VERROU.NL;
				break;
			case RLT_WLC:
				this.wait();
				lock = VERROU.NL;
				break;
			default:
				break;
		}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return this.obj;
	}

	public void callBack() {
		if (callBack != null) {
			callBack.call();
		}
	}

	public void subscribe(CallBack callBack) {
		this.callBack = callBack;
		Client.subscribe(id);
	}

	// TODO - Ajout dans client, server et servObj
	public void unsuscribe() {
		this.callBack = null;
		// Client.unsuscribe(id);
	}
}

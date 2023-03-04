import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {

	public Object obj;
	public int id;

	private CallBack callBack;

	private boolean isTracked   = false;
	private boolean isSubscribed = false;


	enum VERROU {
		NL,
		RLC,
		RLT,
		WLT,
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

		Thread debug = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("id: " + id + " lock: " + lock);
                }
            }
        });
		debug.start();
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
				lock = VERROU.RLC;
				Client.unlock(this.id, this.obj);
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
		System.out.println("get invalidate_reader");
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

	// callback invoked remotely by the server
	public synchronized Object invalidate_writer() { // synchronized(this) { CODE A SYNCHRO }
		try {
		switch (lock) {
			case WLT:
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

	// callback invoked when the object is modified on the server
	public void callBack() {
		if (callBack != null) {
			callBack.call();
		}
	}


	// subscribe to the object
	public void subscribe() {
		if (!isSubscribed) { // Subscribe only if there is a callback
			leave_track();
			Client.subscribe(id);
		}
		this.isSubscribed = true;
	}

	// unsubscribe to the object
	public void unsuscribe() {
		if (isSubscribed) {
			Client.unsubscribe(id);
		}
		this.isSubscribed = false;
	}

	// return true if the object is subscribe
	public boolean isSubscribed() {
		return this.isSubscribed;
	}

	// track the object
	public void track() {
		if (!isTracked) {
			unsuscribe();
			Client.track(id);
		}
		this.isTracked = true;
	}

	// leave the track of the object
	public void leave_track() {
		if (isTracked) {
			Client.leave_track(id);
		}
		this.isTracked = false;
	}

	// return true if the object is tracked
	public boolean isTracked() {
		return this.isTracked;
	}

	// set the callback
	public void setCallBack(CallBack callBack) {
		this.callBack = callBack;
	}

	// remove the callback
	public void removeCallback() {
		this.callBack = null;
	}


	public void trace(Object obj) {
		// Passage en RLC
		switch (lock) {
			case NL:
				lock = VERROU.RLC;
				this.obj = obj;
				break;
			case RLC:
			    this.obj = obj;
				break; 
			default:
				break;
		}
		
		this.callBack();
	}
}

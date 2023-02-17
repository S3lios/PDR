public class TestIncrementeur {
	public static void main(String[] args) {
		
		int nbThreads = Integer.parseInt(args[0]);
		Thread[] threads = new Thread[nbThreads];
		
		Client.init();
		SharedObject nbContainer = (SharedObject) Client.lookup("nb");

		if (nbContainer == null) {
			nbContainer = (SharedObject) Client.create(new Compteur());
			Client.register("nb", nbContainer);
		}
		
		nbContainer.lock_write();
		((Compteur)(nbContainer.obj)).put(0);
		nbContainer.unlock();
		
		for(int i = 0 ; i < nbThreads ; i++) {
			Thread clientThread = new Thread(new Incrementeur());
			threads[i] = clientThread;
		}

		for(int i = 0 ; i < nbThreads ; i++) {
			threads[i].start();
		}
		
		for(int i = 0 ; i < nbThreads ; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		nbContainer.lock_read();
		System.out.println("Valeur lue : " + ((Compteur)(nbContainer.obj)).get() + ", valeur attendue : " + nbThreads);
		nbContainer.unlock();
		System.exit(0);
	}
}
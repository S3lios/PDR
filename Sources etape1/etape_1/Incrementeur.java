public class Incrementeur implements Runnable {

	@Override
	public void run() {
		SharedObject nbContainer = (SharedObject) Client.lookup("nb");
		if (nbContainer == null) {
			System.out.println("nb was not found");
		}
		nbContainer.lock_write();
		((Compteur)(nbContainer.obj)).put(((Compteur)(nbContainer.obj)).get()+1);
		nbContainer.unlock();
		System.exit(0);
	}

}
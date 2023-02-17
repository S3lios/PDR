public class Incrementeur implements Runnable {

	@Override
	public void run() {
		Compteur_itf nbContainer = (Compteur_itf) Client.lookup("nb");
		if (nbContainer == null) {
			System.out.println("nb was not found");
		}
		nbContainer.lock_write();
		nbContainer.put(nbContainer.get()+1);
		nbContainer.unlock();
		System.exit(0);
	}

}
public class Compteur_stub extends SharedObject implements Compteur_itf , java.io.Serializable { 

	public Compteur_stub(Object o, int ident) {
		super(o, ident);} 

	public int get() {
		Compteur objet = (Compteur) obj;
		return objet.get();
	}
	public void put(int arg0) {
		Compteur objet = (Compteur) obj;
		objet.put(arg0);
	}
}
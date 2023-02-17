public class Compteur implements java.io.Serializable {
	private int value;
	public Compteur() {
		this.value = 0;
	}
	public int get() {
//		System.out.println("value read : " + this.value);
		return this.value;
	}

	public void put(int newValue) {
//		System.out.println("value wrote : " + newValue + " previous : " + this.value);
		this.value = newValue;
	}
}

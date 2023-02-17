public class Sentence_stub extends SharedObject implements Sentence_itf , java.io.Serializable { 

	public Sentence_stub(Object o, int ident) {
		super(o, ident);} 

	public void write(String arg0) {
		super.lock_write();
		Sentence objet = (Sentence) obj;
		objet.write(arg0);
		super.unlock();
	}
	public String read() {
		super.lock_read();
		Sentence objet = (Sentence) obj;
		String ret = objet.read();
		super.unlock();
		return ret;
	}
}
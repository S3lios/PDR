public class Sentence_stub extends SharedObject implements Sentence_itf , java.io.Serializable { 

	public Sentence_stub(Object o, int ident) {
		super(o, ident);} 

	public void write(java.lang.String arg0) {
		Sentence objet = (Sentence) obj;
		objet.write(arg0);
	}
	public java.lang.String read() {
		Sentence objet = (Sentence) obj;
		return objet.read();
	}
}
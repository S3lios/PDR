public class Sentence implements java.io.Serializable {
	String 		data;
	public Sentence() {
		data = new String("");
	}
	
	@Write
	public void write(String text) {
		data = text;
	}
	@Read
	public String read() {
		return data;	
	}
	
}
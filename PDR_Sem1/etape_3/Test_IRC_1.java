public class Test_IRC_1 implements Runnable{
    Sentence_itf  sentence;

    public void ecrire(String s) {
        sentence.lock_write();
        sentence.write("Justin "+s+"\n");
        sentence.unlock();
    }

    public String lire() {
        sentence.lock_read();
        String a = sentence.read();
        sentence.unlock();
        return a;
    }

    public void run() {
        Client.init();

        Sentence_itf s = (Sentence_itf)Client.lookup("Commande");
        if (s == null){
            s = (Sentence_itf)Client.create(new Sentence());
            Client.register("Commande", s);
        }
        sentence = s;
        try {
            Thread.sleep(1000);
            System.out.println(lire());
            ecrire("Justin");
            Thread.sleep(1000);
            System.out.println(lire());
            ecrire("Kebab sauce algerienne");
            Thread.sleep(1000);
            System.out.println(lire());
            ecrire("avec boisson");
            Thread.sleep(1000);
            System.out.println(lire());
            ecrire("merci");
            Thread.sleep(1000);
            System.out.println(lire());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}

public class Test_IRC_2 implements Runnable{

    Sentence_itf  sentence;

    public void ecrire(String s) {
        sentence.lock_write();
        sentence.write("Kebabier "+s+"\n");
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
            ecrire("Kebabier");
            Thread.sleep(1000);
            System.out.println(lire());
            ecrire("bonjour chef");
            Thread.sleep(1000);
            System.out.println(lire());
            ecrire("boisson ?");
            Thread.sleep(1000);
            System.out.println(lire());
            ecrire("bonne journée chef");
            Thread.sleep(1000);
            System.out.println(lire());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    
    
}

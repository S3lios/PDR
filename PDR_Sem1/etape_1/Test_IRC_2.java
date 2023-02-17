public class Test_IRC_2 implements Runnable{

    SharedObject  sentence;

    public void ecrire(String s) {
        sentence.lock_write();
        ((Sentence)(sentence.obj)).write("Kebabier "+s+"\n");
        sentence.unlock();
    }

    public String lire() {
        sentence.lock_read();
        String a = ((Sentence)(sentence.obj)).read();
        sentence.unlock();
        return a;
    }

    public void run() {
        Client.init();

        SharedObject s = (SharedObject)Client.lookup("Commande");
        if (s == null){
            s = (SharedObject)Client.create(new Sentence());
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

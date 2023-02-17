public class Test_IRC_1 implements Runnable{
    SharedObject sentence;

    public void ecrire(String s) {
        sentence.lock_write();
        ((Sentence)(sentence.obj)).write("Justin "+s+"\n");
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

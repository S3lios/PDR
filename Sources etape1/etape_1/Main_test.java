public class Main_test {

    public static void main(String[] args) {
        try {
            Thread[] t = new Thread[2];
            Integer i = 0;
            t[0] = new Thread(new Test_IRC_1());
            t[1] = new Thread(new Test_IRC_2());

            for (Thread y : t) {
                y.start();
            }
            for (Thread y :t) {
                i += 1;
                y.join();
                System.out.println("fin "+ i + "\n");
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}

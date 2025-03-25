package chapter10.item69;

public class WrongArrayLoopExample {
    public static void main(String[] args) {
        Mountain[] range = new Mountain[10];
        for(int i=0; i<range.length; i++) {
            range[i] = new Mountain();
        }
        try {
            int i = 0;
            while(true) {
                range[i++].climb();
            }
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }
    static class Mountain {
        public void climb() {
            System.out.println("Climbed the Mountain!");
        }
    }
}

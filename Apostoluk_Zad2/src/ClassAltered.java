public final class ClassAltered {

    public static void main(String[] args) {
        int k = 8;
        int a = 1;
        int b = 2;
        int c = 3;
        do {
        } while (k + a == b + c);
    }

    private static void method1() {
        int g = 4;
        int a = 1;
        double b = 2.4;
        boolean bool = true;
        do {
        } while (g++ < 10);
        do {
        } while (bool == true);
        do {
        } while (a >= b);
    }

    private static void method2(int d) {
        int a = 10;
        int b = 1;
        do {
        } while (a < (b + a) - 10 + d);
        do {
        } while (b >= 3 && a == 1);
    }
}

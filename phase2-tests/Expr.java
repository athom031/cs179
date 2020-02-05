class Expr {
  public static void main(String[]a) {
    int a;
    int b;
    a = 10;
    b = 0;
    while(!(a < b)) {
      System.out.println(a);
      a = a - 1;
    }
  }

}

class A {

  public int a(int a1, int a2, int a3, int a4, int a5) {
    int x;
    x = this.a(a1, a2, a3, a4, a5);
    return 0;
  }

  public int b(int a1, int a2, int a3) {
    return 1;
  }
}


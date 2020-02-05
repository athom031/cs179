class Expr {
  public static void main(String[]a) {
    int a;
    int b;
    int c;
    int d;
    int e;

    a = 1;
    b = 2;
    c = 3;
    d = 10;
    e = 0;
    while((e < d) && (e < (a*100))) {
      e = e+1;
    }
    System.out.println(e);
  }

}

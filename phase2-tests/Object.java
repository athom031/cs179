class Object {

  public static void main(String [] a) {
    OO o0; 
    OO o1; 
    OO o2;

		int x;

  	o0 = new OO();
		o1 = new OO();
		o2 = new OO();

    x = o0.init(1, 2, 3);
    x = o1.init(4, 5, 6);
    x = o2.init(7, 8, 9);

    System.out.println(o0.getX());
    System.out.println(o0.getY());
    System.out.println(o0.getZ());

    System.out.println(o1.getX());
    System.out.println(o1.getY());
    System.out.println(o1.getZ());

    System.out.println(o2.getX());
    System.out.println(o2.getY());
    System.out.println(o2.getZ());
  }

}

class OO {
  int x;
  int y;
  int z;

  public int init(int a, int b, int c) {
    x = a;
    y = b;
    z = c;
		return 1;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getZ() {
    return z;
  }
}



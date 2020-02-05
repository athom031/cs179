class Recursion {
	public static void main(String[] a){
    A a;
    a = new A();
    System.out.println(a.run(2));
    System.out.println(new A().run(50));
	}
}

class A {
  // mutual recursion
	public int run(int i) {
    int x;
    if(i < 1000) {
      x = this.pro(i*2);
    } else {
      x = i;
    }
		return x;
	}

  public int pro(int i) {
    int x;
    x = this.run(i-1);
    return x;
  }


}

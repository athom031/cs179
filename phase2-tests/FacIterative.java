class A {
  public static void main(String [] a) {
		System.out.println(new Fib().compute(1, 1, 10));
  }
}

class Fib {

  public int compute(int a, int b, int n) {
		int x;
		if(n < 1)
			x = b;
		else
			x = this.compute(b, a+b, n-1);
		return x;
  }


}




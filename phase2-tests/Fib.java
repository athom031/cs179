class Main{
	public static void main(String[] a){
    /*Fib f;
    int i;
    i = 0;
    f = new Fib();
    while(i < 20) {
		  System.out.println(f.fib(i));
      i = i+1;
    }*/
    int i;
    i = 0;
    while(i < 20) {
      System.out.println(new Fib().fib(i));
      i = i+1;
    }
	}
}

class Fib {
  public int fib(int n) {
    int answer;
    if(n < 2) {
      answer = n;
    } else {
      answer = (this.fib(n-1)) + (this.fib(n-2));
    }
    return answer;
  }
}

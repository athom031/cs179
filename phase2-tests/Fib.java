class Main{
	public static void main(String[] a){
    int i;
    i = 0;
    while(i < 20) {
      System.out.println(new Fib().fib(i));
      i = i+1;
    }
	}
}

class Fib {

  public int dummy4() {
    return 0;
  }

  public int dummy3() {
    return 0;
  }

  public int dummy2() {
    return 0;
  }

  public int dummy1() {
    return 0;
  }

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
class A {
  public static void main(String [] a) {
    System.out.println(new Fib().compute(10));
  }
}

class Fib {

  public int compute(int n) {
    int [] numbers = new int[n];
    int i;

    numbers[0] = 0;
    numbers[1] = 1;
    i=2;
    while(i<n) {
      numbers[i] = numbers[i-1]+numbers[i-2];
      i=i+1;
    }
    return numbers[n-1];
  }


}




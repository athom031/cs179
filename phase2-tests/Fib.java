class Silly {
  public static void main(String[] a){
    System.out.println(new Fib().fib(5));
  }
}

class Fib {

  public int fib(int n) {
    int [] array; 
    int b;
    int c;
    int i;
    array = new int[n];
    array[0] = 1;
    array[1] = 1;
    i=2;
    while(i<n) {
      //c=array[i-1];
      //b=array[i-2];
      //array[i]=i;
      i=i+1;
    }
    c = array[n-1];
   
    return ;
  }
}

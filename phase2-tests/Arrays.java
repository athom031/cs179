class MainClass {

  public static void main(String [] args) {

    int [] array;
    int i;
    int j;
    int temp;

    // array w/ numbers in random order.
    array = new int[10];
    array[0] = 5;
    array[1] = 3;
    array[2] = 7;
    array[3] = 6;
    array[4] = 9;
    array[5] = 0;
    array[6] = 1;
    array[7] = 2;
    array[8] = 4;
    array[9] = 3;

    // bubble sort
    i=0;
    while(i < 10) {
      j=i+1;
      while(j < 10) {
        if((array[j]) < (array[i])) {
          temp=array[j];
          array[j]=array[i];
          array[i]=temp;
        } else {


        }
        j=j+1;
      }
      i=i+1;
    }

    // print out
    i=0;
    while(i < 10) {
      System.out.println(array[i]);
      i=i+1;
    }
  }

}



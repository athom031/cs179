class Main {
	public static void main(String[] a){ 
        A aaa;
        B bbb;
        int x;
        aaa = new A();
        bbb = new B();
        x = aaa.run();
        x = aaa.solo();
        x = bbb.soo();
	}
}



class B {
	public int run() {
		int x;
		x = 1;
		return x;
	}


	public int soo() {
		int x;
		x = 1;
		return x;
	}

}


class A {
	public int run() {
		int x;
		x = 1;
		return x;
	}


	public int solo() {
		int x;
		x = 1;
		return x;
	}

}

class Call {
	public static void main(String[] a){
		//System.out.println(new A().run());
    A a;
    a = new A();
    System.out.println(a.run());
	}
}

class A {
	public int run() {
		System.out.println(42);
		return 99;
	}
}

run:
	javac Typecheck.java
	java Typecheck < phase1-tests/Basic.java

javacc:
	java -jar jtb.jar minijava.jj
	javacc jtb.out.jj

test:
	./run_tests.sh
clean:
	rm *.class
	rm visitor/*.class
	rm syntaxtree/*.class



FILE = BubbleSort-error.java #TreeVisitor.java

run:
	javac Typecheck.java
	java Typecheck < phase1-tests/$(FILE)

javacc:
	java -jar jtb.jar minijava.jj
	javacc jtb.out.jj

test:
	sh run_tests.sh
clean:
	rm *.class
	rm visitor/*.class
	rm syntaxtree/*.class
	rm symboltable/*.class


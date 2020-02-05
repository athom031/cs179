FILE=Add
JAVA=$(FILE).java
VAPOR=$(FILE).vapor
FOLDER=phase2-tests
OUTPUT_FOLDER=out

run:
	javac J2V.java
	java J2V < $(FOLDER)/$(JAVA) > $(OUTPUT_FOLDER)/$(VAPOR)
	cat $(OUTPUT_FOLDER)/$(VAPOR)
	java -jar vapor.jar run $(OUTPUT_FOLDER)/$(VAPOR)

javacc:
	java -jar jtb.jar minijava.jj
	javacc jtb.out.jj

test:
	sh run_tests.sh

clean:
	rm *.class
	rm visitor/*.class
	rm syntaxtree/*.class
	rm $(OUTPUT_FOLDER)/*


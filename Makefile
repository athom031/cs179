FILE=MoreThan4
JAVA=$(FILE).java
VAPOR=$(FILE).vapor
FOLDER=phase2-tests
OUTPUT_FOLDER=out


vaporm:
	javac V2VM.java
	java V2VM < Phase3Tests/$(FILE).vapor > $(FILE).vaporm
	cat $(FILE).vaporm
	java -jar vapor.jar run -mips $(FILE).vaporm

run:
	javac J2V.java
	java J2V < $(FOLDER)/$(JAVA) > $(OUTPUT_FOLDER)/$(VAPOR)
	cat $(OUTPUT_FOLDER)/$(VAPOR)
	java -jar vapor.jar run $(OUTPUT_FOLDER)/$(VAPOR)


debug:
	javac J2V.java
	jdb J2V < $(FOLDER)/$(JAVA)

javacc:
	java -jar jtb.jar minijava.jj
	javacc jtb.out.jj

test:
	sh run_tests.sh

clean:
	rm *.class
	rm *.vaporm

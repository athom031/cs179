FILE=1-Basic#Factorial#MoreThan4

vaporm:
	javac V2VM.java
	java V2VM < Phase3Tests/$(FILE).vapor > $(FILE).vaporm
	cat $(FILE).vaporm
	java -jar vapor.jar run -mips $(FILE).vaporm

clean:
	rm *.class
	rm *.vaporm

FILE=QuickSort

vaporm:
	javac -g V2VM.java
	java V2VM < Phase3Tests/$(FILE).vapor > $(FILE).vaporm
	java -jar vapor.jar run -mips $(FILE).vaporm

clean:
	rm *.class
	rm *.vaporm

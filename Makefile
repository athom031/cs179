FILE=LinkedList

vaporm:
	javac -g VM2M.java
	java VM2M < Phase4Tests/$(FILE).vaporm > $(FILE).s
	java -jar mars.jar nc $(FILE).s

answer:
	java -jar mars.jar nc Phase5Tests/$(FILE).s

clean:
	rm *.class
	rm *.s

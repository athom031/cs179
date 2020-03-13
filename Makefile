FILE=BinaryTree

vaporm:
	javac -g VM2M.java
	java VM2M < Phase4Tests/$(FILE).vaporm > $(FILE).s
	java -jar mars.jar nc $(FILE).s

clean:
	rm *.class
	rm *.s

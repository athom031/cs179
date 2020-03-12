FILE=BinaryTree

vaporm:
	javac VM2M.java
	java VM2M < Phase4Tests/$(FILE).vaporm > P.s
	java -jar mars.jar nc P.s

clean:
	rm *.class
	rm *.vaporm

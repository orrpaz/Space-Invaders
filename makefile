# 311250708 Or Paz
bin:
	mkdir bin
compile: bin
	javac -d bin -cp biuoop-1.4.jar src/*/*.java src/*.java
jar: compile
	jar cfm space-invaders.jar Manifest.txt -C bin . -C resources .
run: compile
	java -cp biuoop-1.4.jar:bin:resources Ass7Game
check:
	java -jar checkstyle-5.7-all.jar -c biuoop.xml src/*/*.java src/*java

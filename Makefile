# Java compiler
JAVAC=javac
# Java Archive maker
JAR=jar
# Verbosity
BUILD_VERBOSE=1

# Verbosity parser
ifeq ("$(BUILD_VERBOSE)","2")
VERBOSE1=v
VERBOSE2=-verbose
else
ifeq ("$(BUILD_VERBOSE)","0")
VERBOSE1=
VERBOSE2=
else
VERBOSE1=v
VERBOSE2=
endif
endif

all: AutoAppro.jar

AutoAppro.jar: bin/AutoAppro.class
	$(JAR) c$(VERBOSE1)fm AutoAppro.jar Manifest.txt -C bin/ .

bin/AutoAppro.class: src/AutoAppro.java
	$(JAVAC) $(VERBOSE2) -sourcepath src -classpath bin src/AutoAppro.java -d bin

clean:
	rm -r$(VERBOSE1)f AutoAppro.jar bin/*.class bin/loggers/*.class bin/models bin/providers/*.class bin/util
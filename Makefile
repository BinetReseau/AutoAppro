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

AutoAppro.jar: bin/AutoAppro/AutoAppro.class
	$(JAR) c$(VERBOSE1)fm AutoAppro.jar Manifests/AutoAppro/Manifest.txt -C bin/ .
	$(JAR) c$(VERBOSE1)fm updater.jar Manifests/updater/Manifest.txt -C bin/ .

bin/AutoAppro/AutoAppro.class: src/AutoAppro/AutoAppro.java
	$(JAVAC) $(VERBOSE2) -sourcepath src -classpath bin src/AutoAppro/AutoAppro.java -d bin
	$(JAVAC) $(VERBOSE2) -sourcepath src -classpath bin src/updater/Updater.java -d bin

clean:
	rm -r$(VERBOSE1)f AutoAppro.jar updater.jar bin/AutoAppro/*.class bin/loggers/*.class bin/models bin/suppliers/*.class bin/updater/*.class bin/util

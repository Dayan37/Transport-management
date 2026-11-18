#!/bin/sh
# Compile and run without IntelliJ. Needs the MySQL Connector/J jar inside lib/
cd "$(dirname "$0")"
mkdir -p out
javac -encoding UTF-8 -d out src/*.java && java -cp "out:lib/*" Main

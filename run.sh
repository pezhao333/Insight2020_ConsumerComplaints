#!/usr/bin/env bash

javac ./src/report.java
java -classpath ./src report ./input/complaints.csv ./output/report.csv

#!/usr/bin/env bash

javac -cp ./src/report.java

java -cp ./src report ./input/complaints.csv ./output/report.csv
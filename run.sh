#!/usr/bin/env bash

javac ./src report.java

java ./src report ./input/complaints.csv ./output/report.csv

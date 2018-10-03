# OCR-core 

## Repository Purpose

This repository is for a Monash University Industrial Experience project done by "The DreamTeam". This repository is the core part of the system being built for the University and a scouted company Aztomix.

## What is it?

A license plate reader that can be used in server side applications. This software presents a server running on port 10000 and accepts a byte[] and returns a string. 

## How to run it?

    java -jar ocr.jar


Through ssh terminal use screen:

    screen -d -m java -jar ocr.jar


## How to build it

    mvn clean install

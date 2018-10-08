# OCR-core 

## Repository Purpose

The DreamTeam is a Monash University undergrad team building a software solution for Aztomix (our "employers") under the guidance of our Monash University mentor. This counts as credits spanning accross 2 semesters and is called the Industrial Experience Project (FIT3047 - FIT3048). 

## What is it?

A Java based Automatic NumberPlate Recognition (ANPR) build on OpenCv and Tesseract. This software presents a Java websocket server running on port 10000 and accepts a byte[ ] which returns a string of extracted text. 

## How to run it?

    java -jar ocr.jar


Running it in the background use the screen package:

    sudo apt install screen
    screen -d -m java -jar ocr.jar


## How to build it

    git clone git@github.com:Benehiko/ocr-core.git
    cd ocr-core/
    mvn clean install
    cd target/
    java -jar ocr-core.jar


## A full guide is available on this repository's Wiki

[Setting up the Server](https://github.com/Benehiko/ocr-core/wiki/Setting-up-the-Server)


## Licensing

Copyright (c) The DreamTeam

Copyright (c) Monash South Africa

Copyright (c) Aztomix

All rights reserved.

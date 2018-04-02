## Welcome to OCR core
The purpose of this repository is to create a fully functioning java OCR using javacv and tesseract to recognise number plates and extract the text from them.


### The breakdown
OCR core will be used to recognise and extract South African number plates. The software needs to work in harsh weather conditions and any time of day. Since South Africa has different kinds of number plates for different regions, provisions also need to be made for that. 

Currently there will be no need for a real-time system as extracting the number plate from a picture is the main concern.

### Releases
Compiled jar files will be released with an API for ease of use. Check the releases tab and the wiki for more information on this (soon).

### Stages

#### Stage 1
Get Tesseract to extract text from an image

#### Stage 2
Use javacv to improve the quality of the image so tesseract can be more accurate

#### Stage 3
Use javacv to recognise shapes in the image for more focused text extraction

#### Stage 4
Implement realtime recognition

### Completed

- Stage 1
- Stage 2

### Version Control

- Version 0 = Stage 1 - 2
- Version 1 = Stage 3
- Version 2 = Stage 4

### Prerequisites 
This sorftware makes use of a lot leptonica and tesseract c++ libraries, which should be installed.

### Summary of Installation (Linux only)
```
sudo apt-get -y install g++ autoconf automake libtool autoconf-archive pkg-config libpng-dev libjpeg8-dev libtiff5-dev zlib1g-dev

git clone https://github.com/DanBloomberg/leptonica.git
cd leptonica/
./autobuild
./configure
./make
./make install
cd ..

git clone https://github.com/tesseract-ocr/tesseract.git tesseract-ocr

cd tesseract-ocr
    ./autogen.sh
    ./configure
    make
    sudo make install
    sudo ldconfig
```

### Leptonica
https://github.com/DanBloomberg/leptonica

### Tesseract
https://github.com/tesseract-ocr/tesseract

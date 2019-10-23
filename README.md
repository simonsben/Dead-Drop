# README

Writing software to embed data in one or multiple images

## Stages/features

* Open, read, and write files 
* Read JPG headers
* Add data to specific bytes in each block

## Format

* Encoding header
  * 1 byte for encoding type
  * 4 bytes for number of bytes in data
  * 2 bytes for image index (if mode 1)
  * 4 byte encoding ID (if mode 1)
  * X byte filename list (if mode 2)
* Encoding type
  * 0 single-file (default)
  * 1 multi-file indexed
  * 2 multi-file header-defined

## Resources

* Used the classic [Lena test image](https://www.ece.rice.edu/~wakin/images/lenaTest1.jpg)

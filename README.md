# README

Writing software to embed data in one or multiple images

## Stages/features

* Open, read, and write files 
* Add data to specific bytes in each block
* Add checkums
* Add original filename
* Quality setting
    * Increase impact on image quality (ex. enable all of LSB for BPCS or lower threshold)

## Format

* Encoding header
  * 1 byte for encoding type
  * 4 bytes for number of bytes in data
  * 2 bytes for core.image index (if mode 1)
  * 4 byte encoding ID (if mode 1)
* Encoding type
  * 0 single-file (default)
  * 1 multi-file indexed

## Resources

* Used the classic [Lena test image](https://www.ece.rice.edu/~wakin/images/lenaTest1.jpg)

# README

Writing software to embed data in one or multiple images

## Stages/features

* Add checkums
* Quality setting
    * Increase impact on image quality (ex. enable all of LSB for BPCS or lower threshold)

## Format

* Encoding header
  * 1 byte for encoding type + technique + signature
  * 4 bytes for number of bytes in data
  * 1 byte for image index (if mode 1)
  * 2 bytes sequence ID (if mode 1)
* Encoding type
  * 0 single-file (default)
  * 1 multi-file indexed
* Techniques
  * 0 naive (default)
  * 1 bpcs

First byte of header uses the bottom bit (LSB) for encoding type, the second LSB for technique, and the top 5 bits for encoding signature.
The signature should inform the program whether an image contains encoded information.
The probability of having all 6 bits match the assigned signature would be 1/2^6 or ~1.6% (assuming pixels are uniform).
Once the header format/encoding types are more developed more bits may be allocated to the signature to reduce the probability of error.

## Environment

Project was developed using Java 13.

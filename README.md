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

## Usage

For both encode and decode, call encoding handler with the following parameters:

* Specify encode with `-E` (or `-D` for decode), followed by either `B` or `A` to use the basic or advanced encoder, respectively.
* Choose the technique with `-T` followed by either `N` or `B` for naive or bpcs, respectively.
* Give the path to the directory containing the images with `-s source_directory`.
* *[Optionally]* pass a custom encryption key to encoder with `-k custom_key`.

### Encoding

To encode, also pass the following parameters:

* Pass `-d data_file` to specify the **file** you would like to embed.
  * NOTE: if you have a set of files, compress them first (or just use tar as a container). 
* Give the target directory to save the images to with `-t target_directory`.

### Decoding

To decode, also pass the following parameters

* Pass `-d data_file` to specify the path where you would like to save the embedded file

## Environment

Project was developed using Java 13.

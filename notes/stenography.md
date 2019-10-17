# Stenography notes

## Traditional method

The traditional method to embed data into an image is very straight forward, but very short-sided.
By only utilizing the least-significant bit of an image the amount of data that can be hidden is limited to 1/8th the file size.
The approach being:

* Get target image
* Get target data (to be hidden)
* Move through image and embed the *hidden* data into the least significant bit of each byte in the file

## More thought through method

* Embed information in high-frequency components of images since the human eye has a hard time noticing detail there

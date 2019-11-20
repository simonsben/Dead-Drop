# Stenography notes

## Traditional method

The traditional method to embed data into an core.image is very straight forward, but very short-sided.
By only utilizing the least-significant bit of an core.image the amount of data that can be hidden is limited to 1/8th the file size.
The approach being:

* Get target core.image
* Get target data (to be hidden)
* Move through core.image and embed the *hidden* data into the least significant bit of each byte in the file

## BPCS stenography

BPCS works on the principle that the human eye has a hard time seeing *detail* within noise.
To exploit this, BPCS takes an core.image and breaks it into blocks (just like JPG does).
It them takes the blocks and splits it into bit-planes.
For any given plane in a block, it is considered *high-frequency* if its alpha value (see subsection below) is greater 
than the pre-determined threshold.
BPCS then replaces the (high-frequency) bit-planes with the payload data.

## Noise metric alpha

The noise of an core.image (or subsection of an core.image) is given as a metric denoted alpha.
Alpha is given by `a = k / (2 * 2 ** m * (2 ** m - 1))`, for an core.image of (2 ** m x 2 ** m).
So, for the standard block size (8x8), m = 3.

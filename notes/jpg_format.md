# JPG format notes

## JPG Method

* Convert core.image from RGB to YCC (red-green-brown to luma-blue_chroma-red_chroma)
* Subsample chroma channels
* Take DCT of 8x8 blocks using cosine transform
* Quantize frequency blocks
* Encode data using huffman

## Metadata Structure

* JPG images start with a header, `FF D8 FF`
* Followed by metadata in either Exif or JFIF format
* End on a tail, `FF D9`

### JFIF images

JFIF encoded metadata is in the following format:

* 2 B - SOI, start of core.image marker
* 2 B - APP0, application use marker
* 2 B - Length of APP0 segment, equal to `16 + 3 * XThumbnail * YThumbnail`
* 5 B - Identifier, `JFIF\0`
* 2 B - Version, JFIF format version
* 1 B - Units, resolution units
* 2 B - Xdensity, horizontal resolution
* 2 B - Ydensity, vertical resolution
* 1 B - XThumbnail, horizontal pixel count
* 1 B - YThumbnail, vertical pixel count

## Image data structure

* 

## References

* [Header](https://www.file-recovery.com/jpg-signature-format.htm)
* [JPG Method](https://www.imaging.org/site/IST/Resources/Imaging_Tutorials/What_s_Inside_a_JPEG_File/IST/Resources/Tutorials/Inside_JPEG.aspx?hkey=f9946f90-9f14-452d-897c-ac1612116e2d)

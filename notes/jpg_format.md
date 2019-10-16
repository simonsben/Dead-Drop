# JPG format notes

## Structure

* JPG images start with a header, `FF D8 FF`
* Followed by metadata in either Exif or JFIF format
* End on a tail, `FF D9`

### JFIF images

JFIF encoded metadata is in the following format:

* 2 B - SOI, start of image marker
* 2 B - APP0, application use marker
* 2 B - Length of APP0 segment, equal to `16 + 3 * XThumbnail * YThumbnail`
* 5 B - Identifier, `JFIF\0`
* 2 B - Version, JFIF format version
* 1 B - Units, resolution units
* 2 B - Xdensity, horizontal resolution
* 2 B - Ydensity, vertical resolution
* 1 B - XThumbnail, horizontal pixel count
* 1 B - YThumbnail, vertical pixel count

## References

* [Header](https://www.file-recovery.com/jpg-signature-format.htm)
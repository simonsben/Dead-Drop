# README

Some ideas to use to get back into Java and write some interesting code

## Idea 1

* Implement a data stream with run-length compression (?LZW?)
* Encrypt it
* Add checksums
* Add delta encoding with split stream every N bytes in case of errors
* Add some way to sync codebooks every L bytes

### Classes

* streamer
  * handles high-level class interaction
* connection_handler
  * handles 
    * low level byte interaction
    * checksum
* compressor
  * HashSet of values
* encrypt_engine
  * handles wrapping around encryption library

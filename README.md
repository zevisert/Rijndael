# Rijndael

Rijndael is the name that the now ubiquitous AES-256 was proposed under.

This repository implements AES-256 encryption in Java.

## Usage

Compile:
```bash
$> cd Rijndael
$> javac *.java
```
Interation:
```bash
$> java AES option keyFile inputFile
```
Where `keyFile` is a key file, `inputFile` (no extension) names a file containing lines of plaintext, and option is `e` or `d` for encryption and decryption, respectively.

`keyFile` contains a single line of 64 hex characters, which represents a 256-bit key. The `inputFile` should have 32 hex characters per line.

> Note:
> For simplicity, we will not worry about padding and just assume that the input fits exactly into multiple lines of 32 hex characters.

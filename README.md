# Encoder-Decoder

This is a Reed-Solomon Berklemp Welsch Encoder and Decoder. 

The encoder takes a .txt file and encodes it into an .enc file. This file than can then have one character changed in every 
block of 27 characters and the Decoder will decode this .txt file into the original .txt file without any errors. It uses 
redundancy padding and error correction to accomplish this.

I wrote this 1.5 years ago and the code is pretty rough. The Decoder class especially has a lot of heavy math in it and is 
fairly hard to comprehend. If I wrote this today I would do things a lot differently. Both methods have wawy too many global
variables and some of the method names are pretty bad. It can be hard to follow the flow of the two classes. Most of the logic 
is solid though.

That being said this does still work and I think it is fairly cool. You can use the Encoder to encode any basic .txt file and
it will encode it into a file called output.enc which is just another text file. You can modify some the characters in here 
then run the Decoder on it and it should return the original file.

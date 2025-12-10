# Image2Blueprint

Image2Blueprint converts basic bitmaps into Factorio blueprints.
v1 is designed to work with Pyanadons multi-color refined concrete, white lime tile, and brick path.

Run it from the command line like so:

```bash
java -jar Image2Blueprint.jar -d input.png
```

Where
- `-d` is an optional flag to enable dithering.
- `input.png` is the path to the input image file.  It must be the last parameter.

### Input

The input image should be a any image file ImageIO can read.  
This includes **PNG, JPEG, BMP, GIF, PIO,** and **WEBP** files.

### Output 
The output will be to stdout. Redirect it to a file like so:

```bash
java -jar Image2Blueprint-1.0-SNAPSHOT.jar [-d] input.png > output.txt
```

The output file will contain a Factorio blueprint string.  You can import this string into Factorio by copying it to your clipboard and using the import function in the blueprint library.

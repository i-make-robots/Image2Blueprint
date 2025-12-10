package com.marginallyclever.image2blueprint;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * {"position": { "x": 975, "y": 214 }, "name": "lab-white"                },   // FFFFFF
 * {"position": { "x": 975, "y": 214 }, "name": "stone-path"               },   // 52514A
 * {"position": { "x": 975, "y": 214 }, "name": "refined-concrete"         },   // 313129
 * {"position": { "x": 977, "y": 214 }, "name": "black-refined-concrete"   },   // 000000
 * {"position": { "x": 983, "y": 214 }, "name": "yellow-refined-concrete"  },   // D6AA10
 * {"position": { "x": 980, "y": 215 }, "name": "brown-refined-concrete"   },   // 4A1C00
 * {"position": { "x": 986, "y": 215 }, "name": "acid-refined-concrete"    },   // 8CC229
 * {"position": { "x": 979, "y": 217 }, "name": "orange-refined-concrete"  },   // DE7D21
 * {"position": { "x": 987, "y": 217 }, "name": "green-refined-concrete"   },   // 10C229
 * {"position": { "x": 979, "y": 220 }, "name": "red-refined-concrete"     },   // CE0400
 * {"position": { "x": 987, "y": 220 }, "name": "blue-refined-concrete"    },   // 218AE6
 * {"position": { "x": 981, "y": 222 }, "name": "purple-refined-concrete"  },   // 7B1CAD
 * {"position": { "x": 985, "y": 222 }, "name": "cyan-refined-concrete"    },   // 42C2B5
 * </pre>
 * https://wiki.factorio.com/Blueprint_string_format
 * https://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering
 */
class Image2Blueprint {
    static final String start = "{\"blueprint\":{\"icons\":[{\"signal\":{\"name\":\"refined-concrete\"},\"index\":1}],\"tiles\":[";
    static final String end = "]},\"item\":\"blueprint\",\"label\":\"";
    static final String end2 = "\",\"version\":281479276776194}";

    Map<Color,String> table = new HashMap<>();
    List<Color> keys = new ArrayList<>();

    public Image2Blueprint() {
        table.put(new Color(0xFF,0xFF,0xFF), "lab-white");
        table.put(new Color(0x52,0x51,0x4A), "stone-path");
        table.put(new Color(0x31,0x31,0x29), "refined-concrete");
        table.put(new Color(0x00,0x00,0x00), "black-refined-concrete");
        table.put(new Color(0xD6,0xAA,0x10), "yellow-refined-concrete");
        table.put(new Color(0x4A,0x1C,0x00), "brown-refined-concrete");
        table.put(new Color(0x8C,0xC2,0x29), "acid-refined-concrete");
        table.put(new Color(0xDE,0x7D,0x21), "orange-refined-concrete");
        table.put(new Color(0x10,0xC2,0x29), "green-refined-concrete");
        table.put(new Color(0xCE,0x04,0x00), "red-refined-concrete");
        table.put(new Color(0x21,0x8A,0xE6), "blue-refined-concrete");
        table.put(new Color(0x7B,0x1C,0xAD), "purple-refined-concrete");
        table.put(new Color(0x42,0xC2,0xB5), "cyan-refined-concrete");
        keys.addAll(table.keySet());
    }

    public static void main(String[] args) {
        boolean addDither=false;

        // load arg[args.length-1] using imageIO
        if (args.length < 1) {
            System.out.println("Usage: java Scratch <imagefile>");
            return;
        }
        for( String arg : args ) {
            if(arg.startsWith("-d")) {
                addDither = true;
                System.out.println("Dithering enabled.");
            }
        }

        // load the image
        java.awt.image.BufferedImage img = null;
        try {
            img = javax.imageio.ImageIO.read(new java.io.File(args[args.length-1]));
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return;
        }
        int width = img.getWidth();
        int height = img.getHeight();
        System.out.println("Size:" + width + "x" + height);

        // get the raster, which lets us access all the pixel data, including alpha channel.
        var raster = img.getRaster();
        int [] components = new int[img.getColorModel().getNumComponents()];

        // create a scratch, which will populate the color table.
        Image2Blueprint sc = new Image2Blueprint();

        // start building the uncompressed blueprint string.
        StringBuilder sb = new StringBuilder();
        sb.append(start);
        String add = "";

        // build the output preview image
        java.awt.image.BufferedImage after = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);

        // iterate over the image pixels, and for each pixel, find the closest color in the table.
        // use dithering to improve quality.

        // collect the error term for dithering
        Color [] error1 = new Color[width];
        Color [] error2 = new Color[width];

        // for every pixel,
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // get the color, including the error from dithering
                raster.getPixel(x, y, components);
                Color err = error1[x];
                if(err ==null) {
                    err = new Color(0,0,0);
                }
                Color c = new Color(
                        clampColor(components[0] + err.getRed()),
                        clampColor(components[1] + err.getGreen()),
                        clampColor(components[2] + err.getBlue()),
                        components.length > 3 ? components[3] : 255);
                // ignore transparent pixels.
                if (c.getAlpha() < 128) {
                    continue;
                }
                // find closest color in the palette
                Color closest = findClosestColor(sc, c);
                // apply dithering to distribute error
                if(addDither) dither(x,y,width,height,c,closest,error1,error2);

                // build the json entry for this tile
                sb.append(add);
                sb.append("{\"position\":{\"x\":");
                sb.append(x);
                sb.append(",\"y\":");
                sb.append(y);
                sb.append("},\"name\":\"");
                sb.append(sc.table.get(closest));
                sb.append("\"}");
                // set add to comma for next entry.  this way the first entry has no leading comma.
                add=",";

                after.setRGB(x,y,closest.getRGB());
            }
            // swap error arrays for next row
            error1 = error2;
            error2 = new Color[width];
        }
        // finish json
        sb.append(end);
        // the last part of the filename should be the label.
        String absPath = args[args.length-1];
        String label = new java.io.File(absPath).getName();
        sb.append(label);
        sb.append(end2);

        // print result
        //System.out.println(sb.toString());
        String result = zipAndBase64Encode(sb);

        System.out.println("Encoded string:");
        System.out.println("0" + result);

        // save the preview image
        try {
            javax.imageio.ImageIO.write(after, "PNG", new java.io.File("after.png"));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private static String zipAndBase64Encode(StringBuilder sb) {
        try {
            // gzip level 9 compress the result
            byte[] input = sb.toString().getBytes("UTF-8");
            java.util.zip.Deflater deflater = new java.util.zip.Deflater(9);
            deflater.setInput(input);
            deflater.finish();
            byte[] buffer = new byte[input.length];
            int compressedDataLength = deflater.deflate(buffer);
            deflater.end();
            byte[] output = new byte[compressedDataLength];
            System.arraycopy(buffer, 0, output, 0, compressedDataLength);
            // make base64 string
            return java.util.Base64.getEncoder().encodeToString(output);
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static Color findClosestColor(Image2Blueprint sc, Color c) {
        // find closest color in table.
        Color closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Color key : sc.keys) {
            double dist = colorDistance(c, key);
            if (dist < closestDist) {
                closestDist = dist;
                closest = key;
            }
        }
        assert closest != null;
        return closest;
    }

    // apply dithering error to neighboring pixels
    private static void dither(int x,int y,int width,int height,Color oldPixel, Color newPixel, Color[] error1, Color[] error2) {
        // compare closest color to original color, record error for dithering
        float [] quantizationError = new float[] {
                (oldPixel.getRed() - newPixel.getRed()),
                (oldPixel.getGreen() - newPixel.getGreen()),
                (oldPixel.getBlue() - newPixel.getBlue())
        };

        // distribute error to neighboring pixels using Floyd-Steinberg dithering
        if (x + 1 < width) {
            error1[x+1] = scaleColor(quantizationError,error1[x+1],7/ 16.0f);
        }
        if (y + 1 < height) {
            if(x - 1 >= 0) {
                error2[x-1] = scaleColor(quantizationError,error2[x-1],3/ 16.0f);
            }
            error2[x] = scaleColor(quantizationError,error2[x],5/ 16.0f);
            if(x + 1 < width) {
                error2[x+1] = scaleColor(quantizationError,error2[x+1],1/ 16.0f);
            }
        }
    }

    private static Color scaleColor(float [] quantizationError,Color c, double scale) {
        if (c == null) {
            c = new Color(0,0,0);
        }
        int r = clampColor(c.getRed() + (int)(quantizationError[0] * scale));
        int g = clampColor(c.getGreen() + (int)(quantizationError[1] * scale));
        int b = clampColor(c.getBlue() + (int)(quantizationError[2] * scale));
        return new Color(r,g,b);
    }

    private static int clampColor(int i) {
        return Math.clamp(i,0,255);
    }

    /**
     * Calculate the color distance between two colors.  This uses the "perceptual color distance" formula.
     * @param c
     * @param key
     * @return
     */
    private static double colorDistance(Color c, Color key) {
        double rmean = (c.getRed() + key.getRed()) / 2.0;
        double r = c.getRed() - key.getRed();
        double g = c.getGreen() - key.getGreen();
        double b = c.getBlue() - key.getBlue();
        return Math.sqrt((((512 + rmean) * r * r) /256) + 4 * g * g + (((767 - rmean) * b * b) /256));
    }
}
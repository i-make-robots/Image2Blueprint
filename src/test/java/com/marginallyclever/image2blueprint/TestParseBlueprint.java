package com.marginallyclever.image2blueprint;

import org.junit.jupiter.api.Test;

public class TestParseBlueprint {
    @Test
    public void testParseBlueprint() {
        //String input = "0eNqV08GOgyAQBuB3mbNN1EopvMqmB6qzZrJ0MEi3a4zvXm1NakI3oVfgm58Jwwhne8XOEwfQI1DtuAf9NUJPLRu7rLG5IGjw+E2MzW4+UXsMCFMGxA3+gS6mUwaBLD5p53oK5HjB866SIoMBdFlUM/m/2hsoY3i2pv7ZJfHjPuYDWutuiT5fvdjEe3fjRH6IuampSdNSrVq+tPOGW0xMl7FvPSJ/Fl/m2zdrPsve4mXMEnWx6vKlu6vvbKoXsa8H867xeWwp4GW93vMXZPCLvn/UE4dSVUqJY7FXKq+m6Q6XTwoi";
        String input = "0eNqk3d+OLIdxmPF34bUFdP2v0qsEuaBkJiLCUIYkgxEMv3tk8JyFgtkcTtV34wu5Szu7/U1Pb/VPe/7juz/89O8//Ntffvz5b9/9/j+++/GPf/75r9/9/r/9x3d//fF//vz9T//1n/38/f/+4bvff/eXH/7Hjz//8K+/+8cRf/zLD3/74bv//Jfvfvz5X3/4P9/9Xv7zv//Ld3/78acffh39tz//9ce//fjnn/9r+B//3+dfvvv7P/7vPw7/8t/0h5++/+P/+t0n/30vo3If1fuo3Uf9Phr30byP1n2076MDkiA5gZ4EBCWgKAFJCWhKQFQCqhKQlYCuFHSl5DoFulLQlYKuFHSloCsFXSnoSkFXBroy0JWRD0DQlYGuDHRloCsDXRnoykBXDrpy0JWDrpzcWYGuHHTloCsHXTnoykFXAboK0FWArgJ0FeSWHXQVoKsAXQXoKkBXCbpK0FWCrhJ0laCrfOnqp+//8Ltf/vTj/+fwl5T++Pfvf37vK72U9Ne//fnnH373b9//7U+fHt/L41+KeeuX0mf3VUqWx+vyeFse78vjY3l8Lo9fnuVanuWa3fG9PL+9PL+9PL+9PL/t5zdc797aDT4lmux2wKdEg0+JAZ8SAz4lBnxKDPiUGPApMeDuY0BXA7oasjREW0OyNnzI3vAhi8OHbA4fsjp8yO7wIcvDh2wPH7I+fEhhbDFNCkOrabSbRstptJ1G62m0n0YLarKhFrKiFkXPPkhhZEstZE0tZE8tZFEtZFMtZFUtZFctZFktZFsthh6vkcLIwlrIxlrIylrIzlrI0lrI1lrI2lrI3lrI4locPcElhZHdtZDltZDttZD1tZD9tZAFtpANtpAVtpAdtgRCAqQwssYWsscWssgWsskWssoWsssWsswWss0Wss6WRA6FFJaksCSFJSmsSGFFCitQ2K8vWu7QT+7QT+7QT+7QT+7QT+7QT+7QT+7QT+7QTwD0EwD9BEA/AdBPAPQTAP0EQD8B0E8A9BMA/QRAPwHQTwD0EwD9BEA/AdBPAPQTAP0EQD8B0E8A9BMA/QRAPwHQTwD0EwD9BEA/AdBPAPQTAP0EQD8B0E8A9BMA/QRAPwHQTwD0EwD9BEA/AdBPAPQTAP0EQD8B0E8A9BMA/QRAPwHQTwD0EwD9BEA/uWi0l6x+Q5fp8nhbHu/L418yeeu7BoEkCCRBIDmX77Se05ScpvQ0ZacpP02daqk8TdVpqk9Tpzb61Eaf2mjwEdTgI6jBR1CfauncXcG6lsf38vg5fzgMuF8ZcL8yIJYBsQyIZcD9yoCPowEfR0PWjGjPSBaND9k0PmTV+JBd40OWjQ/ZNj5k3fiQfeNDFo4PKYytsklhaA==";
        // decode base64
        byte[] decodedBytes = java.util.Base64.getDecoder().decode(input.substring(1));
        // deflate using zlib deflate
        try {
            java.util.zip.Inflater inflater = new java.util.zip.Inflater();
            inflater.setInput(decodedBytes);
            byte[] result = new byte[1000];
            int resultLength = inflater.inflate(result);
            inflater.end();
            System.out.println(new String(result, 0, resultLength));

            //long value = 562949958139904;  // hex 2,0000,0048,0000

        } catch (java.util.zip.DataFormatException e) {
            e.printStackTrace();
        }
    }
}

package audio.advanced;

import audio.advanced.io.FileIn;
import audio.advanced.io.FileOut;
import audio.advanced.io.IOIn;
import audio.advanced.io.IOOut;

import javax.sound.sampled.*;

public class Main {
    private final static int BUFFER_SIZE = 1024;
    public static void main(String[] args) {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);

            IOIn lineIn = new FileIn("test.wav", format);
            IOOut lineOut = new FileOut("output2.wav", format);
//            advanced.io.IOOut lineOut = new advanced.io.LineOut(format);
            byte[] buffer = new byte[BUFFER_SIZE];

            lineIn.start();
            lineOut.start();

            int bytesRead;
            while ((bytesRead = lineIn.read(buffer, 0, buffer.length)) != -1) {
                lineOut.write(buffer, 0, bytesRead);
            }

            lineIn.close();
            lineOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
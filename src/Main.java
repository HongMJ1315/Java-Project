import javax.sound.sampled.*;

public class Main {
    private final static int BUFFER_SIZE = 1024;
    public static void main(String[] args) {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);

            IOIn lineIn = new LineIn(format);
            Pedal overdrive = new Overdrive();
            Pedal compressor = new Compressor();
            Pedal delay = new Delay(format);
            IOOut lineOut = new LineOut(format);

            byte[] buffer = new byte[BUFFER_SIZE];

            float gain = 2f;
            float level = 25f;
            int delayMillis = 1000;
            float feedback = 0.75f;

            boolean isOverdrive = false;
            boolean isCompressor = false;
            boolean isDelay = true;

            lineIn.start();
            lineOut.start();
            ((Delay)delay).setDelay(delayMillis, feedback);

            while (true) {
                int bytesRead = lineIn.read(buffer, 0, buffer.length);
                ((Overdrive)overdrive).setGain(gain, 128);
                ((Compressor)compressor).setLevel(level);

                byte[] overdriveBuffer;
                byte[] compressorBuffer;
                byte[] delayBuffer;

                if (isOverdrive) overdriveBuffer = overdrive.process(buffer);
                else overdriveBuffer = buffer;

                if (isCompressor) compressorBuffer = compressor.process(overdriveBuffer);
                else compressorBuffer = overdriveBuffer;

                if (isDelay) delayBuffer = delay.process(compressorBuffer);
                else delayBuffer = compressorBuffer;

                lineOut.write(delayBuffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

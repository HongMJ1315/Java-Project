import javax.sound.sampled.*;

public class Main {
    public static void main(String[] args) {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);

            IOIn lineIn = new FileIn("galaxy.wav", format);
            Pedal overdrive = new Overdrive();
            Pedal compressor = new Compressor();
            Pedal Delay = new Delay(format);
            IOOut lineOut = new LineOut(format);

            byte[] buffer = new byte[1024];

            float gain = 2f;
            float level = 25f;
            int delayMilles = 1000;
            float feedback = 0.5f;

            boolean isOverdrive = false;
            boolean isCompressor = false;
            boolean isDelay = false;

            lineIn.start();
            lineOut.start();

            while (true) {
                int bytesRead = lineIn.read(buffer, 0, buffer.length);
                ((Overdrive)overdrive).setGain(gain, 128);
                ((Compressor)compressor).setLevel(level);
                ((Delay)Delay).setDelay(delayMilles, feedback);

                byte[] overdriveBuffer;
                byte[] compressorBuffer;
                byte[] delayBuffer;

                if (isOverdrive) overdriveBuffer = overdrive.process(buffer);
                else overdriveBuffer = buffer;

                if (isCompressor) compressorBuffer = compressor.process(overdriveBuffer);
                else compressorBuffer = overdriveBuffer;

                if (isDelay) delayBuffer = Delay.process(compressorBuffer);
                else delayBuffer = compressorBuffer;

                lineOut.write(delayBuffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

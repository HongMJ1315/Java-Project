import javax.sound.sampled.*;

public class Main {
    public static void main(String[] args) {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

            IOIn lineIn = new FileIn("test.wav",format);
            Pedal overdrive = new Overdrive();
            Pedal delay = new Delay(250, 0.5f, format);
            Pedal compressor = new Compressor();
            IOOut lineOut = new LineOut(format);

            byte[] buffer = new byte[1024];
            float initialGain = 5f;

            lineIn.start();
            lineOut.start();

            while (true) {
                int bytesRead = lineIn.read(buffer, 0, buffer.length);

                // 進行 Overdrive 效果處理
                ((Compressor)compressor).setLevel(3f);
                byte[] compressorBuffer = compressor.process(buffer);
//                ((Overdrive)overdrive).setGain(initialGain, 100);
//                byte[] overdriveBuffer = overdrive.process(buffer);
//                byte[] delayBuffer = delay.process(overdriveBuffer);

                System.out.println();
                lineOut.write(compressorBuffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

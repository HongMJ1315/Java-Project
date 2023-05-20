import javax.sound.sampled.*;

public class LineIn implements IOIn{
    private TargetDataLine targetDataLine;

    public LineIn(AudioFormat format) throws LineUnavailableException {
        targetDataLine = AudioSystem.getTargetDataLine(format);
        targetDataLine.open(format);
    }

    public void start() {
        targetDataLine.start();
    }

    public int read(byte[] buffer, int offset, int length) {
        return targetDataLine.read(buffer, offset, length);
    }

    public void close() {
        targetDataLine.close();
    }
}

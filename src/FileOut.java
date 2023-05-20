import javax.sound.sampled.*;
import java.io.*;

public class FileOut implements IOOut{
    private AudioFormat format;
    private OutputStream outputStream;

    public FileOut(String filename, AudioFormat format) throws IOException {
        this.format = format;
        outputStream = new FileOutputStream(filename);
    }

    public void start() {
        // 不需要實作此方法，僅為相容性考量
    }

    public void write(byte[] buffer, int offset, int length ){
        try {
            outputStream.write(buffer, offset, length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void finalize() throws Throwable {
        super.finalize();
        close();
    }
}

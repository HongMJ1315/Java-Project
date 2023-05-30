package audio.advanced.pedal;

public class Overdrive extends Pedal{
    private float gain;
    private int threshold;
    public void setGain(float gain, int threshold) {
        System.out.println("gain: " + gain + ", threshold:    " + threshold);
        this.gain = gain;
        this.threshold = threshold;
    }
    public byte[] process(byte[] input) {
        for (int i = 0; i < input.length; i++) {
            int tmp = (int)(input[i] * gain);
            if (tmp >= threshold) {
                input[i] = (byte)threshold;
            } else if (tmp <= -threshold) {
                input[i] = (byte)-threshold;
            } else {
                input[i] = (byte)tmp;
            }
        }
        return input;
    }
}

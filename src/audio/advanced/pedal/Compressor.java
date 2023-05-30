package audio.advanced.pedal;

public class Compressor extends Pedal{
    private int level;
    public void setLevel(int level){
        this.level = level;
    }
    public byte[] process(byte[] input){
        for (int i = 0; i < input.length; i++) {
            if(input[i] >= level){
                input[i] = (byte)level;
            }else if(input[i] <= -level){
                input[i] = (byte)-level;
            }
        }
        return input;
    }
}

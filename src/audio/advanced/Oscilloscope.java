package audio.advanced;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.scope.AudioScope;
import com.jsyn.unitgen.LineIn;
import com.jsyn.unitgen.LineOut;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Oscilloscope {
    private Synthesizer synth;
    LineIn lineIn;
    LineOut lineOut;
    private AudioScope scope;
    private JFrame frame;
    public Oscilloscope() {
        startAudio();
        setupGUI();
    }
    private void setupGUI() {
        System.out.println("run GUI");
        frame = new JFrame("Oscilloscope");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopAudio();
            }
        });

        scope = new AudioScope(synth);
        scope.setViewMode(AudioScope.ViewMode.WAVEFORM);

        scope.addProbe(lineIn.output);
        scope.start();

        frame.add(BorderLayout.CENTER, scope.getView());
        frame.pack();
        frame.setVisible(true);
        scope.getView().setControlsVisible(true);

    }

    private void startAudio() {
        synth = JSyn.createSynthesizer();
        synth.add(lineIn = new LineIn());
        synth.add(lineOut = new LineOut());
        AudioScope audioScope = new AudioScope(synth);
        lineIn.output.connect(0, lineOut.input, 0);
        lineIn.output.connect(1, lineOut.input, 1);
        audioScope.addProbe(lineIn.output);
        int numInputChannels = 2;
        int numOutputChannels = 2;
        synth.start(48000, AudioDeviceManager.USE_DEFAULT_DEVICE, numInputChannels,
                AudioDeviceManager.USE_DEFAULT_DEVICE, numOutputChannels);
        lineOut.start();

        System.out.println("Audio passthrough started.");
    }

    private void stopAudio() {
        synth.stop();
        System.out.println("All done.");
    }
}

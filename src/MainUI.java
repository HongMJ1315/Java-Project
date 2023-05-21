import javax.swing.*;
import javax.sound.sampled.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;

public class MainUI {
    private JFrame frame;
    private JToggleButton overdriveToggle;
    private JToggleButton compressorToggle;
    private JToggleButton delayToggle;
    private JSlider gainSlider;
    private JSlider levelSlider;
    private JSlider delaySlider;
    private JSlider feedbackSlider;
    private Pedal overdrive;
    private Pedal compressor;
    private Pedal delay;
    private boolean isOverdrive;
    private boolean isCompressor;
    private boolean isDelay;
    private float gain;
    private float level;
    private int delayMills;
    private float feedback;
    private IOIn lineIn;
    private IOOut lineOut;

    public MainUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout(0, 2, 10, 10));

        overdriveToggle = new JToggleButton("Overdrive");
        compressorToggle = new JToggleButton("Compressor");
        delayToggle = new JToggleButton("Delay");

        gainSlider = new JSlider(1, 10, 2);
        levelSlider = new JSlider(10, 128, 25);
        delaySlider = new JSlider(10, 1000, 100);
        feedbackSlider = new JSlider(1, 99, 50);

        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        overdriveToggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean selected = overdriveToggle.isSelected();
                isOverdrive = selected;
            }
        });

        compressorToggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean selected = compressorToggle.isSelected();
                isCompressor = selected;
            }
        });

        delayToggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean selected = delayToggle.isSelected();
                isDelay = selected;
            }
        });

        gainSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = gainSlider.getValue();
                gain = (float) value;
            }
        });

        levelSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = levelSlider.getValue();
                level = (float) value;
            }
        });

        delaySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = delaySlider.getValue();
                delayMills = value;
            }
        });

        feedbackSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = feedbackSlider.getValue();
                feedback = (float) value / 100;
            }
        });

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                startAudioProcessing();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
                stopAudioProcessing();
            }
        });

        frame.getContentPane().add(overdriveToggle);
        frame.getContentPane().add(compressorToggle);
        frame.getContentPane().add(delayToggle);
        frame.getContentPane().add(new JLabel("Gain"));
        frame.getContentPane().add(gainSlider);
        frame.getContentPane().add(new JLabel("Level"));
        frame.getContentPane().add(levelSlider);
        frame.getContentPane().add(new JLabel("Delay"));
        frame.getContentPane().add(delaySlider);
        frame.getContentPane().add(new JLabel("Feedback"));
        frame.getContentPane().add(feedbackSlider);
        frame.getContentPane().add(startButton);
        frame.getContentPane().add(stopButton);

        frame.setVisible(true);
    }

    private void startAudioProcessing() {
        SwingWorker<Void, Void> audioProcessingWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

                    lineIn = new FileIn("test.wav", format);
                    overdrive = new Overdrive();
                    compressor = new Compressor();
                    delay = new Delay(format);
                    lineOut = new LineOut(format);

                    byte[] buffer = new byte[1024];

                    lineIn.start();
                    lineOut.start();

                    while (!isCancelled()) {
                        int bytesRead = lineIn.read(buffer, 0, buffer.length);
                        ((Overdrive)overdrive).setGain(gain, 128);
                        ((Compressor)compressor).setLevel(level);
                        ((Delay)delay).setDelay(delayMills, feedback);

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
                } finally {
                    lineIn.close();
                    lineOut.close();
                }
                return null;
            }
        };

        audioProcessingWorker.execute();
    }

    private void stopAudioProcessing() {
        if (lineIn != null) {
            lineIn.close();
        }

        if (lineOut != null) {
            lineOut.close();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainUI();
            }
        });
    }
}

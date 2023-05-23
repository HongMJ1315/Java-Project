import javax.swing.*;
import javax.sound.sampled.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.concurrent.*;

public class MainUI {
    private final static int BUFFER_SIZE = 1024;

    enum IOInDevice {
        LINE_IN,
        FILE_IN
    }
    enum IOOutDevice {
        LINE_OUT,
        FILE_OUT
    }
    private String[] inDeviceName= {"Line In", "File In"};
    private String[] outDeviceName = {"Line Out", "File Out"};
    private JFrame frame;
    private JPanel pedalFrame;
    private JToggleButton overdriveToggle;
    private JToggleButton compressorToggle;
    private JToggleButton delayToggle;
    private JSlider gainSlider;
    private JSlider overdriveLevelSlider;
    private JSlider levelSlider;
    private JSlider delaySlider;
    private JSlider feedbackSlider;
    private JComboBox inDeviceComboBox;
    private JComboBox outDeviceComboBox;
    private JButton chooseFileButton;
    private JButton saveFileButton;
    private JButton pauseButton;
    private JButton startButton;
    private JButton stopButton;
    private Pedal overdrive;
    private Pedal compressor;
    private Pedal delay;
    private IOInDevice inDevice;
    private IOOutDevice outDevice;
    private boolean isOverdrive;
    private boolean isCompressor;
    private boolean isDelay;
    private boolean isPaused;
    private boolean isClosed;
    private float gain;
    private float overdriveLevel;
    private float level;
    private int delayMills;
    private float feedback;
    private IOIn lineIn;
    private IOOut lineOut;
    private AudioFormat format;
    private GridBagConstraints c;

    private File file;
    public MainUI() {
        initialize();
    }

    private void initialize() {
        format = new AudioFormat(44100, 16, 2, true, true);

        frame = new JFrame();
        pedalFrame = new JPanel();
        pedalFrame.setLayout(new GridBagLayout());

        inDevice = IOInDevice.LINE_IN;
        outDevice = IOOutDevice.LINE_OUT;
        try {
            lineIn = new LineIn(format);
        }catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try {
            lineOut = new LineOut(format);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        inDeviceComboBox = new JComboBox(inDeviceName);
        outDeviceComboBox = new JComboBox(outDeviceName);

        overdrive = new Overdrive();
        compressor = new Compressor();
        delay = new Delay(format);

        isPaused = false;
        isClosed = false;

        frame.setBounds(100, 100, 400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        overdriveToggle = new JToggleButton("Overdrive");
        compressorToggle = new JToggleButton("Compressor");
        delayToggle = new JToggleButton("Delay");

        gainSlider = new JSlider(1, 10, 2);
        overdriveLevelSlider = new JSlider(1, 128, 25);
        levelSlider = new JSlider(10, 128, 25);
        delaySlider = new JSlider(10, 1000, 100);
        feedbackSlider = new JSlider(1, 99, 50);

        startButton = new JButton("Start");
        stopButton = new JButton("Close");
        stopButton.setEnabled(false);
        pauseButton = new JButton("Pause");
        chooseFileButton = new JButton("Choose File");
        chooseFileButton.setName("chooseFileButton");
        saveFileButton = new JButton("Save File");
        saveFileButton.setName("saveFileButton");
        c = new GridBagConstraints();

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
                ((Overdrive)overdrive).setGain(gain, overdriveLevel);

            }
        });

        overdriveLevelSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = overdriveLevelSlider.getValue();
                overdriveLevel = (float) value;
                ((Overdrive)overdrive).setGain(gain, overdriveLevel);
            }
        });

        levelSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = levelSlider.getValue();
                level = (float) value;
                ((Compressor)compressor).setLevel(level);

            }
        });


        delaySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = delaySlider.getValue();
                delayMills = value;
                ((Delay)delay).setDelay(delayMills, feedback);
            }
        });

        feedbackSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = feedbackSlider.getValue();
                feedback = (float) value / 100;
                ((Delay)delay).setDelay(delayMills, feedback);
            }
        });

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(isClosed){
                    isClosed = false;
                    isPaused = false;
                    int inDeviceIndex = inDeviceComboBox.getSelectedIndex();
                    try{
                        lineOut = new LineOut(format);
                        if(inDeviceIndex == 0) {
                            lineIn = new LineIn(format);
                        } else if(inDeviceIndex == 1) {
                            lineIn = new FileIn(file);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return;
                    }
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    pauseButton.setEnabled(true);
                    startAudioProcessing();
                    return;
                }
                if(isPaused) {
                    isPaused = false;
                    startButton.setEnabled(false);
                    pauseButton.setEnabled(true);
                    return;
                }
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                pauseButton.setEnabled(true);
                inDeviceComboBox.setEnabled(false);
                startAudioProcessing();

            }
        });

        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
                inDeviceComboBox.setEnabled(true);
                isClosed = true;
                stopAudioProcessing();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pauseButton.setEnabled(false);
                startButton.setEnabled(true);
                isPaused = true;
            }
        });
        chooseFileButton.addActionListener(new ActionListener() {
            // 限制只能选择 wav 文件
            FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV File", "wav");
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(filter);
                int returnVal = fileChooser.showOpenDialog(pedalFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    try {
                        lineIn = new FileIn(file.getAbsolutePath(), format);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        inDeviceComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = inDeviceComboBox.getSelectedIndex();
                if (index == 0) {
                    // 选择 Line In
                    inDevice = IOInDevice.LINE_IN;
                    try {
                        lineIn = new LineIn(format);
                        c.gridx = 2;
                        c.gridy = 6;
                        c.gridwidth = 1;

                        // 移除具有指定名称的 "File name" 标签
                        Component[] components = pedalFrame.getComponents();
                        for (Component component : components) {
                            if (component.getName() != null && (component.getName().equals("inFileLabel")||component.getName().equals("chooseFileButton")||component.getName().equals("saveFileButton"))){
                                pedalFrame.remove(component);
                            }
                        }

                        pedalFrame.revalidate();
                        pedalFrame.repaint();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return;
                    }
                } else if (index == 1) {
                    // 选择 File In
                    inDevice = IOInDevice.FILE_IN;
                    try {
                        c.gridx = 2;
                        c.gridy = 6;
                        c.gridwidth = 1;
                        JLabel inFileLabel = new JLabel("File name:");
                        inFileLabel.setName("inFileLabel");
                        pedalFrame.add(inFileLabel, c);
                        c.gridx = 3;
                        c.gridy = 6;
                        c.gridwidth = 1;
                        pedalFrame.add(chooseFileButton, c);
                        c.gridx = 3;
                        c.gridy = 5;
                        c.gridwidth = 1;
                        pedalFrame.add(saveFileButton, c);
                        pedalFrame.revalidate();
                        pedalFrame.repaint();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return;
                    }
                }
            }
        });
        initPedalFrame();
    }
    private void initPedalFrame(){
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        pedalFrame.add(new JLabel("Gain"), c);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        pedalFrame.add(gainSlider, c);
        c.gridx = 3;
        c.gridy = 0;
        c.gridheight = 2;
        c.gridwidth = 1;
        pedalFrame.add(overdriveToggle, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        pedalFrame.add(new JLabel("Level"), c);
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        pedalFrame.add(overdriveLevelSlider, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        pedalFrame.add(new JLabel("Level"), c);
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 2;
        pedalFrame.add(levelSlider, c);
        c.gridx = 3;
        c.gridy = 2;
        c.gridwidth = 1;
        pedalFrame.add(compressorToggle, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        pedalFrame.add(new JLabel("Delay"), c);
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 2;
        pedalFrame.add(delaySlider, c);
        c.gridx = 3;
        c.gridy = 3;
        c.gridheight = 2;
        c.gridwidth = 1;
        pedalFrame.add(delayToggle, c);
        c.gridx = 0;
        c.gridy = 4;
        c.gridheight = 1;
        pedalFrame.add(new JLabel("Feedback"), c);
        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth = 2;
        pedalFrame.add(feedbackSlider,c);

        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 5;
        pedalFrame.add(startButton, c);

        c.gridx = 1;
        c.gridy = 5;
        pedalFrame.add(pauseButton, c);

        c.gridx = 2;
        c.gridy = 5;
        pedalFrame.add(stopButton, c);
        frame.getContentPane().add(pedalFrame);

        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 1;
        pedalFrame.add(new JLabel("Input"), c);
        c.gridx = 1;
        c.gridy = 6;
        c.gridwidth = 1;
        pedalFrame.add(inDeviceComboBox, c);

        frame.setVisible(true);
    }
    private void startAudioProcessing() {
        SwingWorker<Void, Void> audioProcessingWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    byte[] buffer = new byte[1024];

                    lineIn.start();
                    lineOut.start();

                    while (!isCancelled() && !isClosed) {
                        if(isPaused) continue;
                        System.out.println("running");
                        int bytesRead = lineIn.read(buffer, 0, buffer.length);

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

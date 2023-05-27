package audio;
import audio.advanced.MultiEffectsProcessorUI;
import audio.advanced.Oscilloscope;
import audio.basic.AudioEditorGUI;
import audio.basic.AudioFileReader;
import audio.basic.AudioMixer;
import audio.basic.MyRecord;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
public class Main extends JFrame implements ActionListener {

    JButton fileReaderButton,recordButton,mixerButton,editButton,effectButton,OscilloscopeButton;
    private AudioFileReader fileReader;
    private MyRecord record;
    private AudioMixer mixer;
    private AudioEditorGUI edit;
    private MultiEffectsProcessorUI effect;
    private Oscilloscope myScope;
    public Main() {
        setTitle("音頻基礎實現");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("music.png"));
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());

        setLayout(new BorderLayout());
        ((JPanel) getContentPane()).setOpaque(false);
        getLayeredPane().add(backgroundLabel, new Integer(Integer.MIN_VALUE));

        fileReaderButton = new JButton("讀取");
        fileReaderButton.addActionListener(this);
        fileReaderButton.setBackground(Color.CYAN);
        fileReaderButton.setPreferredSize(new Dimension(80, 100));
        recordButton = new JButton("錄音");
        recordButton.addActionListener(this);
        recordButton.setBackground(Color.CYAN);
        recordButton.setPreferredSize(new Dimension(80, 100));
        mixerButton = new JButton("混音");
        mixerButton.addActionListener(this);
        mixerButton.setBackground(Color.CYAN);
        mixerButton.setPreferredSize(new Dimension(80, 100));
        editButton = new JButton("音導裁減/合併");
        editButton.addActionListener(this);
        editButton.setBackground(Color.CYAN);
        editButton.setPreferredSize(new Dimension(120, 100));
        effectButton = new JButton("綜合效果器");
        effectButton.addActionListener(this);
        effectButton.setBackground(Color.CYAN);
        effectButton.setPreferredSize(new Dimension(100, 100));
        OscilloscopeButton = new JButton("示波器");
        OscilloscopeButton.addActionListener(this);
        OscilloscopeButton.setBackground(Color.CYAN);
        OscilloscopeButton.setPreferredSize(new Dimension(80, 100));
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(fileReaderButton);
        panel.add(recordButton);
        panel.add(mixerButton);
        panel.add(editButton);
        panel.add(effectButton);
        panel.add(OscilloscopeButton);
        getContentPane().add(panel, BorderLayout.SOUTH);
        setSize(backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fileReaderButton) {
            fileReader = new AudioFileReader();
            fileReader.readFile();
        } else if (e.getSource() == recordButton) {
            record = new MyRecord();
            record.showgui();
        } else if (e.getSource() == mixerButton) {
            mixer = new AudioMixer();
            //mixer.mixAudio();
        }
        else if(e.getSource()==editButton){
            edit=new AudioEditorGUI();
            edit.run();
        }
        else if(e.getSource()==effectButton){
            effect=new MultiEffectsProcessorUI();
        }
        else if(e.getSource()==OscilloscopeButton){
            myScope = new Oscilloscope();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }
}


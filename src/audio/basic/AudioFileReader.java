package audio.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class AudioFileReader {
    private Clip audioClip;
    private boolean isPlaying = false;

    public void readFile() {
        AudioFileReader audioPlayer = new AudioFileReader();
        audioPlayer.createGUI();
    }

    public void createGUI() {
        JFrame frame = new JFrame("Audio Player");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400, 200);

        JButton openButton = new JButton("Open File");
        openButton.setBackground(Color.CYAN);
        openButton.setForeground(Color.WHITE);
        JButton pauseButton = new JButton("Pause");
        pauseButton.setBackground(Color.CYAN);
        pauseButton.setForeground(Color.WHITE);
        JButton resumeButton = new JButton("Resume");
        resumeButton.setBackground(Color.CYAN);
        resumeButton.setForeground(Color.WHITE);
        JButton stopButton = new JButton("Stop");
        stopButton.setBackground(Color.CYAN);
        stopButton.setForeground(Color.WHITE);

        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pauseAudio();
            }
        });

        resumeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resumeAudio();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopAudio();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);
        buttonPanel.add(stopButton);

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File audioFile = fileChooser.getSelectedFile();
            playAudio(audioFile.getAbsolutePath());
        }
    }

    public void playAudio(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
            audioClip.start();
            isPlaying = true;
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            System.out.println("Error playing audio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void pauseAudio() {
        if (isPlaying) {
            audioClip.stop();
            isPlaying = false;
        }
    }

    public void resumeAudio() {
        if (!isPlaying) {
            audioClip.start();
            isPlaying = true;
        }
    }

    public void stopAudio() {
        if (isPlaying) {
            audioClip.stop();
            audioClip.close();
            isPlaying = false;
        }
    }

    public static void main(String[] args) {
        AudioFileReader audioPlayer = new AudioFileReader();
        audioPlayer.readFile();
    }
}

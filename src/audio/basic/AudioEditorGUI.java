package audio.basic;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class AudioEditorGUI extends JFrame {
    private File choosefile ;
    private JButton mergeButton;
    private JButton segmentButton;
    private JButton playButton;
    private JButton pauseButton;
    private JButton cButton;
    private JLabel statusLabel;
    private Clip audioClip;
    private boolean isPaused = false;
    public AudioEditorGUI() {
        setTitle("Audio Editor");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 創建ui
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        mergeButton = new JButton("MERGE MUSIC");
        segmentButton = new JButton("CUT MUSIC");
        playButton = new JButton("PLAY");
        pauseButton = new JButton("PAUSE");
        statusLabel = new JLabel(" ");
        cButton = new JButton("choose file");

        JLabel startTimeLabel = new JLabel("START TIME (sec)");
        JLabel endTimeLabel = new JLabel("END TIME (sec)");
        JTextField startTimeField = new JTextField();
        JTextField endTimeField = new JTextField();

        // 設置組件樣式
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        startTimeLabel.setFont(labelFont);
        endTimeLabel.setFont(labelFont);
        startTimeField.setFont(labelFont);
        endTimeField.setFont(labelFont);
        statusLabel.setFont(labelFont);

        mergeButton.setPreferredSize(new Dimension(150, 30));
        segmentButton.setPreferredSize(new Dimension(150, 30));
        playButton.setPreferredSize(new Dimension(150, 30));
        pauseButton.setPreferredSize(new Dimension(150, 30));
        cButton.setPreferredSize(new Dimension(150, 30));
        

        // 添加組件到面板
        panel.add(mergeButton);
        panel.add(segmentButton);
        panel.add(startTimeLabel);
        panel.add(startTimeField);
        panel.add(endTimeLabel);
        panel.add(endTimeField);
        panel.add(playButton);
        panel.add(pauseButton);
        panel.add(statusLabel);
        panel.add(cButton);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 添加面板到窗口
        add(panel);

        // 觸發按鈕
        mergeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);
                int result = fileChooser.showOpenDialog(AudioEditorGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] files = fileChooser.getSelectedFiles();
                    mergeAudioFiles(files);
                }
            }
        });

        segmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                    
                    double startTime = Double.parseDouble(startTimeField.getText());
                    double endTime = Double.parseDouble(endTimeField.getText());
                    segmentAudioFile(choosefile, startTime, endTime);
                
            }
        });

        //choose
        cButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(AudioEditorGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    choosefile = fileChooser.getSelectedFile();
                    
                }
            }
        });

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playAudio();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseAudio();
            }
        });
    }

    /**
     * merge多個音檔
     *
     * @param files 要合并的音檔文件數
     */
    private void mergeAudioFiles(File[] files) {
        if (files.length < 2) {
            statusLabel.setText("AT LEAST CHOOSE 2");
            return;
        }

        List<AudioInputStream> audioStreams = new ArrayList<>();

        try {
            // 打開每個音頻文件並將其添加到音頻流列表中
            for (File file : files) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                audioStreams.add(audioStream);
            }

            // 獲取第一個音頻流的格式作為合併後音頻的格式
            AudioFormat format = audioStreams.get(0).getFormat();

            // 創建合併後的音頻流
            Enumeration<AudioInputStream> audioEnum = new Enumeration<AudioInputStream>() {
                private final Iterator<AudioInputStream> iterator = audioStreams.iterator();

                @Override
                public boolean hasMoreElements() {
                    return iterator.hasNext();
                }

                @Override
                public AudioInputStream nextElement() {
                    return iterator.next();
                }
            };
            SequenceInputStream mergedStream = new SequenceInputStream(audioEnum);

            // 將合併後的音頻流寫入輸出文件
            String mergeout = "mergeoutput.wav";
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = mergedStream.read(buffer, 0, buffer.length)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            // 創建音頻輸入流
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            AudioInputStream mergedAudioStream = new AudioInputStream(byteArrayInputStream, format,
                    byteArrayOutputStream.size() / format.getFrameSize());

            // 保存合併後的音頻文件
            AudioSystem.write(mergedAudioStream, AudioFileFormat.Type.WAVE, new File(mergeout));
            statusLabel.setText("merge success");

            // close
            mergedAudioStream.close();
            byteArrayOutputStream.close();
            byteArrayInputStream.close();

            // close
            mergedStream.close();
            for (AudioInputStream audioStream : audioStreams) {
                audioStream.close();
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed");
        }
    }

    /**
     * 裁切音频文件
     *
     * @param inputFile  要裁切的音频文件
     * @param startTime 
     * @param endTime    
     */
    private void segmentAudioFile(File inputFile, double startTime, double endTime) {
        String segmentout = "segmentoutput.wav";
        
        try {
            // 讀取
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputFile);
            AudioFormat format = audioStream.getFormat();

            // 計算裁切位置
            long startFrame = Math.round(startTime * format.getFrameRate());
            long endFrame = Math.round(endTime * format.getFrameRate());

            // 計算裁切后的時長
            long frameCount = endFrame - startFrame;

            audioStream.skip(startFrame * format.getFrameSize());

            // 建立裁切后的stream
            AudioInputStream segmentedStream = new AudioInputStream(
                    new AudioInputStream(audioStream, format, frameCount),
                    format, frameCount);

            // 寫入輸出文件
            AudioSystem.write(segmentedStream, AudioFileFormat.Type.WAVE, new File(segmentout));

            // close
            segmentedStream.close();
            audioStream.close();

            statusLabel.setText("segment success");
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed");
        }
    }

    /**
     * 播放音频文件
     */
    private void playAudio() {
        try {
            
            JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(AudioEditorGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File inputFile = fileChooser.getSelectedFile();
                    audioClip = AudioSystem.getClip();
                    audioClip.open(AudioSystem.getAudioInputStream(inputFile));
                    audioClip.start();
                     statusLabel.setText("Playing...");
                }
           
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to play");
        }
    }

    /**
     * 暂停播放音频文件
     */
    private void pauseAudio() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
            isPaused = true;
            statusLabel.setText("Paused");
            mergeButton.setEnabled(true); // 允許merge按钮
            segmentButton.setEnabled(true); // 允許cut按钮
            pauseButton.setText("Resume"); // 按钮設置為"Resume"
        } else if (audioClip != null && isPaused) {
            audioClip.start();
            isPaused = false;
            statusLabel.setText("Playing...");
            mergeButton.setEnabled(false); // 禁用merge按钮
            segmentButton.setEnabled(false); // 禁用cut按钮
            pauseButton.setText("Pause"); // 設為"Pause"
        }
    }
    

    public void run() {
        new AudioEditorGUI().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AudioEditorGUI().setVisible(true);
            }
        });
    }
}

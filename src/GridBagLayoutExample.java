import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GridBagLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("GridBagLayout Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // 创建两个按钮
        JButton button1 = new JButton("Button 1");
        JButton button2 = new JButton("Button 2");

        // 创建并设置GridBagConstraints对象
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // 第一个按钮的权重为1
        panel.add(button1, gbc);

        gbc.weightx = 2.0; // 第二个按钮的权重为2
        panel.add(button2, gbc);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}

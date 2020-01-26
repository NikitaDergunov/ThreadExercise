import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LabJava extends Panel {
    private Thread t1 = null;
    private Thread t2 = null;
    private String str = "         Synchronised threads";
    private Can can;
    private JCheckBox checkBox;
    private Panel panel;
    private int X = 0, Y = 0;
    private int napr = 0;
    private boolean valueFlag = false;
    static boolean stopFlag = false;
    private boolean select = false;
    private LabJava(){
        setLayout(new BorderLayout());
        panel = new Panel();
        can = new Can();
        can.setBackground(Color.GRAY);
        panel.setBackground(Color.green);
        add(panel, BorderLayout.NORTH);
        add(can, BorderLayout.CENTER);
        checkBox = new JCheckBox("Stop or start",select);
        panel.add(checkBox);
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(select == true){select = false; can.stop();}
                else {select = true; can.start();}
            }
        });

    }
    class Can extends Canvas{
        @Override
        public void update(Graphics g) {
            paint(g);
        }

        @Override
        public void paint(Graphics g) {
            Image buffer = createImage(getWidth(), getHeight());
            Graphics2D g2d = (Graphics2D)buffer.getGraphics();
            FontMetrics fm = getFontMetrics(getFont());
            Dimension d = getSize();
            int h = d.height;
            int w = d.width;
            Font font = new Font("TimesRoman", Font.BOLD, 16);
            g2d.setFont(font);
            g2d.setColor(Color.ORANGE);
            g2d.drawString(str, ((w - fm.stringWidth(str))/2)-40, (h + fm.getMaxAscent() - fm.getMaxDescent()) / 2);
            g2d.fillOval(X, Y, 20, 20);
            g2d.setColor(Color.black);
            g2d.drawOval(X, Y, 20, 20);
            g.drawImage(buffer, 0, 0, null);

        }

        void start(){
             stopFlag = false;
            t1 = new Thread(new Draw());
            t2 = new Thread(new Draw1());
            t1.start();
            t2.start();
         }
        void stop(){
            stopFlag = true;
            t1 = null;
            t2 = null;
        }
        synchronized void ball() throws InterruptedException {
            if (!valueFlag) wait();
            if(napr == 0){
                X++;
                if(X == getSize().width - 20)
                    napr = 1;
            }
            if(napr == 1){
                Y++;
                if(Y == getSize().height - 20)
                    napr = 2;
            }
            if(napr == 2){
                X--;
                if(X == 0)
                    napr = 3;
            }
            if(napr == 3){
                Y--;
                if(Y == 0)
                    napr = 0;
            }
            if(napr == 2 || napr == 0){
                valueFlag = false;
                notify();
            }

        }
        synchronized void line() throws InterruptedException {
            if (valueFlag) wait();
            char ch;
            if(napr == 0){
                ch = str.charAt(str.length() -1);
                str = str.substring(0,str.length() - 1);
                str = ch + str;
            }
            if(napr == 2) {
                ch = str.charAt(0);
                str = str.substring(1);
                str += ch;
            }
            valueFlag = true;
            notify();
        }
        class Draw extends Thread{
            public void run(){
                while(!stopFlag) {
                    try {
                        Thread.sleep(10);
                        repaint();
                        ball();
                    } catch (InterruptedException e) {
                        return;
                    }
                }}
        }
        class Draw1 extends Thread{
            public void run(){
                while(!stopFlag) {
                    try {
                        Thread.sleep(10);
                        repaint();
                        line();
                    } catch (InterruptedException e) {
                        return;
                    }
                }}
        }
    }
    // Точка хода в программу
    public static void main(String[] args) {
        LabJava panel = new LabJava();
        Frame frame = new Frame("An AWT-Based Application");
        frame.add(panel);
        frame.setSize(450, 350);
        frame.setLocation(100, 100);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter(){
            public void windowIconified(WindowEvent e){
                stopFlag = true;
            }
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
    }
}

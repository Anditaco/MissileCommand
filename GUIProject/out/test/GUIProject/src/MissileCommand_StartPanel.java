import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MissileCommand_StartPanel extends JPanel {
    JFrame parentFrame;
    Image bgImage;
    JButton startButton;

    @Override
    protected void paintComponent(Graphics graphics) {
        setSize(parentFrame.getSize());
        startButton.setLocation(parentFrame.getWidth() / 2 - (startButton.getWidth() / 2),parentFrame.getHeight() * 7 / 10);
        super.paintComponent(graphics);
        graphics.drawImage(bgImage, 0, 0, MissileCommand_Main.mainFrame.getWidth(), MissileCommand_Main.mainFrame.getHeight() - MissileCommand_Main.topInset, 0, 0, bgImage.getWidth(null), bgImage.getHeight(null), null);
        graphics.setFont(MissileCommand_Main.MC_Font.deriveFont(75f));
        graphics.setColor(Color.WHITE);
        FontMetrics fm = graphics.getFontMetrics();
        graphics.drawString("MISSILE COMMAND", parentFrame.getWidth() / 2 - (fm.stringWidth("MISSILE COMMAND") / 2), parentFrame.getHeight() / 10 + MissileCommand_Main.topInset);

        Connection conn;
        Statement stmt;
        try{
            conn = DriverManager.getConnection(MissileCommand_Main.highScoreDBC);
            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("select * from scores order by score desc");

            graphics.setFont(MissileCommand_Main.MC_Font.deriveFont(50f));
            graphics.setColor(Color.WHITE);
            fm = graphics.getFontMetrics();

            int i = 0;
            while(rs.next() && i < 5){
                String ss = Integer.toString(i + 1) + ". " + Integer.toString(rs.getInt("score")) + " - " + rs.getString("name");
                graphics.drawString(ss, parentFrame.getWidth() / 2 - (fm.stringWidth(ss) / 2), parentFrame.getHeight() * (i+2) / 10 + MissileCommand_Main.topInset);
                i++;
            }

            stmt.close();
            conn.close();

        } catch (Exception e){
            System.out.println(e.getMessage());
        }


    }

    public MissileCommand_StartPanel(JFrame parent) {
        parentFrame = parent;
        setSize(parentFrame.getSize());

        try{
           bgImage = ImageIO.read(new File("MC_StartScreen.png"));
        }catch (Exception e){
            System.out.println(e.getMessage().toString());
        }

        final Timer buttonFlash = new Timer(250, null);
        ActionListener buttonStateChange = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(startButton.getForeground().getAlpha() > 0){
                    startButton.setForeground(new Color(255,255,255,0));
                }else{
                    startButton.setForeground(new Color(255,255,255));
                }
            }
        };
        buttonFlash.addActionListener(buttonStateChange);
        buttonFlash.start();

        startButton = new JButton("Start");
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setHorizontalTextPosition(SwingConstants.CENTER);
        startButton.setForeground(Color.WHITE);
        startButton.setFont(MissileCommand_Main.MC_Font.deriveFont(50f));

        this.add(startButton);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent actionEvent){
                MissileCommand_GamePanel.playMissileSound();
                MissileCommand_Main.gamePanel.playGame();
                MissileCommand_Main.gamePanel.playWave(1);
                setVisible(false);
            }
        });

        startButton.setVisible(true);

    }
}

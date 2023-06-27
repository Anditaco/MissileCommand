//import com.apple.eawt.Application;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.InputStream;
import java.sql.*;

import static java.awt.event.KeyEvent.VK_SPACE;

public class MissileCommand_Main {
    public static final int START_WIDTH = 266;
    public static final int START_HEIGHT = 224;
    //public static Application mc = Application.getApplication();
    public static final String highScoreDBC = "jdbc:sqlite:highscoreDB.db";
    public static Font MC_Font;

    public static int refreshRate = 10;
    public static int topInset;

    public static JFrame mainFrame;
    public static MissileCommand_GamePanel gamePanel;
    public static MissileCommand_StartPanel startPanel;
    public static MissileCommand_EndPanel endPanel;

    public static void initMainFrame(){
        mainFrame = new JFrame("Missile Command");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(START_WIDTH, START_HEIGHT);

        mainFrame.setVisible(true);

        mainFrame.setFocusable(true);
        mainFrame.requestFocus();

        topInset = mainFrame.getInsets().top;
        mainFrame.setLayout(null);
        mainFrame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if(gamePanel.inWave == true) {
                    MissileCommand_Turret tempTurret = findClosestTurretByDuration(mouseEvent);
                    Image bg = MissileCommand_GamePanel.gameComponents.get(0).icon;
                    BufferedImage tempImage = new BufferedImage(bg.getWidth(null), bg.getHeight(null), BufferedImage.TYPE_INT_RGB);
                    Graphics2D tIG = tempImage.createGraphics();
                    tIG.drawImage(bg, 0, 0, null);
                    if (tempTurret != null &&
                            ((tempImage.getRGB(mouseEvent.getX() * gamePanel.gameImage.getWidth() / mainFrame.getWidth(),
                                    mouseEvent.getY() * gamePanel.gameImage.getHeight() / mainFrame.getHeight()) == new Color(0, 10, 83).getRGB())))
                        tempTurret.fire(mouseEvent);
                }
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
            }
        });
        mainFrame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                gamePanel.setSize(mainFrame.getSize());
                endPanel.setSize(mainFrame.getSize());
                startPanel.setSize(mainFrame.getSize());

            }

            @Override
            public void componentMoved(ComponentEvent componentEvent) {

            }

            @Override
            public void componentShown(ComponentEvent componentEvent) {

            }

            @Override
            public void componentHidden(ComponentEvent componentEvent) {

            }
        });
        mainFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == VK_SPACE && endPanel.isVisible() && endPanel.state == MissileCommand_EndPanel.PanelState.DISPLAYING_SCORE){
                    endPanel.finished = true;
                }
                else if (endPanel.isVisible() && endPanel.state == MissileCommand_EndPanel.PanelState.DISPLAYING_SCORE) {
                    endPanel.addKey(keyEvent);
                }
            }
        });
    }

    public static MissileCommand_Turret findClosestTurretByDuration(MouseEvent me){
        double lowestTime = Double.MAX_VALUE;
        double tempTime;
        MissileCommand_Turret bestTurret = null;

        for(int i = 1; i < 4; i++){
            if(MissileCommand_GamePanel.gameComponents.get(i) instanceof MissileCommand_Turret){
                tempTime = ((MissileCommand_Turret) MissileCommand_GamePanel.gameComponents.get(i)).getTimeToLocation(me);
                if(tempTime < lowestTime &&
                        ((MissileCommand_Turret) MissileCommand_GamePanel.gameComponents.get(i)).getMissilesRemaining() > 0){
                    lowestTime = tempTime;
                    bestTurret = (MissileCommand_Turret) MissileCommand_GamePanel.gameComponents.get(i);
                }
            }
        }

        return bestTurret;
    }

    public static void initGamePanel(){
        gamePanel = new MissileCommand_GamePanel(mainFrame);
        gamePanel.setSize(START_WIDTH, START_HEIGHT);
        mainFrame.add(gamePanel);
        gamePanel.setVisible(false);
    }

    public static void initStartPanel(){
        startPanel = new MissileCommand_StartPanel(mainFrame);
        mainFrame.add(startPanel, BorderLayout.CENTER);
        startPanel.setVisible(true);
    }

    public static void initEndPanel(){
        endPanel = new MissileCommand_EndPanel(mainFrame);
        mainFrame.add(endPanel);
        endPanel.setVisible(false);
    }

    public static void createFont(){
        try {
            InputStream is = new FileInputStream("ARCADECLASSIC.TTF");
            MC_Font = Font.createFont(Font.TRUETYPE_FONT, is);
        }catch(Exception e){
            System.out.println(e.getMessage().toString());
        }
    }

    public static void setDockIcon(){
        /*
        try {
            mc.setDockIconImage(ImageIO.read(new File("MC_Icon2.png")));
        }catch(IOException ex){
            System.out.println("Error reading MC_Icon");
        }
        */
    }

    public static void createDatabaseIfNotExists(){
        Connection conn;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:sqlite:highscoreDB.db");
            Statement stmt = conn.createStatement();
            stmt.execute("drop table if exists Scores");
            stmt.execute("create table if not exists Scores(score integer, name text)");
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void initAllPanels(){
        initGamePanel();
        initStartPanel();
        initEndPanel();
    }

    public static void initialResize(){
        mainFrame.setSize(1064,896 + topInset);
        gamePanel.setSize(1064,896);
        startPanel.setSize(1064,896);
        endPanel.setSize(1064,896);
    }

    public static void main(String[] args){
        createDatabaseIfNotExists();
        setDockIcon();
        createFont();
        initMainFrame();
        initAllPanels();
        initialResize();
    }
}

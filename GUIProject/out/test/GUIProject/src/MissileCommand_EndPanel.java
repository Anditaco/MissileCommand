import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MissileCommand_EndPanel extends JPanel{
    JFrame parentFrame;
    int score;
    String username = "";

    Connection conn;
    Statement stmt;

    public MissileCommand_EndPanel(JFrame parent){
        parentFrame = parent;
        setSize(parentFrame.getSize());
        setBackground(Color.GREEN);
    }

    public enum PanelState{
        PLAYING_GRAPHIC,
        DISPLAYING_SCORE,
    }

    public PanelState state;

    public void takeOver(){
        username = "";
       state = PanelState.PLAYING_GRAPHIC;
        playFinalExplosion();
    }

    public void addKey(KeyEvent event) {
        char c = event.getKeyChar();
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            if (username.length() < 3)
            username += c;
            else{
                username = username.substring(0, username.length() - 1) + c;

            }
        }
        else if (c == '\n' && username.length() > 0) {
            try {
                stmt.execute("insert into Scores (score, name) values (" + score + ", '" + username + "')");
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
            setVisible(false);
            MissileCommand_Main.startPanel.setVisible(true);
        }
        repaint();
    }
    Polygon leftBound = new Polygon();
    Polygon rightBound = new Polygon();


    public void connect(){
        try {
            conn = DriverManager.getConnection(MissileCommand_Main.highScoreDBC);
            stmt = conn.createStatement();
        }catch (Exception e){
            System.out.println(e.getMessage().toString());
        }
    }

    boolean inTopFive;

    public void playFinalExplosion(){
        connect();

        final double w = parentFrame.getWidth();
        final double h = parentFrame.getHeight();
        score = MissileCommand_Main.gamePanel.score;

        inTopFive = false;
        try{
            ResultSet cs = stmt.executeQuery("select count(*) as rowcount from scores");
            cs.next();
            int count = cs.getInt("rowcount");
            if (count < 5) {
                inTopFive = true;
            }
            else {
                ResultSet rs = stmt.executeQuery("select * from scores order by score desc");
                for (int i = 0; i <= 5; i++) {
                    rs.next();
                    if (score > rs.getInt("score")) {
                        System.out.println("Score = " + rs.getInt(score));
                        inTopFive = true;
                        break;
                    }
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }


        MissileCommand_Explosion.playExplosionSound();

        final Timer exploTimer = new Timer(MissileCommand_Main.refreshRate * 4, null);
        ActionListener exploListener = new ActionListener() {
            double currentRadius = 0;
            double finalRadius = (Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2)) / 2) + 25;
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                currentRadius += 8;
                if(currentRadius > finalRadius){
                    exploTimer.stop();
                    if(inTopFive){
                        System.out.println("Score in top five");
                        state = PanelState.DISPLAYING_SCORE;
                    }else {
                        System.out.println("Score not in top five");
                        setVisible(false);
                        MissileCommand_Main.startPanel.setVisible(true);
                    }
                    repaint();
                }
                else{
                    leftBound.reset();
                    rightBound.reset();

                    leftBound.addPoint((int)(w/2.0),-15);
                    leftBound.addPoint(-15,-5);
                    leftBound.addPoint(-15,(int)h);
                    leftBound.addPoint((int)(w/2.0),(int)h);
                    for(int i = -40; i < 600; i++){
                        leftBound.addPoint((int)(w/2.0 - (Math.sin(i / 180.0) * currentRadius)), (int)(h/2.0 + (Math.cos(i / 180.0) * currentRadius)));
                    }

                    rightBound.addPoint((int)(w/2.0),-15);
                    rightBound.addPoint((int)w + 15,-5);
                    rightBound.addPoint((int)w + 15,(int)h);
                    rightBound.addPoint((int)(w/2.0),(int)h);
                    for(int i = -40; i < 600; i++){
                        rightBound.addPoint((int)(w/2.0 + (Math.sin(i / 180.0) * currentRadius)), (int)(h/2.0 + (Math.cos(i / 180.0) * currentRadius)));
                    }
                    repaint();
                }
            }
        };
        exploTimer.addActionListener(exploListener);

        exploTimer.start();
    }

    volatile boolean finished = true;

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        switch (state){
            case PLAYING_GRAPHIC:
                graphics.setColor(Color.BLACK);
                graphics.setFont(MissileCommand_Main.MC_Font.deriveFont(100f));
                FontMetrics fm = graphics.getFontMetrics();
                graphics.drawString("THE END", parentFrame.getWidth()/2 - (fm.stringWidth("THE END") / 2), parentFrame.getHeight() / 2 + MissileCommand_Main.topInset);
                graphics.fillPolygon(leftBound);
                graphics.fillPolygon(rightBound);
                break;

            case DISPLAYING_SCORE:
                setBackground(Color.GREEN);
                graphics.setColor(Color.BLACK);
                graphics.setFont(MissileCommand_Main.MC_Font.deriveFont(100f));
                String scoreString = "SCORE: " + score;
                FontMetrics f = graphics.getFontMetrics();
                graphics.drawString(scoreString, parentFrame.getWidth()/2 - (f.stringWidth(scoreString) / 2), parentFrame.getHeight() * 2 / 5 + MissileCommand_Main.topInset - (f.getHeight()/2));
                graphics.setFont(MissileCommand_Main.MC_Font.deriveFont(50f));
                graphics.drawString("Enter Name: "+username, parentFrame.getWidth()/2 - (f.stringWidth(scoreString) / 2), parentFrame.getHeight() * 3 / 5 + MissileCommand_Main.topInset - (f.getHeight()/2));
                break;

        }

    }
}

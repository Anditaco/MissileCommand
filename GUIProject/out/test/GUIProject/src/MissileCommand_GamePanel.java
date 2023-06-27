import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class MissileCommand_GamePanel extends JPanel {
    public JFrame parentFrame;
    public BufferedImage gameImage;
    public Graphics2D gIG;
    public int waveNumber;
    public int score;
    public Timer refreshTimer;

    public volatile boolean inWave;

    public static CopyOnWriteArrayList<MissileCommand_GameComponent> gameComponents = new CopyOnWriteArrayList<MissileCommand_GameComponent>();

    public MissileCommand_GamePanel(JFrame parent) {
        parentFrame = parent;
        score = 0;
    }

    public void reset(){
        System.out.println("reseting");
        clearGameComponents();
        waveNumber = 1;
        score = 0;
    }

    public void clearGameComponents(){
        for(int i = 0; i < gameComponents.size(); i++){
            gameComponents.remove(i);
            i--;
        }
    }

    public void playGame(){
        reset();
        setLocation(0,0);
        setSize(parentFrame.getSize());
        setVisible(true);

        gameImage = new BufferedImage(MissileCommand_Main.START_WIDTH, MissileCommand_Main.START_WIDTH - (2*MissileCommand_Main.topInset), BufferedImage.TYPE_INT_ARGB);
        gIG = gameImage.createGraphics();
        initAllFixedComponents();

        inWave = false;
        refreshTimer = new Timer(MissileCommand_Main.refreshRate, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateAllComponents();
                repaint();
            }
        });
        refreshTimer.start();
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                copyToDrawnImage();
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

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(inWave) {
            copyToDrawnImage();
            g.drawImage(gameImage, 0, 0, parentFrame.getWidth(), parentFrame.getHeight() - MissileCommand_Main.topInset, 0, 0, gameImage.getWidth(), gameImage.getHeight(), null);
            g.setColor(Color.WHITE);
            g.setFont(MissileCommand_Main.MC_Font.deriveFont(50f));
            g.drawString("Score: " + Integer.toString(score), 10, 15 + MissileCommand_Main.topInset);
            drawTurretValues(g);
        }
    }

    public void copyToDrawnImage() {
        for (MissileCommand_GameComponent component : gameComponents) {
            component.drawComponent(gIG);
        }
    }

    public void drawTurretValues(Graphics g){
        FontMetrics fm = g.getFontMetrics();
        for(int i = 1; i < 4; i++){
            MissileCommand_Turret temp = (MissileCommand_Turret)gameComponents.get(i);
            String missilesRemaining = Integer.toString(temp.getMissilesRemaining());
            int x = (int)(temp.getScaledCenter().getX() - (fm.stringWidth(missilesRemaining) / 2));
            g.setColor(new Color(0, 43, 255));
            g.drawString(Integer.toString(temp.getMissilesRemaining()), x, (int)temp.getScaledCenter().getY() + 20);
        }
    }

    public void updateAllComponents() {
        for (MissileCommand_GameComponent component : gameComponents) {
            component.update(System.currentTimeMillis());
        }
    }

    public void initAllFixedComponents() {
        gameComponents.add(new MissileCommand_Background(new MissileCommand_Point(0, 0), gameImage));

        gameComponents.add(new MissileCommand_Turret(new MissileCommand_Point(22, 195), gameImage, 3));
        gameComponents.add(new MissileCommand_Turret(new MissileCommand_Point(129, 195), gameImage, 5));
        gameComponents.add(new MissileCommand_Turret(new MissileCommand_Point(253, 194), gameImage, 3));

        gameComponents.add(new MissileCommand_City(new MissileCommand_Point(39, 199), gameImage));
        gameComponents.add(new MissileCommand_City(new MissileCommand_Point(67, 200), gameImage));
        gameComponents.add(new MissileCommand_City(new MissileCommand_Point(92, 201), gameImage));
        gameComponents.add(new MissileCommand_City(new MissileCommand_Point(146, 199), gameImage));
        gameComponents.add(new MissileCommand_City(new MissileCommand_Point(181, 196), gameImage));
        gameComponents.add(new MissileCommand_City(new MissileCommand_Point(210, 200), gameImage));
    }

    public MissileCommand_GameComponent getRandomTarget() {
        ArrayList<MissileCommand_FixedComponent> validTargets = new ArrayList<MissileCommand_FixedComponent>();
        for (int i = 1; i < 10; i++) {
            MissileCommand_GameComponent comp = gameComponents.get(i);
            if (comp instanceof MissileCommand_FixedComponent) {
                if (((MissileCommand_FixedComponent) comp).state == MissileCommand_FixedComponent.State.ALIVE) {
                    validTargets.add((MissileCommand_FixedComponent) comp);
                }
            }
        }
        if (validTargets.size() > 0) {
            return validTargets.get((int) (Math.random() * validTargets.size()));
        }
        return null;
    }

    public MissileCommand_Point getCenterOfTarget(MissileCommand_GameComponent comp) {
        MissileCommand_Point compLoc = comp.getCenter();
        return new MissileCommand_Point(compLoc.getX() * MissileCommand_Main.mainFrame.getWidth() / gameImage.getWidth(), compLoc.getY() * MissileCommand_Main.mainFrame.getHeight() / gameImage.getHeight());
    }

    volatile  boolean stop;
    Timer waveClock;
    Timer waitForWaveEnd;
    Thread cityCount;

    public void playWave(final int waves) {
        inWave = true;
        waveClock = new Timer(1000, null);
        ActionListener missileListener = new ActionListener() {
            int wavesSent = 0;

            public void actionPerformed(ActionEvent actionEvent) {
                fireEnemyMissiles(4);
                wavesSent++;
                if (wavesSent == waves + 1) {
                    waveClock.stop();
                }
            }
        };

        waitForWaveEnd = new Timer((MissileCommand_Main.refreshRate / 2), null);
        waitForWaveEnd.setInitialDelay(1001);
        ActionListener endListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!explosionsRemaining() && !missilesRemaining()) {
                    inWave = false;
                    waitForWaveEnd.stop();
                    if(waves != 0) {
                        addScores();
                    }
                    int w = waves + 1;
                    reviveAllTurrets();
                    if (getCitiesRemaining() > 0)
                        System.out.println("wave #" + (waves - 1) + " completed");
                        playWave(w);
                }
            }
        };

        cityCount = new Thread(){

            @Override
            public void run() {
                super.run();
                stop = false;
                while(!stop){
                    if (testCities())
                        waitForWaveEnd.stop();
                }
            }
        };

        waveClock.addActionListener(missileListener);
        waitForWaveEnd.addActionListener(endListener);

        waveClock.start();
        waitForWaveEnd.start();
        cityCount.start();

    }

    synchronized boolean testCities(){
        if (getCitiesRemaining() == 0 && stop == false) {
            stop = true;
            System.out.println("No cities found");
            freeze();
            triggerEnd();
            return true;
        }
        return false;
    }

    public void addScores() {
        addMissilesToScore();
        addCitiesToScore();
    }

    public int getCitiesRemaining() {
        int cityCount = 0;
        for (int i = 4; i < 10; i++) {
            if (((MissileCommand_City) (gameComponents.get(i))).state == MissileCommand_FixedComponent.State.ALIVE)
                cityCount++;
        }
        return cityCount;
    }

    public void addCitiesToScore() {
        score += 100 * getCitiesRemaining();
        System.out.println("Adding cities");
    }

    public void addMissilesToScore() {
            System.out.println("Adding missiles");
            score += getMissilesRemaining() * 5;
    }

    public int getMissilesRemaining() {
        int total = 0;
        for (int i = 1; i < 4; i++) {
            total += (((MissileCommand_Turret) gameComponents.get(i)).getMissilesRemaining());
        }
        return total;
    }

    public boolean explosionsRemaining() {
        boolean remaining = false;
        for (MissileCommand_GameComponent comp : gameComponents) {
            if (comp instanceof MissileCommand_Explosion) {
                remaining = true;
            }
        }
        return remaining;
    }

    public boolean missilesRemaining() {
        boolean remaining = false;
        for (MissileCommand_GameComponent comp : gameComponents) {
            if (comp instanceof MissileCommand_EnemyMissile) {
                remaining = true;
            }
        }
        return remaining;
    }

    private static final double ENEMY_SPEED = 0.25;  // Normal speed
    private static final double FAST = 2.5;

    public void fireEnemyMissiles(int missiles) {
        for (int i = 0; i < missiles; i++) {
            MissileCommand_GameComponent target = getRandomTarget();
            gameComponents.add(new MissileCommand_EnemyMissile(target, getCenterOfTarget(target), gameImage.createGraphics(), gameImage, new MissileCommand_Point(Math.random() * 266, 0), ENEMY_SPEED));
        }
    }

    public void reviveAllTurrets() {
        for (int i = 1; i < 4; i++) {
            ((MissileCommand_Turret) gameComponents.get(i)).revive();
        }
    }

    public void freeze() {
        inWave = false;
        for (MissileCommand_GameComponent comp : gameComponents) {
            comp.alive = false;
        }
    }

    public void triggerEnd(){
        MissileCommand_Main.gamePanel.setVisible(false);
        MissileCommand_Main.endPanel.setVisible(true);
        MissileCommand_Main.endPanel.takeOver();
    }

    public static void playMissileSound(){
        try
        {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File("missileLaunchEffect.wav")));
            clip.start();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
        }
    }

}

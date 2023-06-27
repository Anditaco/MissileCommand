import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MissileCommand_Turret extends MissileCommand_FixedComponent{
    private int missilesRemaining = 10;
    public double speed;
    BufferedImage parentImage;


    public void drawComponent(Graphics2D g) {

    }

    public void update(long time) {}

    public MissileCommand_Turret(MissileCommand_Point loc, BufferedImage parent, double speed){
        state = State.ALIVE;
        parentImage = parent;
        height = parent.getHeight();
        width = parent.getWidth();
        location = new MissileCommand_Point(loc);
        this.speed = speed;

        try {
            icon = ImageIO.read(new File("MC_Missile.png"));
        } catch (IOException iox) {
            System.out.println("error reading MC_Missile file");
        }
    }

    public void fire(MouseEvent mouseEvent){
        if(missilesRemaining > 0) {
            MissileCommand_GamePanel.gameComponents.add(new MissileCommand_PlayerMissile(new MissileCommand_Point(mouseEvent, MissileCommand_Main.topInset), parentImage.createGraphics(), parentImage, location, speed));
            MissileCommand_Main.gamePanel.playMissileSound();
            missilesRemaining--;
        }
    }

    public int getMissilesRemaining(){
        return missilesRemaining;
    }

    public double getTimeToLocation(MouseEvent me){
        MissileCommand_Point destination = new MissileCommand_Point(me.getX() * parentImage.getWidth() / MissileCommand_Main.mainFrame.getWidth(), me.getY() * parentImage.getHeight() / MissileCommand_Main.mainFrame.getHeight());
        double distance = location.getTrueDistance(destination);
        return distance / speed;
    }

    public MissileCommand_Point getScaledCenter() {
        return new MissileCommand_Point(location.getX() * MissileCommand_Main.mainFrame.getWidth() / MissileCommand_Main.gamePanel.gameImage.getWidth(), location.getY() * MissileCommand_Main.mainFrame.getHeight() / MissileCommand_Main.gamePanel.gameImage.getHeight());
    }

    @Override
    public MissileCommand_Point getCenter() {
        return location;
    }

    public void setMissilesRemaining(int missilesRemaining) {
        this.missilesRemaining = missilesRemaining;
    }

    @Override
    public void destroy() {
        super.destroy();
        setMissilesRemaining(0);
    }

    @Override
    public void revive() {
        super.revive();
        setMissilesRemaining(10);
    }
}
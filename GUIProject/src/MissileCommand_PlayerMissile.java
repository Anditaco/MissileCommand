import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MissileCommand_PlayerMissile extends MissileCommand_Missile{

    public MissileCommand_PlayerMissile(MissileCommand_Point me, Graphics2D parent, BufferedImage parentI, MissileCommand_Point ori, double speed){
        alive = true;
        state = MissleState.MOVING;
        parentGraphics = parent;
        this.speed = speed;
        origin = new MissileCommand_Point(ori);
        destination = new MissileCommand_Point(me.getX() * parentI.getWidth() / MissileCommand_Main.mainFrame.getWidth(), me.getY() * parentI.getHeight() / MissileCommand_Main.mainFrame.getHeight());
        vector = new MissileCommand_Vector(Math.atan2(destination.getY() - origin.getY(), destination.getX() - origin.getX()), speed, origin, "vector");
        line = new MissileCommand_Vector(Math.atan2(destination.getY() - origin.getY(), destination.getX() - origin.getX()), 0, origin, "line");
        lastUpdateTime = System.currentTimeMillis();

        try {
            icon = ImageIO.read(new File("MC_target.png"));
        } catch (IOException iox) {
            System.out.println("error reading target file");
        }
    }

    public void drawComponent(Graphics2D g) {
        if(alive){
            super.drawComponent(g);
            g.drawImage(icon, (int)destination.getX() - (icon.getWidth(null) / 2), (int)destination.getY() - (icon.getHeight(null) / 2), null);

            g.setColor(Color.GREEN);
            g.drawLine((int)line.getOrigin().getX(), (int)line.getOrigin().getY(), (int)line.getTip().getX(), (int)line.getTip().getY());
        }
    }

    public void ifGone(){
        alive = false;
        MissileCommand_GamePanel.gameComponents.remove(this);
        MissileCommand_GamePanel.gameComponents.add(new MissileCommand_Explosion(line.getTip(), new Color(255, 108, 36)));
    }
}

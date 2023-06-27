import java.awt.*;
import java.awt.image.BufferedImage;

public class MissileCommand_EnemyMissile extends MissileCommand_Missile{

    MissileCommand_GameComponent target;

    public MissileCommand_EnemyMissile(MissileCommand_GameComponent target, MissileCommand_Point dest, Graphics2D parent, BufferedImage parentI, MissileCommand_Point ori, double speed){
        alive = true;
        this.target = target;
        state = MissleState.MOVING;
        parentGraphics = parent;
        this.speed = speed;
        origin = new MissileCommand_Point(ori);
        destination = new MissileCommand_Point(dest.getX() * parentI.getWidth() / MissileCommand_Main.mainFrame.getWidth(), dest.getY() * parentI.getHeight() / MissileCommand_Main.mainFrame.getHeight());
        vector = new MissileCommand_Vector(Math.atan2(destination.getY() - origin.getY(), destination.getX() - origin.getX()), speed, origin, "vector");
        line = new MissileCommand_Vector(Math.atan2(destination.getY() - origin.getY(), destination.getX() - origin.getX()), 0, origin, "line");
        lastUpdateTime = System.currentTimeMillis();
    }

    public void drawComponent(Graphics2D g) {
        if(alive){
            super.drawComponent(g);
            g.setColor(Color.RED);
            g.drawLine((int)line.getOrigin().getX(), (int)line.getOrigin().getY(), (int)line.getTip().getX(), (int)line.getTip().getY());
        }
    }

    @Override
    public void handelDestination() {
        super.handelDestination();
        if(target instanceof MissileCommand_FixedComponent){
            ((MissileCommand_FixedComponent)target).destroy();
        }
    }

    public void ifGone(){
        alive = false;
        MissileCommand_GamePanel.gameComponents.remove(this);
        MissileCommand_GamePanel.gameComponents.add(new MissileCommand_Explosion(line.getTip(), new Color(142, 255,0)));
    }
}

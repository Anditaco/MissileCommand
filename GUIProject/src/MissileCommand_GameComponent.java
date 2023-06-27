import java.awt.*;

public abstract class MissileCommand_GameComponent {
    public Graphics2D parentGraphics;
    public Image icon;
    int width;
    int height;
    MissileCommand_Point location;
    boolean alive;

    long lastUpdateTime;
    long currentUpdateTime;

    public abstract void update(long time);

    public void drawComponent(Graphics2D g) {
        g.drawImage(icon, (int)location.getX(), (int)location.getY(),null);
    }

    public MissileCommand_Point getCenter(){
        double x = location.getX();
        x += (icon.getWidth(null) / 2);
        return new MissileCommand_Point(x, location.getY() + (icon.getHeight(null)/2));
    }

}
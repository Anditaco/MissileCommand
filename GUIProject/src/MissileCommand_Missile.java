import java.awt.*;

public class MissileCommand_Missile extends MissileCommand_GameComponent{
    MissileCommand_Point origin;
    MissileCommand_Point destination;
    double speed;
    MissileCommand_Vector line;
    MissileCommand_Vector vector;

    public MissileCommand_Vector getLine() {
        return line;
    }

    enum MissleState {
        MOVING,
        AT_DESTINATION,
        GONE
    }

    MissleState state;

    public void update(long time) {
        if (alive) {
            currentUpdateTime = time;
            double delta = (currentUpdateTime - lastUpdateTime) / MissileCommand_Main.refreshRate;
            switch (state) {
                case MOVING:
                    if (delta != 0) {
                        line.addMagnitude(vector.multiply(delta));
                        lastUpdateTime = currentUpdateTime;
                    }
                    if (getTrueDistance(line.getTip(), line.getOrigin()) >= getTrueDistance(destination, line.getOrigin())) {
                        line.setTip(destination);
                        state = MissleState.AT_DESTINATION;
                    }
                    break;

                case AT_DESTINATION:
                    handelDestination();
                    break;

                case GONE:
                    ifGone();
                    break;
            }
        }
    }

    public void ifGone(){
        alive = false;
        MissileCommand_GamePanel.gameComponents.remove(this);
        MissileCommand_GamePanel.gameComponents.add(new MissileCommand_Explosion(destination, new Color(0,0,0)));
    }

    public void drawComponent(Graphics2D g) {
        g.setColor(Color.WHITE);
        Point tip = line.getTip().toPoint();
        g.drawLine((int)tip.getX(), (int)tip.getY(),(int)tip.getX(), (int)tip.getY());
    }

    public MissileCommand_Missile(){}

    @Override
    public MissileCommand_Point getCenter() {
        return line.getTip();
    }

    public void handelDestination(){
        state = MissleState.GONE;
    }

    public double getTrueDistance(MissileCommand_Point p1, MissileCommand_Point p2){
        return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
    }
}
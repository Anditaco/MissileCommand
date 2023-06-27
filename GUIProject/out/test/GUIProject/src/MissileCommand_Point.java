import java.awt.*;
import java.awt.event.MouseEvent;

public class MissileCommand_Point {
    double x,y;

    public MissileCommand_Point(double X, double Y){
        x = X;
        y = Y;
    }

    public MissileCommand_Point(MissileCommand_Point p){
        x = p.getX();
        y = p.getY();
    }

    public MissileCommand_Point(Point p){
        x = p.getX();
        y = p.getY();
    }

    public void setLocation(double X, double Y){
        x = X;
        y = Y;
    }

    public MissileCommand_Point(MouseEvent me, int topInset){
        setLocation(me.getX(), me.getY() - (topInset / 2));
    }

    public Point toPoint(){
        return new Point((int)this.getX(), (int)this.getY());
    }

    public double getX(){
        return x;
    }

    public double getY() {
        return y;
    }

    public double getTrueDistance(MissileCommand_Point p2){
        return Math.sqrt(Math.pow(p2.getX() - this.getX(), 2) + Math.pow(p2.getY() - this.getY(), 2));
    }
}

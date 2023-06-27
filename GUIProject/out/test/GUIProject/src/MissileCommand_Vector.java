public class MissileCommand_Vector {
    public double theta;
    public double magnitude;
    public MissileCommand_Point origin;
    public String name;

    public MissileCommand_Vector(double t, double m, MissileCommand_Point o, String n){
        theta = t;
        magnitude = m;
        origin = new MissileCommand_Point(o);
        name = n;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public MissileCommand_Point getOrigin() {
        return origin;
    }

    public MissileCommand_Point getTip(){
        double xVel = Math.cos(theta) * magnitude;
        double yVel = Math.sin(theta) * magnitude;

        return new MissileCommand_Point((origin.getX() + xVel), (origin.getY() + yVel));
    }

    public void setTip(MissileCommand_Point newTip){
        double x = newTip.getX() - origin.getX();
        double y = newTip.getY() - origin.getY();

        magnitude = Math.sqrt(Math.pow(x,2) + Math.pow(y, 2));
        theta = Math.atan2(y, x);
    }

    public void addMagnitude(MissileCommand_Vector vec){
        this.setMagnitude(getMagnitude()+vec.getMagnitude());
    }

    public MissileCommand_Vector multiply(double factor){
        MissileCommand_Vector temp = new MissileCommand_Vector(theta,magnitude,origin, name);
        temp.setMagnitude(temp.getMagnitude()*factor);
        return temp;
    }

}

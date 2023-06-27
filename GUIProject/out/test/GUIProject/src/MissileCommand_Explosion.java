import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;

public class MissileCommand_Explosion extends MissileCommand_GameComponent{

    public long startTime;
    public long endTime;

    public double diameter;
    public static double finalDiameter;

    private Color explosionColor;

    Polygon explosion = new Polygon();

    enum ExplosionState {
        EXPLODING,
        EXPLODED,
        GONE
    }

    ExplosionState state;

    @Override
    public void drawComponent(Graphics2D g) {
        if(alive) {
            g.setColor(new Color(explosionColor.getRed(), explosionColor.getGreen(), explosionColor.getBlue(), 255 - (int)(Math.pow((diameter / finalDiameter), 10) * 255)));
            g.fillPolygon(explosion);
        }
    }

    @Override
    public void update(long time) {
        currentUpdateTime = time;
        switch (state){
            case EXPLODING:
                diameter = (currentUpdateTime - startTime) / (double) (endTime - startTime) * finalDiameter;
                if(diameter >= finalDiameter){
                    state = ExplosionState.EXPLODED;
                    diameter = finalDiameter;
                }
                refreshExplosion();
                break;
            case EXPLODED:
                state = ExplosionState.GONE;
                break;
            case GONE:
                alive = false;
                MissileCommand_GamePanel.gameComponents.remove(this);
                break;
        }
        lastUpdateTime = time;

        for(int i = 0; i < MissileCommand_GamePanel.gameComponents.size(); i++){
            if(MissileCommand_GamePanel.gameComponents.get(i) instanceof MissileCommand_EnemyMissile){
                MissileCommand_Missile comp = (MissileCommand_Missile)MissileCommand_GamePanel.gameComponents.get(i);
                Point compLoc = comp.getLine().getTip().toPoint();
                if(explosion.contains(compLoc)){
                    comp.ifGone();
                    i--;
                }
            }
        }
    }

    public void refreshExplosion(){
        explosion.reset();
        for(int i = 0; i < 100; i++){
            explosion.addPoint((int)(location.getX() - (Math.sin(0.02*Math.PI*i) * diameter / 2)), (int)(location.getY() - (Math.cos(0.02*Math.PI*i) * diameter / 2)));
        }
    }

    public MissileCommand_Explosion(MissileCommand_Point origin, Color c){
        playExplosionSound();
        startTime = System.currentTimeMillis();
        state = ExplosionState.EXPLODING;
        endTime = startTime + 1000;
        lastUpdateTime = startTime;
        alive = true;
        location = new MissileCommand_Point(origin);
        explosionColor = c;
        finalDiameter = 27;
    }
    public static void playExplosionSound(){
        try
        {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File("abomb_mc (1).wav")));
            clip.start();
        }
        catch (Exception exc)
        {
            exc.printStackTrace(System.out);
        }
    }
}
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MissileCommand_City extends MissileCommand_FixedComponent {

    public void update(long time) {}

    public MissileCommand_City(MissileCommand_Point loc, BufferedImage parent) {
        alive = true;
        state = State.ALIVE;
        width = parent.getWidth();
        height = parent.getHeight();
        location = new MissileCommand_Point(loc);

        try {
            icon = ImageIO.read(new File("MC_City.png"));
        } catch (IOException iox) {
            System.out.println("error reading MC_City file");
        }
    }

    @Override
    public void revive() {
        super.revive();
        try {
            icon = ImageIO.read(new File("MC_City.png"));
        } catch (IOException iox) {
            System.out.println("error reading MC_City file");
        }
    }

    public void destroy(){
        super.destroy();
        try {
            icon = ImageIO.read(new File("MC_BrokenCity.png"));
        } catch (IOException iox) {
            System.out.println("error reading MC_BrokenCity file");
        }
    }
}
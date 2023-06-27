import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MissileCommand_Background extends MissileCommand_GameComponent{

    public MissileCommand_Background(MissileCommand_Point loc, BufferedImage parent){
        width = parent.getWidth();
        height = parent.getHeight();
        location = new MissileCommand_Point(loc);

        try{
            icon = ImageIO.read(new File("MC_gameBackground_Final.png"));
        }catch(IOException iox){
            System.out.println("error reading gameBackground file");
        }
    }

    public void update(long time) {}
}

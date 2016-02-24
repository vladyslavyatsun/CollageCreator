package collage.controller.collage;


import collage.entity.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;

public class ImageCreator {

    private BufferedImage drawCollage(List<Image> images, int width, int height) {
        // Create a white image to draw the collage on.
        BufferedImage img =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = img.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);

        Random r = new Random();

        // Shrink the image, then rotate it, and draw it on the collage image.
        for (Image filename : images) {

            BufferedImage image = null;
            try {
                image = ImageIO.read(new URL(filename.getImgUrl()));
            } catch (IOException e) {
                continue;
            }


            g.drawImage(image, filename.getX(), filename.getY(), filename.getWidth(), filename.getHeight(), null);
        }
        g.dispose();
        return img;
    }

    private static Random r = new Random();

    public String  getImgUrl(List<Image> images, int width, int height) throws IOException {
        BufferedImage image = drawCollage(images, width, height);
        File file = new File("images/img" + r.nextInt() + ".png");
        ImageIO.write(image, "png", file);
        return file.getAbsolutePath();
    }


}

package collage.controller;

import collage.entity.Image;

import java.util.Collections;
import java.util.List;

public class ImagePropertyDefault implements ImageProperty {

    public ImagePropertyDefault() {
    }

    @Override
    public void setImagesProperty(List<Image> images, int width, int height) {
        Collections.shuffle(images);
        int x = (int) Math.ceil(Math.sqrt(1f * images.size() * width / height));
        int y = (int) Math.ceil(1f * images.size() / x);
        int size;
        try {
            size = Math.min(width / x, height / y);
        } catch (Exception e){
            size = 100;
        }
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Image image;
                if ((i * y + j) < images.size()) {
                    image = images.get(i * y + j);
                } else {
                    Image rand = images.get((int) (Math.random() * images.size()));
                    image = new Image(rand.getImgUrl(), rand.getPostCount());
                    images.add(image);
                }
                image.setX(size * i);
                image.setY(size * j);
                image.setHeight(size);
                image.setWidth(size);
        }
    }
}

}

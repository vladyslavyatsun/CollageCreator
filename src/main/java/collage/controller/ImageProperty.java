package collage.controller;

import collage.entity.Image;

import java.util.List;


public interface ImageProperty {
    void setImagesProperty(List<Image> images, int width, int height);
}

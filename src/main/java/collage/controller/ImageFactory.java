package collage.controller;

import collage.controller.impl.Twitter4jParser;
import collage.entity.Image;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.*;
import java.util.stream.Collectors;

public class ImageFactory {

    static final double MAX_PERCENT = 0.3;
    @Autowired
    private Twitter4jParser parser;
    private final static Logger logger = Logger.getLogger(ImageFactory.class);


    public List<Image> getUserImages(String login, int width, int height, ImageProperty imageProperty) throws TwitterException {
        List<Long> userId = parser.getFollowers(login);
        HashMap<String, Integer> hashMap = parser.getImagesMap(userId);
        List<Image> images = hashMap.keySet().stream().
                map(key -> new Image(key, hashMap.get(key)))
                .sorted((i1, i2) -> i1.getPostCount() - i2.getPostCount())
                .collect(Collectors.toList());
        setImagesProperty(images, width, height, imageProperty);
        return images;
    }

    protected void setImagesProperty(List<Image> images, int width, int height, ImageProperty imageProperty) {
        imageProperty.setImagesProperty(images, width, height);
    }

    public User check(String login) throws TwitterException {
        return parser.getUser(login);
    }
}



package collage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import collage.controller.ImageFactory;
import collage.controller.ImageProperty;
import collage.controller.collage.ImageCreator;
import collage.entity.Image;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import twitter4j.TwitterException;
import twitter4j.User;

import javax.servlet.http.HttpServletResponse;


@Controller
public class MainController {

    @Autowired
    private ImageFactory imageFactory;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ImageCreator imageCreator;

    private final static Logger logger = Logger.getLogger(MainController.class);


    @RequestMapping(value = "/makeCollage", method = RequestMethod.GET)
    public String showImages(
            @RequestParam(value = "login", required = false, defaultValue = "") String login,
            @RequestParam(value = "width", required = false, defaultValue = "1000") int width,
            @RequestParam(value = "height", required = false, defaultValue = "800") int height,
            @RequestParam(value = "selectProp", required = false, defaultValue = "0") int builder,
            Map<String, Object> map) {
        if (width < 0 || height < 0 || width > 10000 || height > 10000) {
            map.put("errorMessage", "Please write normal height or width");
            return "error";
        }
        if (logger.isDebugEnabled()) {
            logger.debug("login:\"" + login + "\" width=" + width + ", height=" + height + " builder=" + builder);
        }
        User user;
        try {
            user = imageFactory.check(login);
            ImageProperty imageProperty = (ImageProperty) applicationContext.getBean(ConfigProperty.PROP[builder]);
            List<Image> images;
            map.put("images", images = imageFactory.getUserImages(login, width, height, imageProperty));
//            String bufferedImage = imageCreator.getImgUrl(images, width, height);
//            map.put("imgSrc", bufferedImage);
            if (logger.isDebugEnabled()) {
                logger.debug("login:\"" + login + "\" image size =" + images.size());
            }
        } catch (TwitterException /*| IOException*/ e) {
            logger.error(e);
            if (e instanceof TwitterException) {
                if (((TwitterException) e).getErrorMessage().equals("Rate limit exceeded"))
                    map.put("errorMessage", "Rate limit, please wait " + ((TwitterException) e).getRateLimitStatus().getSecondsUntilReset() / 60 + ":" + ((TwitterException) e).getRateLimitStatus().getSecondsUntilReset() % 60);
                else if (((TwitterException) e).getErrorMessage().equals("Sorry, that page does not exist."))
                    map.put("errorMessage", "login:\"" + login + "\" not found");
                else {
                    map.put("errorMessage", "Sorry, error");
                }
            }
            return "error";
        }
        map.put("collageW", width);
        map.put("collageH", height);
        map.put("user", user);
        return "viewImage";
    }

    @RequestMapping(value = "/index")
    public String redirect() {
        return "index";
    }

    @RequestMapping(value = "/")
    public String ef() {
        return "index";
    }


}
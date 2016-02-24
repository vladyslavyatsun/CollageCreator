package collage;

import collage.controller.ImageFactory;
import collage.controller.ImageProperty;
import collage.controller.ImagePropertyDefault;
import collage.controller.RandomImageProperty;
import collage.controller.collage.Canvas;
import collage.controller.collage.ImageCreator;
import collage.controller.impl.Twitter4jParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

    @Bean
    public ImageFactory imageFactory(){
        return new ImageFactory();
    }

    @Bean
    public Twitter4jParser parser(){
        return new Twitter4jParser(ConfigProperty.POOL_SIZE);
    }

    @Bean(name = "defaultImageProp")
    public ImageProperty defaultImageProperty(){
        return new ImagePropertyDefault();
    }

    @Bean(name = "randomImageProp")
    public ImageProperty randomImageProperty(){
        return new RandomImageProperty();
    }

    @Bean
    public Canvas canvas(){
        return new Canvas();
    }

    @Bean
    public ImageCreator imageCreator() {
        return new ImageCreator();
    }
}

package collage.controller.check;

import twitter4j.Status;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;


public class CheckFactory {

//    private HashMap<String, >

    public String getKeyString(){
        return UUID.randomUUID().toString();
    }

    public Status getStatus(String key){
        return null;
    }

}
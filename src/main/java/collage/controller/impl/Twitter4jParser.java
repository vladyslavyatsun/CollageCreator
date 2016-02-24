package collage.controller.impl;

import org.apache.log4j.*;
import twitter4j.*;
import twitter4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Twitter4jParser {

    private Twitter twitter;
    private ExecutorService service;
    private int poolSize;
    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Twitter4jParser.class);

    public Twitter4jParser(int poolSize) {
        this.twitter = new TwitterFactory().getInstance();
        service = Executors.newFixedThreadPool(poolSize);
        this.poolSize = poolSize;
    }


    public List<Long> getFollowers(Long userId) throws TwitterException {
        return getFollowers(new FollowersInterface() {
            @Override
            public IDs next() {
                IDs iDs;
                try {
                    iDs = twitter.getFriendsIDs(userId, cursor);
                } catch (TwitterException e) {
                    return null;
                }
                cursor = iDs.getNextCursor();
                return iDs;
            }
        });
    }


    public List<Long> getFollowers(String login) throws TwitterException {
        return getFollowers(new FollowersInterface() {
            @Override
            public IDs next() throws TwitterException {
                if (cursor == 0) return null;
                IDs iDs = null;
                try {
                    iDs = twitter.getFriendsIDs(login, cursor);
                    cursor = iDs.getNextCursor();
                } catch (TwitterException e) {
                    throw e;
                }
                return iDs;
            }
        });
    }

    private List<Long> getFollowers(FollowersInterface fol) throws TwitterException {
        List<Long> res = new LinkedList<>();
        IDs iDs;
        try {
            while ((iDs = fol.next()) != null)
                for (long id : iDs.getIDs()) {
                    res.add(id);
                }

        } catch (TwitterException e) {
            throw e;
        }
        return res;
    }


    public HashMap<String, Integer> getImagesMap(List<Long> usersId) throws TwitterException {
        List<Handler> handlers = new LinkedList<>();
        CountHandler countHandler = new CountHandler();
        for (int i = 0; i < poolSize; i++) {
            Handler handler = new Handler(usersId, i, poolSize, twitter, countHandler);
            service.execute(handler);
            handlers.add(handler);
        }
        while (!countHandler.isEmpty()) try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap<String, Integer> res = new HashMap<>();
        for (Handler handler : handlers) {
            res.putAll(handler.getImgCount());
        }
        return res;

    }


    public User getUser(String login) throws TwitterException {
        return twitter.showUser(login);
    }
}

abstract class FollowersInterface {

    long cursor = -1;

    abstract IDs next() throws TwitterException;

}

class Handler implements Runnable {

    private List<Long> userIds;
    private int handlerId;
    private int step;
    private Exception e;

    private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Handler.class);

    public HashMap<String, Integer> getImgCount() {
        return imgCount;
    }

    private HashMap<String, Integer> imgCount;
    private Twitter parser;
    private static final int STEP_SIZE = 100;
    private CountHandler removeHadlers;

    public Handler(List<Long> userIds, int handlerId, int step, Twitter parser, CountHandler removeHandlers) {
        this.userIds = userIds;
        this.handlerId = handlerId;
        this.step = step;
        this.parser = parser;
        imgCount = new HashMap<>();
        this.removeHadlers = removeHandlers;
        removeHandlers.inc();
        e = null;
    }

    public Exception getE() {
        return e;
    }

    @Override
    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("thread:" + handlerId + "started");
        }
        int i = handlerId;
        int course;
        while ((course = i * STEP_SIZE) < userIds.size()) {
            long[] userIdsStep = new long[Math.min(STEP_SIZE, userIds.size() - i)];
            i += step;
            for (int j = 0; j < Math.min(STEP_SIZE, userIds.size() - course); j++) {
                userIdsStep[j] = userIds.get(course + j);
            }
            try {
                List<User> users = parser.lookupUsers(userIdsStep);
                for (User user : users) {
                    String img = user.getOriginalProfileImageURL();
                    int postCount = user.getStatusesCount();
                    imgCount.put(img, postCount + 1);
                }
            } catch (TwitterException e) {
                logger.error(handlerId, e);
                removeHadlers.setException(e);
                this.e = e;
                return;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("thread:" + handlerId + "end");
        }
        removeHadlers.dec();
    }
}

class CountHandler {
    private int count;

    public void inc() {
        count++;
    }

    public boolean isEmpty() throws TwitterException {
        if (exception != null) throw exception;
        return count <= 0;
    }

    public void dec() {
        count--;
    }

    public void setException(TwitterException e) {
        this.exception = e;
    }

    private TwitterException exception;
}


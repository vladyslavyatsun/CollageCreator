package collage.entity;

public class Image {
    private final String imgUrl;
    private int width;
    private int height;
    private int x;
    private int y;
    private int postCount;

    public Image(String imgUrl, int postCount) {
        this.imgUrl = imgUrl;
        this.postCount = postCount;
    }


    public String getImgUrl() {
        return imgUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int count) {
        this.postCount = count;
    }
}

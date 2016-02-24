package collage.controller.collage;

import collage.ConfigProperty;
import collage.entity.Image;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class Canvas {

    private int width;
    private int height;
    private List<Image> imageList;
    private Random random;
    private List<Point4D> points;

    public Canvas(int width, int height, List<Image> imageList) {
        this();
        this.width = width;
        this.height = height;
        this.imageList = imageList;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }

    public Canvas() {
        points = new LinkedList<>();
        random = new Random();
    }

    private void addBaryer() {
        points.add(new Point4D(-width * 2, -height * 2, width * 2, 0));
        points.add(new Point4D(width, -height * 2, width * 2, height * 2));
        points.add(new Point4D(-width * 2, height, width * 2, height * 2));
        points.add(new Point4D(-width * 2, -height * 2, 0, height * 2));
    }


    public void build() {
        clear();
        addBaryer();
        for (Image image : imageList) {
            put(image);
        }
    }

    private void put(Image image) {
        Point4D point4D = getFreePoint(image);
        image.setX(point4D.x1);
        image.setY(point4D.y1);
        image.setWidth(point4D.x2 - point4D.x1);
        image.setHeight(point4D.y2 - point4D.y1);
        points.add(point4D);
    }

    public Point4D getFreePoint(Image image) {
        Point5D best = null;
        for (int i = 0; i < ConfigProperty.SEARCH_COUNT; i++) {
            Point5D point5D = getRandomPoint(image);
            if (best == null || best.anInt > point5D.anInt) {
                best = point5D;
            }
        }
        if (best.anInt == -1) {
            best.anInt = 0;
        }
//        best.x2 -= best.anInt;
//        best.y2 -= best.anInt;
        return best;
    }

    private static final int GRID = 20;

    private Point5D getRandomPoint(Image image) {

        Point5D p = new Point5D();
        do {
            p.x1 = random.nextInt(width);
            p.y1 = random.nextInt(height);
            p.x2 = p.x1 + image.getWidth();
            p.y2 = p.y1 + image.getHeight();
        } while (!check(p));
        return p;
    }

    private boolean check(Point5D p) {
        int fasa = -1;
        if (p == null) return false;
        for (Point4D p4 : points) {
            if (p4 == null) continue;
            if (check(p.x1, p4.x1, p4.x2) && check(p.y1, p4.y1, p4.y2)) return false;
            int buf = -1;
            try {
                buf = Math.min(minIn(p.x1, p.x2, p4.x1, p4.x2) - p.x1, minIn(p.y1, p.y2, p4.y1, p4.y2) - p.y1);
                buf = p.x2 - p.x1 - buf;//todo if height != width
            } catch (Exception ignored) {
            }


            if ((check(p.x1, p4.x1, p4.x2) && check(p.x2, p4.x1, p4.x2)) && check(p.y2, p4.y1, p4.y2)) {
                buf = Math.max(buf, p.y2 - p4.y1);
            }

            if ((check(p.y1, p4.y1, p4.y2) && check(p.y2, p4.y1, p4.y2)) && check(p.x2, p4.x1, p4.x2)) {
                buf = Math.max(buf, p.x2 - p4.x1);
            }
            fasa = Math.max(buf, fasa);

        }
        p.anInt = fasa;
        return true;
    }

    private int minIn(int x1, int x2, int z1, int z2) throws Exception {
        if (check(z1, x1, x2)) return z1;
        if (check(z2, x1, x2)) return z2;
        throw new Exception();
    }


    private boolean check(int x, int x1, int x2) {
        return x1 < x && x2 > x;
    }

    private void clear() {
        points.clear();
        addBaryer();
    }

    private class Point4D {
        int x1;
        int y1;
        int x2;
        int y2;

        public Point4D() {
        }

        public Point4D(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }

    private class Point2D {
        int x;
        int y;

        public Point2D(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private class Point5D extends Point4D {
        int anInt;

        public Point5D(int x1, int y1, int x2, int y2) {
            super(x1, y1, x2, y2);
        }

        public Point5D() {}
    }

}

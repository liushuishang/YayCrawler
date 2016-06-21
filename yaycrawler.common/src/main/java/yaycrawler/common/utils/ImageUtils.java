package yaycrawler.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Created by yuananyun on 2016/6/19.
 */
public class ImageUtils {


    /**
     * 合成指定的多张图片到一张图片
     *
     * @param imgSrcList       图片的地址列表
     * @param topLeftPointList 每张小图片的偏移量
     * @param countOfLine 每行的小图片个数
     * @param cutWidth         每张小图片截取的宽度（像素）
     * @param cutHeight        每张小图片截取的高度（像素）
     * @param savePath         合并后图片的保存路径
     * @param subfix         合并后图片的后缀
     * @return 是否合并成功
     */
    public static boolean combineImages(List<String> imgSrcList, List<String[]> topLeftPointList, int countOfLine, int cutWidth, int cutHeight, String savePath, String subfix) {
        if (imgSrcList == null || savePath == null || savePath.trim().length() == 0) return false;
        BufferedImage lastImage = new BufferedImage(cutWidth * countOfLine, cutHeight * ((int) (Math.floor(imgSrcList.size() / countOfLine))), BufferedImage.TYPE_INT_RGB);
        String prevSrc = "";
        BufferedImage prevImage = null;
        try {
            for (int i = 0; i < imgSrcList.size(); i++) {
                String src = imgSrcList.get(i);
                BufferedImage image;
                if (src.equals(prevSrc)) image = prevImage;
                else {
                    if (src.trim().toLowerCase().startsWith("http"))
                        image = ImageIO.read(new URL(src));
                    else
                        image = ImageIO.read(new File(src));
                    prevSrc = src;
                    prevImage = image;

                }
                if (image == null) continue;
                String[] topLeftPoint = topLeftPointList.get(i);
                int[] pixArray = image.getRGB(0 - Integer.parseInt(topLeftPoint[0].trim()), 0 - Integer.parseInt(topLeftPoint[1].trim()), cutWidth, cutHeight, null, 0, cutWidth);
                int startX = ((i) % countOfLine) * cutWidth;
                int startY = ((i) / countOfLine) * cutHeight;

                lastImage.setRGB(startX, startY, cutWidth, cutHeight, pixArray, 0, cutWidth);
            }
            File file = new File(savePath);
            return ImageIO.write(lastImage, subfix, file);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static int findXDiffRectangeOfTwoImage(String imgSrc1, String imgSrc2) {
        try {
            BufferedImage image1 = ImageIO.read(new File(imgSrc1));
            BufferedImage image2 = ImageIO.read(new File(imgSrc2));
            int width1 = image1.getWidth();
            int height1 = image1.getHeight();
            int width2 = image2.getWidth();
            int height2 = image2.getHeight();

            if (width1 != width2) return -1;
            if (height1 != height2) return -1;

            int left = 0;
            /**
             * 从左至右扫描
             */
            boolean flag = false;
            for (int i = 0; i < width1; i++) {
                for (int j = 0; j < height1; j++)
                    if (isPixelNotEqual(image1, image2, i, j)) {
                        left = i;
                        flag = true;
                        break;
                    }
                if (flag) break;
            }
            return left;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }


    /**
     * 找出两张图片不同之处的最小外接矩形
     *
     * @param imgSrc1 第一张图片
     * @param imgSrc2 第二张图片
     * @return
     */
    public static Rectangle findDiffRectangeOfTwoImage(String imgSrc1, String imgSrc2) {
        try {
            BufferedImage image1 = ImageIO.read(new File(imgSrc1));
            BufferedImage image2 = ImageIO.read(new File(imgSrc2));
            int width1 = image1.getWidth();
            int height1 = image1.getHeight();
            int width2 = image2.getWidth();
            int height2 = image2.getHeight();

            if (width1 != width2) return null;
            if (height1 != height2) return null;

            int left = 0, top = 0, right = 0, bottom = 0;

            /**
             * 从左至右扫描
             */
            boolean flag = false;
            for (int i = 0; i < width1; i++) {
                for (int j = 0; j < height1; j++)
                    if (isPixelNotEqual(image1, image2, i, j)) {
                        left = i;
                        flag = true;
                        break;
                    }
                if (flag) break;
            }
            /**
             * 从上往下扫描
             */
            flag = false;
            for (int i = 0; i < height1; i++) {
                for (int j = 0; j < width1; j++)
                    if (isPixelNotEqual(image1, image2, j, i)) {
                        top = i;
                        flag = true;
                        break;
                    }
                if (flag) break;
            }

            /**
             * 从右至左扫描
             */
            flag = false;
            for (int i = width1 - 1; i >= 0; i--) {
                for (int j = 0; j < height1; j++)
                    if (isPixelNotEqual(image1, image2, i, j)) {
                        right = i;
                        flag = true;
                        break;
                    }
                if (flag) break;
            }

            /**
             * 下扫描
             */
            flag = false;
            for (int i = height1 - 1; i >= 0; i--) {
                for (int j = 0; j < width1; j++)
                    if (isPixelNotEqual(image1, image2, j, i)) {
                        bottom = i;
                        flag = true;
                        break;
                    }
                if (flag) break;
            }
            return new Rectangle(left, top, -(left - right), -(top - bottom));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    private static boolean isPixelNotEqual(BufferedImage image1, BufferedImage image2, int i, int j) {
        int pixel1 = image1.getRGB(i, j);
        int pixel2 = image2.getRGB(i, j);

        int[] rgb1 = new int[3];
        rgb1[0] = (pixel1 & 0xff0000) >> 16;
        rgb1[1] = (pixel1 & 0xff00) >> 8;
        rgb1[2] = (pixel1 & 0xff);

        int[] rgb2 = new int[3];
        rgb2[0] = (pixel2 & 0xff0000) >> 16;
        rgb2[1] = (pixel2 & 0xff00) >> 8;
        rgb2[2] = (pixel2 & 0xff);

        for (int k = 0; k < 3; k++)
            if (Math.abs(rgb1[k] - rgb2[k]) > 50)
                return true;

        return false;
    }


}

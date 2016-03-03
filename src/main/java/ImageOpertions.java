import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by sbw22 on 2016/3/3.
 */
public class ImageOpertions {


    public static void main(String[] args) throws  Exception{
        BufferedImage bi = (BufferedImage) ImageIO.read(new File("d:/detail.jpg"));
        int[] rgb = new int[3];

        // 获取图像的宽度和高度
        int width = bi.getWidth();
        int height = bi.getHeight();
        boolean isTransparent = true;
        // 扫描图片
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {// 行扫描
                int dip = bi.getRGB(j, i);
                rgb[0] = (dip & 0xff0000) >> 16;
                rgb[1] = (dip & 0xff00) >> 8;
                rgb[2] = (dip & 0xff);
                System.out.println("dip:"+dip+" "+rgb[0]+" "+rgb[1]+" "+rgb[2]);
                if(rgb[0]>200&&rgb[1]>200&&rgb[2]>200){
                    bi.setRGB(j, i, -1);
                }
            }

        }


        BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics2D g2d = (Graphics2D)target.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(bi.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

        g2d.dispose();
        ImageIO.write(target, "png", new File("d:/24_2.png"));
//System.out.print(Color.white.getRGB());
    }

    /**
     * 获取图片RGB数组
     * @param filePath
     * @return
     */
    public static int[][] getImageGRB(String filePath) {
        File file  = new File(filePath);
        int[][] result = null;
        if (!file.exists()) {
            return result;
        }
        try {
            BufferedImage bufImg = ImageIO.read(file);
            int height = bufImg.getHeight();
            int width = bufImg.getWidth();
            result = new int[width][height];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    result[i][j] = bufImg.getRGB(i, j) & 0xFFFFFF;
                    System.out.println(bufImg.getRGB(i, j) & 0xFFFFFF);

                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }
}

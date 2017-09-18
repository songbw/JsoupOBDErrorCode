import bean.BZJTreeBean;
import bean.StandardBean;
import com.mongodb.DBObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.DBUtil;
import utils.MongoUtil;
import utils.UpaiUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by song on 16/4/19.
 */
public class BZJTreeParse {

    private static final String SEQUENCE = "OBDErrorCode2";
    private static String firstUrl = "http://www.164580.com/biaozhun/list_1_2_90.html";
    private static String pageBaseUrl = "http://www.164580.com" ;
    private static String baseUrl = "http://www.164580.com/biaozhun";
    private static int count = 0 ;
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://42.121.121.130:3307/biaozhun?useUnicode=true&characterEncoding=utf8";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "xiquedaojia@123";
    static int counts = 0;

    public static void main(String[] args) {
        try {
//            getTree();
            getStandard();


//            Document basedoc = getDoc("http://www.164580.com/biaozhun/index.php?keyword=Tolerences+for+fasteners-Bolt%2Fnut+assemblies+for+service+temperatures+from+-200%E2%84%83~%2B700%E2%84%83&submit=+%E6%90%9C+%E7%B4%A2+") ;
//            Element element = basedoc.select(".indexbox").get(1) ;
//            Elements tables = element.select("table") ;
//            Element content = tables.get(4);
//            getStandardContent(content,new StandardBean());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void getStandard() throws Exception {
        List<BZJTreeBean> bzjTreeBeans = DBUtil.findTreeByUrl();
        StandardBean standardBean1 = DBUtil.getStandardLast();
        for (BZJTreeBean bzjTreeBean : bzjTreeBeans) {
            if (standardBean1!=null) {
                if (bzjTreeBean.getId() < standardBean1.getParentId()) {
                    continue;
                }
            }
//            String kinship = (String) dbobject.get("Kinship");
            Long parentId = bzjTreeBean.getId();
            StandardBean standardBean = new StandardBean() ;
//            standardBean.setKinship(kinship);
            standardBean.setParentId(parentId);

            String url = bzjTreeBean.getUrl();
            System.out.println("baseURL is : " + url);
            url = url.substring(1,url.length()) ;
            Document basedoc = getDoc(baseUrl + url) ;
            Element element = basedoc.select(".indexbox").get(1) ;
            Elements tables = element.select("table") ;
            Element content = tables.get(4);
            int endCount = DBUtil.getStandardCountByParentId(parentId) ;
            int page = endCount/20;
            int pageSize = endCount%20;
            if (page==0) {
                getStandardContent(content,standardBean,pageSize);
            }
            // 分页获取数据
            Element pages = tables.get(3).select("div[class=page]").get(0) ;
            Elements pageList = pages.select("a[href]") ;
            int i=0;
            for (Element link : pageList) {
                if (page > 0) {
                    if (i < page) {
                        i++;
                        continue;
                    }
                }
                String pageUrl = link.attr("href") ;
                Document pageDoc = getDoc(pageBaseUrl + pageUrl) ;
                Element pageElement = pageDoc.select(".indexbox").get(1)  ;
                Elements pageTables = pageElement.select("table");
                Element pageContent = pageTables.get(4);
                getStandardContent(pageContent, standardBean,pageSize);

            }
        }
    }

    /**
     * 解析小图片ele
     * @param smallImgEle
     * @return
     */
    private static String parseSmallImgEle(Element smallImgEle) {
        String path = smallImgEle.select("img").attr("src");
        return path;
    }

    /**
     * 解析文字信息
     * @param textEle
     * @return
     */
    private static StandardBean parseTextEle(Element textEle) {
        StandardBean standardBean = new StandardBean();
        Elements textEles = textEle.children();
        String biaozhunType = textEles.get(0).text();
        String bigImgUrl = textEle.select("a").attr("href");
        String names = textEle.select("a").get(0).text();
        String reg = "[\u4e00-\u9fa5]";
        int index = -1;
        String daihao="";
        String name ="";
        if (names.matches (".*" + reg + ".*"))
        {
            int test = names.split(reg).length;
            if (test > 0) {
                index = names.split(reg)[0].length();
                daihao=names.substring(0,index-1);
                name = names.substring(index-1);
            } else {
                name = names;
            }
        } else {
            daihao=names;
        }
        String dengxiaobz = textEle.select("font[color=#333333]").text();
//        String dengxiaobz = textEles.get(5).text();
        String engName = textEle.select("span[style=color:#999]").text();
//        String engName = textEles.get(7).text();
        standardBean.setEngName(engName);
        standardBean.setName(name);
        standardBean.setType(biaozhunType);
        standardBean.setSimilar(dengxiaobz);
        standardBean.setCode(daihao);
        standardBean.setImg(bigImgUrl);
        System.out.println("标准类型：" + biaozhunType + " ,代号: " + daihao + " ,名称: " + name + " ,等效标准: " + dengxiaobz + " ,英语名称：" +engName);
        return standardBean;
    }


    private static String getBigImgDoc(Element imgEle) {
        String path = imgEle.select("a").attr("href");
        return path;
    }

    private static String getBigImgUrl(String docUrl) {
        Document imgDoc = getDoc(pageBaseUrl + docUrl) ;
//        Document imgDoc = getDoc("http://www.164580.com/info_153.html") ;
        Element item1Element = imgDoc.getElementById("item1") ;
        Elements elements = item1Element.children();
        Element imgEle = elements.get(6);
        // 格式错了，返回空路径
        if (imgEle.nodeName().equals("div")) {
            return null;
        }
        Element imgTdEle = imgEle.select("td[align=center]").get(0);
        String imgPath = imgTdEle.select("img").attr("src");
        return imgPath;
    }

    // 从doc中获取具体内容
    public static void getStandardContent(Element element, StandardBean standardParent,int pageSize) throws Exception{
        long parentId = standardParent.getParentId() ;
        Element tr = element.child(0).child(0);
        Elements talist = tr.child(0).children();
        int i = 0;
        for (Element ele : talist) {
            if (i < pageSize) {
                i++;
                continue;
            }
            //解析具体内容
            Element tbody = ele.getElementsByTag("tbody").get(0);
            Elements contents = tbody.child(0).children();
            Element smallImgElement = contents.get(2);
            Element smallImg2Ele = contents.get(3);
            Element textInfoEle = contents.get(4);

            StandardBean standardBean = parseTextEle(textInfoEle);
            standardBean.setParentId(parentId);
            String smallImg = parseSmallImgEle(smallImgElement);
            String smallImg2Path = parseSmallImgEle(smallImg2Ele);
            standardBean.setSmallImg(smallImg);
            standardBean.setSmallImgT(smallImg2Path);
//            String bigImg = getBigImgDoc(smallImgElement);


            String img = getBigImgUrl(standardBean.getImg()) ;
//            standardBean.setImg(bigImg);
            String smallImgPath = getImg(standardBean.getSmallImg(),"small/") ;
//            standardBean.setSmallImgFlag(UpaiUtil.uploadImg(smallImgPath));
            String smallImgPathT = getImg(standardBean.getSmallImgT(),"smallt/") ;
//            standardBean.setSmallImgTFlag(UpaiUtil.uploadImg(smallImgPathT));
            standardBean.setSmallImgPath(smallImgPath);
            standardBean.setSmallImgPathT(smallImgPathT);
            if (img!=null && (!"".equals(img))) {
                String imgPath = getImg(pageBaseUrl + img, "big/") ;
//                standardBean.setImgPathFlag(UpaiUtil.uploadImg(imgPath));
                standardBean.setImgPath(imgPath);
            }

            DBUtil.addStandard(standardBean);
            //end try
            System.out.println("Goodbye!");

        }
    }

    public static String getImg(String path,String local)throws Exception{
        if ("".equals(path) || path == null) {
            return "" ;
        }
        URL url = new URL(path) ;
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setReadTimeout(6 * 10000);
        int a = conn.getResponseCode() ;
        if (conn.getResponseCode() <10000){
            InputStream inputStream = conn.getInputStream();
            if (local.equals("big/")) {
                String filePath = imageOpertion(inputStream,local) ;
                return filePath;
            }

            byte[] data = readStream(inputStream);
            if(data.length>(1024*1)){
                String fileName = new Date().getTime() + ".jpg" ;
                String localPath = "./img/" + local + fileName;
                FileOutputStream outputStream = new FileOutputStream(localPath);
                outputStream.write(data);
                outputStream.close();
                String filePath = local +  fileName;
                return filePath;
            }
        }
        return null ;
    }

    public static String imageOpertion(InputStream inputStream,String local) throws Exception{
        BufferedImage bi = ImageIO.read(inputStream);
        int[] rgb = new int[3];
        if (bi != null) {

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
//                System.out.println("dip:"+dip+" "+rgb[0]+" "+rgb[1]+" "+rgb[2]);
                    if(rgb[0]>200&&rgb[1]>200&&rgb[2]>200){
                        bi.setRGB(j, i, -1);
                    }
                }

            }
            String fileName = new Date().getTime() + ".jpg" ;
            String localPath = "./img/" + local + fileName;
            BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
            Graphics2D g2d = (Graphics2D)target.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.drawImage(bi.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

            g2d.dispose();
            ImageIO.write(target, "jpg", new File(localPath));
            return local + fileName;
        } else {
            return null ;
        }
    }


    public static void getTree() {
        Document basedoc = getDoc(firstUrl) ;
        Element baseEle = basedoc.getElementById("showtree") ;
        Elements elements = baseEle.children();
        Element ulElement = elements.get(0) ;
        Elements baseliElements = ulElement.children() ;
        BZJTreeBean bzjTree = new BZJTreeBean();
        parseUL(ulElement,bzjTree);
    }

    public static void parseUL(Element ul,BZJTreeBean bzjTree) {
        for (Element li : ul.children()) {
            BZJTreeBean bzj = new BZJTreeBean();
            if (li.select("ul").size() != 0) {
                bzjTree.setName(li.child(0).text());
                long id  = insertTree(bzjTree);
                System.out.println(counts++ + ": " + li.child(0).text());
                Element nextUl = li.select("ul").get(0);
                bzj.setParentId(id);
                parseUL(nextUl,bzj);
            } else {
                String allName = li.child(0).text();
                String[] name = allName.split("\\(");
                String regEx="[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(allName);
                String count = m.replaceAll("").trim();
                bzjTree.setName(name[0]);
                bzjTree.setCount(Integer.parseInt(count));
                bzjTree.setUrl(li.select("a").attr("href"));
                System.out.println(counts++ + ": name is " + name[0] + "  url is : " + li.select("a").attr("href") + " , count is : " + Integer.parseInt(count));
                insertTree(bzjTree);
            }
        }
    }

    public static long insertTree(BZJTreeBean bzjTreeBean) {
        Connection conn = null;
        Statement stmt = null;
        Long insertId = 0l;
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String insertSql = "insert into bzj_tree(name,count,url,parentId,kinship) values('" + bzjTreeBean.getName() + "'," +
                    "'" + bzjTreeBean.getCount() + "'," +
                    "'" + bzjTreeBean.getUrl() + "'," +
                    "'" + bzjTreeBean.getParentId() + "'," +
                    "" + bzjTreeBean.getKinship() + ")";
            PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                insertId = rs.getLong(1);
//                    System.out.println("数据主键：" + insertId);
            }

//            bzjTreeBean.setKinship(bzjTreeBean.getKinship() + insertId + ",");
//            String updateSql = "update standard set kinship = '"+bzjTreeBean.getKinship()+"' where id =" + insertId ;
//            stmt.executeUpdate(updateSql);


            //STEP 6: Clean-up environment
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }
        return insertId;
    }


    public static byte[] readStream(InputStream inputStream) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while((len = inputStream.read(buffer)) !=-1){
            outputStream.write(buffer, 0, len);
        }
        outputStream.close();
        inputStream.close();
        return outputStream.toByteArray();
    }

    private static Document getDoc(String url){
        try {
            Document doc = Jsoup
                    .connect(url)
                    .timeout(30000)
                    .get();
            return doc;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}

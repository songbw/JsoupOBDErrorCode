import bean.BZJTreeBean;
import bean.StandardBean;
import com.mongodb.DBObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.DBUtil;
import utils.MongoUtil;

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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void getStandard() throws Exception {
        List<BZJTreeBean> bzjTreeBeans = DBUtil.findTreeByUrl();
        for (BZJTreeBean bzjTreeBean : bzjTreeBeans) {

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
            getStandardContent(content,standardBean);

            // 分页获取数据
            Element pages = tables.get(3).select("div[class=page]").get(0) ;
            Elements pageList = pages.select("a[href]") ;
            for (Element link : pageList) {
                String pageUrl = link.attr("href") ;
                Document pageDoc = getDoc(pageBaseUrl + pageUrl) ;
                Element pageElement = pageDoc.select(".indexbox").get(1)  ;
                Elements pageTables = pageElement.select("table");
                Element pageContent = pageTables.get(4);
                getStandardContent(pageContent, standardBean);
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
        String[] names = textEles.get(2).text().split(" ");
        String daihao="";
        for (int i=0;i<names.length-1;i++) {
            daihao=daihao+names[i];
        }
        String name = names[names.length-1];
        String dengxiaobz = textEles.get(5).text();
        String engName = textEles.get(7).text();
        standardBean.setEngName(engName);
        standardBean.setName(name);
        standardBean.setType(biaozhunType);
        standardBean.setSimilar(dengxiaobz);
        standardBean.setCode(daihao);
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
    public static void getStandardContent(Element element, StandardBean standardParent) throws Exception{
        Connection conn = null;
        Statement stmt = null;
        long parentId = standardParent.getParentId() ;
//        Elements tableList = element.select("td").get(0).select("table[width=100%]") ;
        Element tr = element.child(0).child(0);
        Elements talist = tr.child(0).children();
        for (Element ele : talist) {
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
            String bigImg = getBigImgDoc(smallImgElement);


//            Document imgDoc = getDoc(pageBaseUrl + bigImg) ;
//            Element imgElement = imgDoc.body() ;
//            Elements imgSelects = imgElement.select("table[align=center]") ;
//            Element imgTable = imgSelects.get(7) ;
            String img = getBigImgUrl(bigImg) ;
            standardBean.setImg(bigImg);
//            String title = "" ;
//            if (imgTable.select("img") != null && imgTable.select("img").size() > 0) {
//                img = imgTable.select("img").get(0).attr("src") ;
//                standardBean.setImg(img);
//                title = imgTable.select("img").get(0).attr("title") ;
//                standardBean.setImgTitle(title);
//            }
            String smallImgPath = getImg(standardBean.getSmallImg(),"small/") ;
            String smallImgPathT = getImg(standardBean.getSmallImgT(),"smallt/") ;
            standardBean.setSmallImgPath(smallImgPath);
            standardBean.setSmallImgPathT(smallImgPathT);
            if (img!=null && (!"".equals(img))) {
                String imgPath = getImg(pageBaseUrl + img, "big/") ;
                standardBean.setImgPath(imgPath);
            }

//            standardBean.set_id(null);
//            String _id = MongoUtil.add("Standard", standardBean);
//            long id = MongoUtil.queryCount("Standard") ;
//            DBObject standard =  MongoUtil.query("Standard", _id);
//            standardBean.setId(id);
//            standardBean.setKinship(standardBean.getKinship() + standardBean.getId() + ",");
//            MongoUtil.updateBatch("Standard", standard, standardBean);
            try{
                //STEP 2: Register JDBC driver
                Class.forName("com.mysql.jdbc.Driver");

                //STEP 3: Open a connection
                System.out.println("Connecting to database...");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);

                //STEP 4: Execute a query
                System.out.println("Creating statement...");
                stmt = conn.createStatement();
                String insertSql="insert into standard(code,imgAlt,smallImg,engName,img,imgPath,imgTitle,kinship,name,similar,smallImgPath,type,parentId) values('"+standardBean.getCode()+"'," +
                        "'"+standardBean.getsImgAlt()+"'," +
                        "'"+standardBean.getSmallImg()+"'," +
                        "'"+standardBean.getEngName()+"'," +
                        "'"+standardBean.getImg()+"'," +
                        "'"+standardBean.getImgPath()+"'," +
                        "'"+standardBean.getImgTitle()+"'," +
                        "'"+standardBean.getKinship()+"'," +
                        "'"+standardBean.getName()+"'," +
                        "'"+standardBean.getSimilar()+"'," +
                        "'"+standardBean.getSmallImgPath()+"'," +
                        "'"+standardBean.getType()+"'," +
                        ""+standardBean.getParentId()+")";
                PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                pstmt.executeUpdate();
                Long insertId = 0l;
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    insertId = rs.getLong(1);
//                    System.out.println("数据主键：" + insertId);
                }

                standardBean.setKinship(standardBean.getKinship() + insertId + ",");
                String updateSql = "update standard set kinship = '"+standardBean.getKinship()+"' where id =" + insertId ;
                stmt.executeUpdate(updateSql);


                //STEP 6: Clean-up environment
                stmt.close();
                conn.close();
            }catch(SQLException se){
                //Handle errors for JDBC
                se.printStackTrace();
            }catch(Exception e){
                //Handle errors for Class.forName
                e.printStackTrace();
            }finally{
                //finally block used to close resources
                try{
                    if(stmt!=null)
                        stmt.close();
                }catch(SQLException se2){
                }// nothing we can do
                try{
                    if(conn!=null)
                        conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }//end finally try
            }//end try
            System.out.println("Goodbye!");
//            System.out.println("count : "+ count +", parentId : " +standardBean.getParentId() +"  , kinship : "+standardBean.getKinship()+",  informations is  smallImg : " + standardBean.getSmallImg() + ", imgAlt : " + standardBean.getsImgAlt() + ", type : " + type + ", name : " + name + ", engName : " + engName + ", similar : " + similar + ", img : "  + img + ", title : " + title);
            count ++ ;
        }
    }

    public static String getImg(String path,String local)throws Exception{
        URL url = new URL(path) ;
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setReadTimeout(6 * 10000);
        int a = conn.getResponseCode() ;
        if (conn.getResponseCode() <10000){
            InputStream inputStream = conn.getInputStream();
            if (local.equals("big/")) {
                return imageOpertion(inputStream,local) ;
            }

            byte[] data = readStream(inputStream);
            if(data.length>(1024*1)){
                String localPath = "./img/" + local + new Date().getTime() + ".jpg" ;
                FileOutputStream outputStream = new FileOutputStream(localPath);
                outputStream.write(data);
                outputStream.close();
                return localPath ;
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

            String localPath = "./img/" + local + new Date().getTime() + ".jpg" ;
            BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
            Graphics2D g2d = (Graphics2D)target.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.drawImage(bi.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

            g2d.dispose();
            ImageIO.write(target, "jpg", new File(localPath));
            return localPath;
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

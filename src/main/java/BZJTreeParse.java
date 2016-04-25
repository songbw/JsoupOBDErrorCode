import bean.BZJTreeBean;
import bean.StandardBean;
import com.mongodb.DBObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.MongoUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by song on 16/4/19.
 */
public class BZJTreeParse {

    private static final String SEQUENCE = "OBDErrorCode2";
    private static String firstUrl = "http://www.164580.com/biaozhun/list_1_2_90.html";
    private static String pageBaseUrl = "http://www.164580.com" ;
    private static String baseUrl = "http://www.164580.com/biaozhun";
    private static int count = 0 ;

    public static void main(String[] args) {
//        getTree();
//        String tempurl = "./list_1_2_92.html";
//        tempurl = tempurl.substring(1,tempurl.length()) ;
//        Document basedoc = getDoc(baseUrl + tempurl) ;
        try {
            getStandard();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void getStandard() throws Exception {
        List<DBObject> dbobjects = MongoUtil.query("BZJTree") ;
        for (DBObject dbobject : dbobjects) {

            if(dbobject.get("Url") != null && !"null".equals(dbobject.get("Url")) && !"".equals(dbobject.get("Url"))) {

                String kinship = (String) dbobject.get("Kinship");
                Long parentId = (Long) dbobject.get("ParentId");
                StandardBean standardBean = new StandardBean() ;
                standardBean.setKinship(kinship);
                standardBean.setParentId(parentId);

                String url = (String) dbobject.get("Url");
                url = url.substring(1,url.length()) ;
                Document basedoc = getDoc(baseUrl + url) ;
                Element element = basedoc.body() ;
                Elements tables = element.select("table") ;
                getStandardContent(element,standardBean);

                // 分页获取数据
                Element pages = tables.get(7).select("div[class=page]").get(0) ;
                Elements pageList = pages.select("a[href]") ;
                for (Element link : pageList) {
                    String pageUrl = link.attr("href") ;
                    Document pageDoc = getDoc(pageBaseUrl + pageUrl) ;

                    Element pageElement = pageDoc.body() ;
                    getStandardContent(pageElement, standardBean);
                }

            }
        }
    }

    // 从doc中获取具体内容
    public static void getStandardContent(Element element, StandardBean standardBean) {
        Elements tables = element.select("table") ;
        Element table = tables.get(8) ;
        Elements tableList = table.select("td").get(0).select("table[width=100%]") ;
        for (Element ele : tableList) {
            //解析具体内容
            Elements tds = ele.select("td") ;
            Element td3 = tds.get(3) ;
            Element td4 = tds.get(4) ;
            standardBean.setSmallImg(td3.select("img[src]").get(0).attr("src"));
            standardBean.setsImgAlt(td3.select("img[alt]").get(0).attr("alt"));
            String type = td4.select("font[color=#666666]").get(0).text() ;
            String name = td4.select("font[style=font-size:14px]").get(0).text() ;
            String engName = "" ;
            if (td4.select("span") != null && td4.select("span").size() > 0) {
                engName = td4.select("span").get(0).text() ;
            }
            String similar = "" ;
            if (td4.select("font[color=#333333]") != null && td4.select("font[color=#333333]").size() > 0) {
                similar = td4.select("font[color=#333333]").get(0).text() ;
            }
            String imgUrl = td4.select("a[href]").get(0).attr("href") ;
            Document imgDoc = getDoc(pageBaseUrl + imgUrl) ;
            Element imgElement = imgDoc.body() ;
            Elements imgSelects = imgElement.select("table[align=center]") ;
            Element imgTable = imgSelects.get(7) ;
            String imgPath = "" ;
            String title = "" ;
            if (imgTable.select("img") != null && imgTable.select("img").size() > 0) {
                imgPath = imgTable.select("img").get(0).attr("src") ;
                title = imgTable.select("img").get(0).attr("title") ;
            }
            System.out.println("count : "+ count +",  informations is  smallImg : " + standardBean.getSmallImg() + ", imgAlt : " + standardBean.getsImgAlt() + ", type : " + type + ", name : " + name + ", engName : " + engName + ", similar : " + similar + ", imgPath : "  + imgPath + ", title : " + title);
            count ++ ;
        }
    }


    public static void getTree() {
        Document basedoc = getDoc(firstUrl) ;
        Element baseEle = basedoc.getElementById("showtree") ;
        Elements elements = baseEle.children();
        Element ulElement = elements.get(0) ;
        Elements baseliElements = ulElement.children() ;
        BZJTreeBean bzjTree = new BZJTreeBean();
        getTreeUl(baseliElements,bzjTree) ;
    }

    public static void getTreeUl(Elements baseElement,BZJTreeBean bzjTree) {

        for (Element li: baseElement) {

            if (li.children().size() > 1) {
                bzjTree.set_id(null);
                bzjTree.setName(li.child(0).text());
                bzjTree.setParentId(bzjTree.getId());
                String _id = MongoUtil.add("BZJTree", bzjTree) ;
                DBObject bzjTree1 =  MongoUtil.query("BZJTree", _id);
                long id = MongoUtil.queryCount("BZJTree") ;
                bzjTree.setId(id + 1);
                bzjTree.setKinship(bzjTree.getKinship() + bzjTree.getId() + ",");
                MongoUtil.updateBatch("BZJTree",bzjTree1,bzjTree);
                getTreeUl(li.child(1).children(), bzjTree);
            } else if (li.children().size() == 1) {
                bzjTree.set_id(null);
                bzjTree.setUrl(li.child(0).attr("href"));
                bzjTree.setName(li.child(0).text());
                bzjTree.setParentId(bzjTree.getId());
                String _id = MongoUtil.add("BZJTree", bzjTree) ;
                DBObject bzjTree1 =  MongoUtil.query("BZJTree", _id);
                long id = MongoUtil.queryCount("BZJTree") ;
                bzjTree.setId(id + 1);
                bzjTree.setKinship(bzjTree.getKinship() + bzjTree.getId() + ",");
                MongoUtil.updateBatch("BZJTree", bzjTree1, bzjTree);
            }

        }
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

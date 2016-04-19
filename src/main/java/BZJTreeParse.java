import bean.BZJTreeBean;
import com.mongodb.DBObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.MongoUtil;

import java.io.IOException;

/**
 * Created by song on 16/4/19.
 */
public class BZJTreeParse {

    private static final String SEQUENCE = "OBDErrorCode2";
    private static String firstUrl = "http://www.164580.com/biaozhun/list_1_2_90.html";
    private static String baseUrl = "http://www.164580.com/biaozhun";

    public static void main(String[] args) {
//        getTree();
        String tempurl = "./list_1_2_92.html";
        tempurl = tempurl.substring(1,tempurl.length()) ;
        Document basedoc = getDoc(baseUrl+ tempurl) ;

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

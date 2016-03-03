import bean.OBDErrorCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by sbw22 on 2016/2/16.
 */
public class JsoupErrorCode {

    private static final String SEQUENCE = "OBDErrorCode2";

    public static void main(String[] args) {

        String firstUrl = "http://chezg.cn/OBDCode/List/P0/";
        String baseUrl = "http://chezg.cn";

        Document basedoc = getDoc(firstUrl) ;
        Element baseEle = basedoc.getElementById("main") ;
        Elements elements = baseEle.children();
        Element OBDList = elements.get(0) ;
        Elements  hrefList = OBDList.select("a[href]") ;
        OBDErrorCode obdErrorCode = null ;
        for (Element link: hrefList
             ) {
            System.out.println(link.attr("href")+"***************"+link.text());
            Document listdoc = getDoc(baseUrl+link.attr("href")) ;
            Element tempEle = listdoc.getElementById("main") ;
            Element odbIndex = tempEle.children().get(1);
            Elements  hrefIndex = odbIndex.select("a[href]") ;
            for (Element indexLink: hrefIndex
                 ) {
                obdErrorCode = new OBDErrorCode() ;
                String url = indexLink.attr("href") ;
                Document indexdoc = getDoc(baseUrl+url) ;
                Elements xj = indexdoc.select("tbody");
                Element scopeEle = xj.first().child(1) ;
                System.out.println(scopeEle.text());
                obdErrorCode.setCarMaker(scopeEle.text());
                Elements content =  xj.first().select("td") ;
                System.out.println(content);
                obdErrorCode.setCode(content.get(0).text());
                obdErrorCode.setDescChinese(content.get(1).text());
                obdErrorCode.setDesc(content.get(2).text());
                obdErrorCode.setScope(content.get(3).text());
                obdErrorCode.setAbout(content.get(4).text());
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

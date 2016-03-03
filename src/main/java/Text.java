import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by sbw22 on 2016/2/17.
 */
public class Text {

    public static void main(String[] args) {
        String baseUrl = "http://chezg.cn/" ;
        String xx = "/OBDCode/P02×× /" ;
        String[] param = xx.split("/") ;
        try {
            param[2] = URLEncoder.encode(param[2].trim(),"UTF-8") ;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        xx = "/" ;
        for (String x:param
             ) {
            xx += x +"/" ;
        }
        Document indexdoc = getDoc(baseUrl+param[1]+"/"+param[2]) ;
        Elements xj = indexdoc.select("tbody");
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

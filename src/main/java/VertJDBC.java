import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.sql.SQLConnection;

/**
 * Created by song on 16/5/13.
 */
public class VertJDBC {
    public static void main(String[] args) {
        JsonObject mySQLClientConfig = new JsonObject().put("host", "182.92.186.153").put("port",3306)
                .put("maxPoolSize",30)
                .put("username", "root")
                .put("password", "root")
                .put("database", "standard");
        final AsyncSQLClient client = MySQLClient.createShared(Vertx.vertx(), mySQLClientConfig);

        String uri = "mongodb://115.29.177.82:27017";

        String db = "chleon";

        JsonObject mongoconfig = new JsonObject()
                .put("connection_string", uri)
                .put("db_name", db);

        MongoClient mongoClient = MongoClient.createShared(Vertx.vertx(), mongoconfig);
        JsonObject query = new JsonObject().put("Type","[美标]");
        mongoClient.find("Standard", query, res -> {
            System.out.print("123456");

//            if (res.succeeded()) {
//
//                for (JsonObject json : res.result()) {
//
//                    System.out.println(json.encodePrettily());
////                    client.getConnection(conn -> {
////                        if (conn.failed()) {
////                            System.err.println(conn.cause().getMessage());
////                            return;
////                        }
////                        final SQLConnection connection = conn.result();
////                        connection.execute("")
////                    });
//
//                }
//
//            } else {
//
//                res.cause().printStackTrace();
//
//            }

        });


    }
}

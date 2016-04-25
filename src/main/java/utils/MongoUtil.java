package utils;

import com.mongodb.*;
import org.bson.types.ObjectId;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class MongoUtil {
	private static MongoClient mongoClient;
	private static DB chleonDB;

	private static String HOST;
	private static String PORT;
	private static String DB_NAME;
	public static final String SERVER_CLIENTID_PREFIX = "Servier-";
	public static final String CLIENT_CLIENTID_PREFIX = "Client-";
	
	static {
		try {
			Properties p = new Properties();
			InputStream in;
			in = MongoUtil.class.getResourceAsStream("/mongo_dev.properties");
			p.load(in);
			
			HOST = p.getProperty("mongo.host", "");
			PORT = p.getProperty("mongo.port", "27017");
			DB_NAME = p.getProperty("mongo.db", "chleon");
			int port = Integer.parseInt(PORT);
			
			mongoClient = new MongoClient(HOST, port);
			chleonDB = mongoClient.getDB(DB_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String add(String tableName, DBObject obj) {
		DBCollection collection = chleonDB.getCollection(tableName);
		collection.insert(obj);
		ObjectId id = (ObjectId)obj.get("_id");
		return id.toString();
	}
	
	public static List<DBObject> query(String tableName, DBObject query) throws Exception {
		DBCollection collection = chleonDB.getCollection(tableName);
		return collection.find(query).toArray();
	}

	public static List<DBObject> query(String tableName) throws Exception {
		DBCollection collection = chleonDB.getCollection(tableName) ;
		return collection.find().toArray() ;
	}
	
	public static DBObject query(String tableName, String id) {
		DBCollection collection = chleonDB.getCollection(tableName);
		DBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		return collection.findOne(query);
	}

	public static long queryCount(String tableName) {
		DBCollection collection = chleonDB.getCollection(tableName);

		return collection.count();
	}
	
	public static void update(String tableName, DBObject query, DBObject obj) {
		DBCollection collection = chleonDB.getCollection(tableName);
		DBObject updateSetValue = new BasicDBObject("$set", obj);
		collection.update(query, updateSetValue);
	}
	
	public static void updateBatch(String tableName, DBObject query, DBObject obj) {
		DBCollection collection = chleonDB.getCollection(tableName);
		DBObject updateSetValue = new BasicDBObject("$set", obj);
		collection.updateMulti(query, updateSetValue);
	}
	
	public static void main(String []args) {
//		DBObject query = new BasicDBObject();
//		query.put("_id", new ObjectId("526fa5e2e4b0ce193303216c"));
//		
//		
//		DBObject updatedValue = new BasicDBObject();  
//        updatedValue.put("status", 2);  
        // DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
        
        DBObject query = new BasicDBObject();
		query.put("serial_number", "babe002");
		query.put("status", 0);
		DBObject pending = new BasicDBObject();
		pending.put("status", 1);
		MongoUtil.updateBatch("task", query, pending);

		updateBatch("task", query, pending);
	}
	
}

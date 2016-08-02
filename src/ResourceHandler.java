import org.json.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jakob on 28/07/2016.
 */

@Path("resource")
public class ResourceHandler {

    //private String domain = "http://graugaard.bobach.eu:8080/";
    private String domain = Constants.DOMAIN;

    @GET
    public Response getUserResources(@QueryParam("token") String token) throws IOException {

        String s = makeRequest(domain + "/OAuth/rest/oauth/get_token_permissions?token=" + token);
        JSONObject t = new JSONObject(s);
        JSONObject o = new JSONObject();

        JSONArray arr = t.getJSONArray("permissions");

        o.put("user", t.getString("user"));

        for (int i = 0; i < arr.length(); i++) {
            switch (arr.getString(i)) {
                case "food":
                    o.put("food","lasagne");
                    break;
                case "film":
                    o.put("film", "lord or the rings");
                    break;
                case "book":
                    o.put("book", "winnie the pooh");
                    break;
                case "fear":
                    o.put("fear", "girl scouts");
                    break;
                case "game":
                    o.put("game", "tag");
                    break;
                default:
                    break;
            }
        }

        return Response.status(Response.Status.OK).entity(o.toString()).build();
    }

    private String makeRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

        StringBuilder builder = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder.toString();
    }
}

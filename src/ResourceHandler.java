import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Created by jakob on 28/07/2016.
 */

@Path("resource")
public class ResourceHandler {

    @GET
    public Response getUserResources(@QueryParam("token") String token) {
        JSONObject o = new JSONObject();
        o.put("food", "lasagne");
        o.put("film", "lord of the rings");
        o.put("book", "Winnie the Pooh");
        o.put("fear", "girl scouts");
        o.put("game", "tag");
        return Response.status(Response.Status.OK).entity(o.toString()).build();
    }
}

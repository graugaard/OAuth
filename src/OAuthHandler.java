import javax.ws.rs.*;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

/**
 * Created by jakob on 28/07/2016.
 */

@Path("oauth/*")
public class OAuthHandler {

    @GET
    @Path("/get_code")
    public Response getAuthorazationCode(@QueryParam("client_id") String clientId,
                                   @QueryParam("permissions") String permissionsString,
                                   @QueryParam("redirect_uri") String redirectUri) {
        List<String> permissions = new ArrayList<String>();
        for(String s: permissionsString.split(",")) {
            permissions.add(s);
        }
        URI uri = null;
        String s = "";
        for(String str : permissions) {
            s += str + ",";
        }
        try {
            uri = new URI(redirectUri + "?code=" + 5 + "&number_permissions=" + permissions.size() + "," + s);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Error");
        }
        Response response;
        if (uri == null) {
            response = Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            response = Response.status(Response.Status.SEE_OTHER).location(uri).build();
        }
        return response;
    }
}

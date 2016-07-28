import javax.ws.rs.*;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.json.*;
import java.util.logging.*;

/**
 * Created by jakob on 28/07/2016.
 */

@Path("oauth")
public class OAuthHandler {

    @GET
    @Path("get_code")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthorizationCode(@QueryParam("client_id") String clientId,
                                   @QueryParam("permissions") String permissionsString,
                                   @QueryParam("redirect_uri") String redirectUri) {
        List<String> permissions = new ArrayList<String>();
        JSONObject json = new JSONObject();
        for(String s: permissionsString.split(",")) {
            permissions.add(s);
        }
        URI uri = null;

        try {
            uri = new URI(redirectUri + "?code=" + 5);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Error");
        }
        String code = "";
        String redirect = "";
        if (uri == null) {
            redirect = "error";
            code = "error";
        } else {
            code = "5";
            redirect = redirectUri;
        }

        json.put("auth_code", code);
        json.put("redirect_uri", redirect);

        return Response.status(Response.Status.OK).entity(json.toString()).build();
    }

    @GET
    public Response exchangeCodeForToken(@QueryParam("code") String authCode,
                                         @QueryParam("client_id") String clientId,
                                         @QueryParam("client_secret") String clientSecret) {
        if (codeAndClientIdMatch(authCode, clientId)) {
            JSONObject object = generateToken(clientId);
            return Response.status(Response.Status.OK).entity(object.toString()).build();
        } else {
            JSONObject object = new JSONObject();
            object.put("access_token", "error");
            return Response.status(Response.Status.OK).entity(object.toString()).build();
        }
    }

    private JSONObject generateToken(String clientId) {
        JSONObject o = new JSONObject();
        o.put("access_token", "1234567890");
        return o;
    }

    private boolean codeAndClientIdMatch(String authCode, String clientId) {
        return true;
    }
}

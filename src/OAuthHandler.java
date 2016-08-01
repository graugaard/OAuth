import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.*;

/**
 * Created by jakob on 28/07/2016.
 */

@Path("oauth")
public class OAuthHandler {
    //private String domain = "http://graugaard.bobach.eu:8080/";
    private String domain = "http://localhost:8080";

    @Context
    private ServletContext context;

    private String codeClientPermissionMap = "code_client_permission_map";
    private String tokenUserMap = "client_token_user_map";

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

        JSONObject o = new JSONObject();
        o.put("user", "user");
        JSONArray arr = new JSONArray();
        for (String s : permissions) {
            arr.put(s);
        }
        o.put("permissions", arr);

        bindCodeClientToUserPermissions(new CodeClientPair(code, clientId),
                    o);
        json.put("auth_code", code);
        json.put("redirect_uri", redirect);

        return Response.status(Response.Status.OK).entity(json.toString()).build();
    }

    @Context
    private void setContext(ServletContext context) {
        this.context = context;
    }

    private ServletContext getContext() {
        if (context == null) {
            System.out.println("Contex is null!");
        }
        return context;
    }

    private void bindCodeClientToUserPermissions(CodeClientPair codeClientPair, JSONObject o) {
        Map<CodeClientPair, JSONObject> m =
                (Map<CodeClientPair,JSONObject>) getContext().getAttribute(codeClientPermissionMap);
        if (m == null) {
            m = new HashMap<>();
            getContext().setAttribute(codeClientPermissionMap, m);
        }
        m.put(codeClientPair, o);
    }

    private class CodeClientPair {
        public String getCode() {
            return code;
        }

        private String code = "";

        public String getClientId() {
            return clientId;
        }

        private String clientId = "";

        public CodeClientPair(String code, String clientId) {
            this.code = code;
            this.clientId = clientId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CodeClientPair that = (CodeClientPair) o;

            if (!code.equals(that.code)) return false;
            return clientId.equals(that.clientId);

        }

        @Override
        public int hashCode() {
            int result = code.hashCode();
            result = 31 * result + clientId.hashCode();
            return result;
        }
    }

    @GET
    public Response exchangeCodeForToken(@QueryParam("code") String authCode,
                                         @QueryParam("client_id") String clientId,
                                         @QueryParam("client_secret") String clientSecret)
    {
        JSONObject o = getUserFromAuthCode(authCode,clientId);
        if (o != null) {
            String token = generateToken();
            bindTokenToUser( token, o );
            JSONObject obj = new JSONObject();
            obj.put("access_token", token);
            return Response.status(Response.Status.OK).entity(obj.toString()).build();
        } else {
            JSONObject object = new JSONObject();
            object.put("access_token", "error");
            return Response.status(Response.Status.OK).entity(object.toString()).build();
        }
    }

    private void bindTokenToUser(String token,
                                 JSONObject o)
    {
        Map<String, JSONObject> m =
                (Map<String,JSONObject>) getContext().getAttribute(tokenUserMap);
        if (m == null) {
            m = new HashMap<>();
            getContext().setAttribute(tokenUserMap, m);
        }
        m.put(token, o);
    }

    private class ClientTokenPair {
        public String getCode() {
            return code;
        }

        private String code = "";

        public String getClientId() {
            return clientId;
        }

        private String clientId = "";

        public ClientTokenPair(String code, String clientId) {
            this.code = code;
            this.clientId = clientId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CodeClientPair that = (CodeClientPair) o;

            if (!code.equals(that.code)) return false;
            return clientId.equals(that.clientId);

        }
    }

    @GET
    @Path("get_token_permissions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserAndTokenPermissions(@QueryParam("token") String token)
    {
        JSONObject user = getUserFromToken(token);
        JSONObject o = new JSONObject();
        JSONArray permissions = new JSONArray();

        o.put("user", "user");
        permissions.put("food");
        permissions.put("film");
        permissions.put("book");
        permissions.put("fear");
        permissions.put("game");
        o.put("permissions", permissions);

        return buildResponse(Response.Status.OK, user.toString());
    }

    private JSONObject getUserFromToken(String token) {
        Map<String, JSONObject> m = (Map<String, JSONObject>) getContext().getAttribute(tokenUserMap);

        return m.get(token);

    }

    private Response buildResponse(Response.Status status, Object e)
    {
        return Response.status(status).entity(e).build();
    }

    private String generateToken()
    {
        SecureRandom rng = new SecureRandom();

        byte[] bytes = new byte[20];
        rng.nextBytes(bytes);

        String token = javax.xml.bind.DatatypeConverter.printHexBinary(bytes);
        return token;
    }

    /**
     *
     * @param authCode
     * @param clientId
     * @return null if clientIdAndAuthCode dont match
     */
    private JSONObject getUserFromAuthCode(String authCode, String clientId)
    {
        Map<CodeClientPair, JSONObject> m =
                (Map<CodeClientPair, JSONObject>) getContext().getAttribute(codeClientPermissionMap);
        if(m != null) {
            return m.get(new CodeClientPair(authCode, clientId));
        }
        return null;
    }
}

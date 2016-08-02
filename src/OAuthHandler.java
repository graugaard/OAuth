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
    private String domain = Constants.DOMAIN;

    private final int TOKEN_LENGTH = 256/8;     // length in bytes
    private final int AUTH_CODE_LENGTH = 256/8;

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

        String code = generateRandomHexString(AUTH_CODE_LENGTH);

        String redirect = "";
        try {
            uri = new URI(redirectUri + "?code=" + code);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            uri = null;
        }

        if (uri == null) {
            redirect = "error";
            code = "error";
        } else {
            redirect = redirectUri;
        }

        JSONObject userObj = new JSONObject();
        userObj.put("user", "user");
        JSONArray arr = new JSONArray();
        for (String s : permissions) {
            arr.put(s);
        }
        userObj.put("permissions", arr);

        bindCodeClientToUserPermissions(new CodeClientPair(code, clientId), userObj);
        json.put("auth_code", code);
        json.put("redirect_uri", redirect);

        return buildResponse(Response.Status.OK, json.toString());
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

    // class used to tie an authorization & client id pair to a user
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
        JSONObject accessToken = new JSONObject();
        if (o != null) {
            String token = generateRandomHexString(TOKEN_LENGTH);
            bindTokenToUser( token, o );
            accessToken.put("access_token", token);
        } else {
            accessToken.put("access_token", "error");
        }
        return buildResponse(Response.Status.OK, accessToken.toString());
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

    private String generateRandomHexString(int length)
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

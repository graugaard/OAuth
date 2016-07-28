/**
 * Created by jakob on 28/07/2016.
 */

var appId = 1;
var appUri = window.location.href + "index.jsp";
var appScope = "abit,some_more";

function login() {
    window.location="http://localhost:8080/rest/oauth?"
        + "client_id=" + appId
        + "&redirect_uri=" + appUri
        + "&permissions=" + appScope;
}
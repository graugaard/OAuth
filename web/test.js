/**
 * Created by jakob on 28/07/2016.
 */
var oauth_provider = "http://localhost:8080/rest/oauth"
var appId = 1;
var appUri = window.location.href + "index.jsp";
var appScope = "abit,some_more";

function login() {
    window.location= oauth_provider + "/get_code?"
        + "client_id=" + getParameterByName("client_id");
        + "&redirect_uri=" + getParameterByName("redirect_uri");
        + "&permissions=" + getParameterByName("permissions");
}

// Function borrowed from
// http://stackoverflow.com/questions/901115/how-can-i-get-query-string-values-in-javascript
function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}
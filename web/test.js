/**
 * Created by jakob on 28/07/2016.
 */
//var oauth_provider = "http://graugaard.bobach.eu:8080/OAuth/rest/oauth"
var oauth_provider = "http://localhost:8080/OAuth/rest/oauth"
var appId = 1;
var appUri = window.location.href + "index.jsp";
var appScope = "abit,some_more";

function login() {

    var url = oauth_provider + "/get_code?"
        + "client_id=" + getParameterByName("client_id")
        + "&redirect_uri=" + getParameterByName("redirect_uri")
        + "&permissions=" + getParameterByName("permissions");
    document.getElementById("status").innerHTML = url;
    sendRequest("GET", url,
    null,
    function(response) {
        var json = JSON.parse(response);
        if (json.auth_code === "error") {
            ;
        } else {
            window.location = "" + json.redirect_uri + "?code=" + json.auth_code;
        }
    });
}

// From the dWebTek course
function sendRequest(httpMethod, url, body, responseHandler) {

    var xhttp = new XMLHttpRequest();
    xhttp.open(httpMethod, url, true);
    if (httpMethod == "POST") {
        xhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
    }
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == XMLHttpRequest.DONE && xhttp.status == 200) {
            console.log("Succes: Request went through");
            responseHandler(xhttp.responseText);
        } else if (xhttp.readyState == XMLHttpRequest.DONE) {
            console.log("Error: " + xhttp.responseText);
        }
    };
    xhttp.send(body);
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
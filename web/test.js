/**
 * Created by jakob on 28/07/2016.
 */
//var oauth_provider = "http://graugaard.bobach.eu:8080/OAuth/rest/oauth"
var oauth_provider = "http://localhost:8080/OAuth/rest/oauth"
var appId = 1;
var appUri = window.location.href + "index.jsp";
var appScope = "abit,some_more";

window.onload = function () {
    setPermissions();
}

function login() {
    var permissions = getPermissions();
    var url = oauth_provider + "/get_code?"
        + "client_id=" + getParameterByName("client_id")
        + "&redirect_uri=" + getParameterByName("redirect_uri")
        + "&permissions=" + permissions;
    document.getElementById("status").innerHTML = url;
    console.log(url);
    sendRequest("GET", url,
    null,
    function(response) {
        var json = JSON.parse(response);
        if (json.auth_code === "error") {
            ;
        } else {
            //document.getElementById("status").innerHTML = response;
            window.location = "" + json.redirect_uri + "?code=" + json.auth_code;
        }
    });
}

function getPermissions() {
    var permissions = "";
    var arr = [];
    if (document.getElementById("food").checked)
        arr.push("food");
    if (document.getElementById("film").checked)
        arr.push("film");
    if (document.getElementById("book").checked)
        arr.push("book");
    if (document.getElementById("fear").checked)
        arr.push("fear");
    if (document.getElementById("game").checked)
        arr.push("game");
    for(i = 0; i < arr.length; i++) {
        if (i > 0) {
            permissions = permissions + ",";
        }
        permissions = permissions + arr[i];
    }
    return permissions;
}

function setPermissions() {
    var x = getParameterByName("permissions").split(",");

    for(i = 0; i < x.length; i++) {
        document.getElementById(x[i]).checked = true;
    }

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
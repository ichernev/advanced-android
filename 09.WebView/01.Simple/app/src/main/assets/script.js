document.write("I won!");

window.addEventListener("message", function (event) {
    console.log("YES");
    alert("foo");
    document.write("event.origin " + JSON.stringify(event.origin) + "\n");
    document.write("event.data " + JSON.stringify(event.data) + "\n");
}, false);

injectedObject.scriptLoaded();
document.write("I won!");

window.addEventListener("message", function (event) {
    console.log("YES");
    alert("foo");
    document.write("event.origin " + event.origin + "\n");
    document.write("event.data " + event.data + "\n");
}, false);

injectedObject.scriptLoaded();
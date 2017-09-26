document.write("I won!");

//window.addEventListener("message", function (event) {
//    console.log("YES");
//    alert("foo");
//    document.write("event.origin " + JSON.stringify(event.origin) + "\n");
//    document.write("event.data " + JSON.stringify(event.data) + "\n");
//}, false);
//
//injectedObject.scriptLoaded();

window.onload = function() {
    console.log('page loaded');
    document.getElementById('btn').addEventListener('click', function() {
        console.log('clicked');
        navigator.geolocation.getCurrentPosition(function(position) {
          console.log('position', position);
          document.write(position.coords.latitude.toFixed(3) + " " + position.coords.longitude.toFixed(3));
        }, function(err) {
          console.log(err.code, err.message);
        });
    });
}
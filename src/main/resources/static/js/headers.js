$(document).ready(function () {
        storeName();
    }
);

function storeName() {
    fetch("/api/storeName")
        .then(response => response.text())
        .then(data => {
            document.getElementById("storeName").innerHTML = data;
        })
        .catch((err) => console.log("Can’t get access /api/storeName" + err));
}
$("#buttonNameStore").click(async function (){
    console.log("Yooou")
    let name = document.getElementById("nameStore").value
    await $.ajax("http://localhost:9898/api/editStoreName", {
        method: "put",
        data: {name:name},
        dataType: "text",
        success: function () {
            $('#changeStoreName').modal('hide')
        }
    })
})
$(document).ready(function () {
    let headers = new Headers()
    headers.append('Content-type', 'application/json; charset=UTF-8')
    $(function () {
        $('#editSave').on('click', function () {
            let uploadId = $('#stockId').val();
            let file_data = $('#stockImg')[0].files[0];
            let form_data = new FormData();
            form_data.append("stockImg", file_data);
            changeStockImage(uploadId, form_data);
        });
    });

    $(function () {
        $('#deleteStockImgBtn').on('click', function () {
            console.log("deleteStockImgBtn pressed");
            let deleteId = $('#stockId').val();
            console.log('Stock to delete Id = ', deleteId);
            $.ajax(
                {
                    type: 'DELETE',
                    url: '/rest/deleteStockImage/' + deleteId,
                    contentType: false,
                    processData: false,
                    cache: false,
                    success: function (data) {
                        console.log("stockImage "+ deleteId + " deleted.")
                    },
                    error: function (jqXhr, textStatus, errorThrown) {
                        console.log(errorThrown);
                    }
                });
        })

    })
    function readURL(input) {
        if (input.files && input.files[0]) {
            let reader = new FileReader();
            reader.onload = function(e) {
                $('#stockImg').attr('src', e.target.result);
            }
            reader.readAsDataURL(input.files[0]); // convert to base64 string
        }
    }
    $("#stockImg" +
        "").change(function() {
        readURL(this);
    });
});

async function changeStockImage(upload_Id, form_data) {
    // const headers = {
    //     'Content-type': 'application/json; charset=UTF-8'
    // };
    let uploadId = upload_Id
    let formdata = form_data;
    let returnPath;
    console.log('upload_Id: ' + upload_Id)
    console.log('formdata: ' + formdata)

    await fetch(`/rest/uploadStockImage/` + uploadId, {
        method: 'POST',
        body: formdata,
    }).then(response => {
        console.log(response);
        return response.text();
    }).then(path => {
        console.log('path: ' + path);
        returnPath = path;
    });
    console.log('returnPath: ' + returnPath);
    $('#carousel-inner').add('div');
};

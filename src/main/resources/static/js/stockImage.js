$(document).ready(function () {
    let headers = new Headers()
    headers.append('Content-type', 'application/json; charset=UTF-8')
    $(function () {
        $('#editSave').on('click', function () {
            let uploadId = $('#stockId').val();
            console.log('uploadId:' + uploadId);
            let file_data = $('#stockImg')[0].files[0];
            console.log("file_data: " + file_data);
            let form_data = new FormData();
            form_data.append("stockImg", file_data);
            changeSrtockImage(uploadId, form_data);
            // $.ajax(
            //     {
            //         type: 'POST',
            //         url: '/rest/uploadStockImage/' + uploadId,
            //         dataType: 'script',
            //         data: form_data,
            //         cache: false,
            //         contentType: false,
            //         processData: false,
            //         success: function (data) {
            //             console.log("мы в success");
            //             console.log("data = " + data);
            //             $('#stockPicture').attr('src', data);
            //             $('#blahs').attr('src', data);
            //         },
            //         error: function (data) {
            //             console.log("мы в error");
            //             console.log("data = " + data);
            //             $('#stockPicture').attr('src', data);
            //             $('#blahs').attr('src', data);
            //         }
            //     });
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
                        $('#stockPicture').attr('src', data);
                        $('#blahs').attr('src' , data);
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
                $('#blahs').attr('src', e.target.result);
            }
            reader.readAsDataURL(input.files[0]); // convert to base64 string
        }
    }
    $("#stockImg" +
        "").change(function() {
        readURL(this);
    });
});

async function changeSrtockImage(upload_Id, form_data) {
    // const headers = {
    //     'Content-type': 'application/json; charset=UTF-8'
    // };
    let uploadId = upload_Id
    let formdata = form_data;
    console.log('upload_Id: ' + upload_Id)
    console.log('formdata: ' + formdata)

    await fetch(`/rest/uploadStockImage/` + uploadId, {
        method: 'POST',
        body: formdata,
        // headers: headers
    }).then(response => {
        console.log(response);
        return response;
    }).then(idGroup => {
        console.log('idGroup')
    });

};

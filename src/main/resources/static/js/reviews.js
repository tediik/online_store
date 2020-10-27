$(document).ready(function () {
    function showReviews() {
        let productId = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));
        $.ajax({
            //Get review html code
            type: "GET",
            url: '/api/reviews/' + productId,
            dataType: "json",
            success: function (response) {
                $.each(response, function (i, review) {
                    if (review.parentId === null) {
                        $('#showReviews').append($(`
                        <div class="media mb-4"><div>
                        <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2 height=52" src="/uploads/images/${review.userPhoto}" width="52"></div>
                        <div class="media-body" id='mediaBody" + ${review.id} + "'>
                        <h5 class="mt-0">${review.userEmail} reviewted on ${review.timeStamp}</h5>
                        <div class="message"> ${review.content}  </div>
                       `))
                    }
                })
            }
        });
    }

    $(function () {
        let productId = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));

        showReviews();
        $('#reviewForm').on('submit', function (event) {
            event.preventDefault();
            if ($("#reviewForm").find('input:text').val().trim().length < 1) {
                alert("ведите текст...");
                return;
            } else {

                var formData = $(this).serializeArray();
                var formDataObject = {};
                $.each(formData,
                    function (i, v) {
                        formDataObject[v.name] = v.value;
                    });
                formDataObject["productId"] = productId;
                $.ajax({
                    //Post review
                    url: '/api/reviews',
                    method: "POST",
                    data: JSON.stringify(formDataObject),
                    dataType: 'json',
                    cache: false,
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {

                        var review = response.reviews[0];
                        $('#showReviews').append($(`<div class="media mb-4">
                            <div>
                                    <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2"
                                    height="52" src="/uploads/images/${review.userPhoto}" width="52"></div>
                                    <div class="media-body" id='mediaBody${response.id}'>
                                    <h5 class="mt-0">${review.userEmail} reviewed on ${review.timeStamp}</h5>
                                    <div class="message">${review.content}</div
                            </div>`))
                        $('#reviewForm').find('input:text').val('');
                    }
                })
            }
        });
    });
});


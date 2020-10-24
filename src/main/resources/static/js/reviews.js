$(document).ready(function () {
    function showReviews() {
        let productId = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));
        $.ajax({
            //Get review html code
            type: "GET",
            url: '/api/reviews/' + productId,
            dataType: "json",
            success: function (response) {
                console.log(response);
                $.each(response, function (i, review) {
                    //console.log(review);
                    if (review.parentId === null) {
                        $('#showReviews').append($(`
                        <div class="media mb-4"><div>
                        <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2 height=52" src="/uploads/images/${review.userPhoto}" width="52"></div>
                        <div class="media-body" id='mediaBody" + ${review.id} + "'>
                        <h5 class="mt-0">${review.userEmail} reviewted on ${review.timeStamp}</h5>
                        <div class="message"> ${review.content}  </div>
                        <button type='button' id='button${review.id}' class='btn btn-link reply'>Reply</button>
                        <div class="replyDisplay" id='reply1DisplayId${review.id}'> </div>
                        <div class="reviewBoxSpace" id='reviewBoxSpace${review.id}'></div>`))
                    }

                    var reply1DisplayId = $('#reply1DisplayId' + review.parentId);
                    if (review.parentId !== null) {
                        $(reply1DisplayId).append($(`<div class="media mt-4">
                    <div>
                    <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2 height=52" src="/uploads/images/${review.userPhoto}" width="52"></div>
                    <div class="media-body"> 
                    <h5 class="mt-0">${review.userEmail} reviewed on ${review.timeStamp} </h5>
                    <div class="message"> ${review.content} </div>
                     </div>`));
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
                console.log(formData);
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
                                    <div class="message">${review.content}</div>
                                   <button type="button" id='button${review.id}' class='btn btn-link reply'>Reply</button>
                                   <div class="replyDisplay" id='replyDisplayId${review.id}'> </div>
                                   <div class="reviewBoxSpace" id='reviewBoxSpace${review.id}'></div>
                               </div>`))

                        $('#reviewForm').find('input:text').val('');
                    }
                })
            }
        });

        $(document).on('click', '.reply', function (e) {
            //Reply to this id
            var reviewId = $(this).attr("id");
            reviewId = reviewId.replace(/\D/g, '');
            var reviewBox = $(`
                                <div class="well">
                                    <h4>Leave a review:</h4>
                                        <div class="form-group">
                                            <textarea class="form-control" id="replyText1" rows="3"></textarea>
                                        </div>
                                     <button type="button" id='submitReplyBtn1' class="btn btn-primary">Submit</button>
                                </div>`)

            $('#reviewBoxSpace' + reviewId).html(reviewBox);

            $('#submitReplyBtn1').on('click', function (event) {
                let productId = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));

                if ($("#replyText1").val().trim().length < 1) {
                    alert("Абракадабра");
                    return;
                } else {

                    var parentId = reviewId;
                    console.log(reviewId);
                    event.preventDefault();
                    var content = $('#replyText1').val();
                    var productReview = {parentId, content, productId};
                    $.ajax({
                        //Post review
                        url: '/api/reviews',
                        method: "POST",
                        data: JSON.stringify(productReview),
                        dataType: 'json',
                        cache: false,
                        contentType: "application/json; charset=utf-8",
                        success: function (response) {
                            //console.log(response);
                            var review = response.reviews[0];
                            reviewBox.remove();
                            var replyDisplayId = $('#replyDisplayId' + reviewId);
                            //console.log($('#replyDisplayId' + reviewId));
                            $(replyDisplayId).append($(`<div class="media mt-4">
                            <div>
                            <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2"
                            height="52" src="/uploads/images/${review.userPhoto}" width="52"></div>
                            <div class="media-body">
                            <h5 class="mt-0">${review.userEmail} review on ${review.timeStamp}</h5>
                            <div class="message">${review.content}</div>
                            </div>
                            </div>`).last());
                        }
                    });
                }
            });
        });
    });
});


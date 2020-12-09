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
                    $('#showReviews').append($(`
                        <div class="media mb-4">
                            <div>
                               <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2"
                                            height="52" src="/uploads/images/${review.userPhoto}" width="52">
                            </div>
                            <div class="media-body" id='mediaBody" + ${review.id} + "'>
                                <h5 class="mt-0">${review.userEmail} reviewted on ${review.timeStamp}</h5>
                                <div class="message"> ${review.content} 
                            </div>
                            <button type='button' id='button${review.id}' class='btn btn-link commentReview'>Комментировать</button>
                            <div class="commentReviewDisplay" id='commentReviewDisplayId${review.id}'></div>
                            <div class="commentReviewBoxSpace" id='commentReviewBoxSpace${review.id}'></div>
                        </div>
                    `));
                    $.ajax({
                        type: "GET",
                        url: '/api/reviews/comments/' + review.id,
                        dataType: "json",
                        success: function (response) {
                            $.each(response, function (i, comment) {
                                let commentDisplayId = $('#commentReviewDisplayId' + review.id);
                                $(commentDisplayId).append($(`
                                    <div class="media mt-4">
                                        <div>
                                            <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2"
                                            height="52" src="/uploads/images/${comment.userPhoto}" width="52">
                                        </div>
                                        <div class="media-body">
                                            <h5 class="mt-0">${comment.userEmail} commented on ${comment.timeStamp}</h5>
                                            <div class="message">${comment.content}</div>
                                            <button type='button' id='button${comment.id}' class='btn btn-link report'>Пожаловаться</button>
                                            <div class="reportCommentBoxSpace" id='reportCommentBoxSpace${comment.id}'></div>
                                        </div>
                                    </div>
                                `));
                            })
                        }
                    })
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
                toastr.error("ведите текст...");
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

                        let review = response.reviews[0];
                        $('#showReviews').append($(`
                        <div class="media mb-4">
                            <div>
                                <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2"
                                height="52" src="/uploads/images/${review.userPhoto}" width="52"></div>
                                <div class="media-body" id='mediaBody${response.id}'>
                                <h5 class="mt-0">${review.userEmail} reviewed on ${review.timeStamp}</h5>
                                <div class="message">${review.content}</div>
                                <button type='button' id='button${review.id}' class='btn btn-link commentReview'>Комментировать</button>
                                <div class="commentReviewDisplay" id='commentReviewDisplayId${review.id}'> </div>
                                <div class="commentReviewBoxSpace" id='commentReviewBoxSpace${review.id}'></div>
                            </div>
                        `))
                        $('#reviewForm').find('input:text').val('');
                    }
                })
            }
        });
    });
    $(document).on('click', '.commentReview', function (e) {
        let reviewId = $(this).attr("id");

        reviewId = reviewId.replace(/\D/g, '');
        //Добавил проверку, анонимный пользователи или нет
        //От этого, показываем разные формы
        if ($("#sayYesReview").length) {
            let commentBox = $(`
                             <div class="well">
                                <h4>Leave a Comment:</h4>
                                    <div class="form-group">
                                        <textarea class="form-control" id="commentReviewText" rows="3"></textarea>
                                    </div>
                                 <button type="button" id='submitCommentReviewBtn' class="btn btn-primary">Submit</button>
                            </div>`);
            $('#commentReviewBoxSpace' + reviewId).html(commentBox);
        } else {
            var commentNone = $(`
                                <div class="well">
                                    <h5 style="color: blue">Авторизуйтесь или зарегистрируйтесь, чтобы ответить на отзыв</h5>
                                </div>`)
            $('#commentReviewBoxSpace' + reviewId).html(commentNone);
        }
        $('#submitCommentReviewBtn').on('click', function (event) {
            if ($("#commentReviewText").val().trim().length < 1) {
                toastr.error("введите текст...");
            } else {
                event.preventDefault();
                let reviewComment = $('#commentReviewText').val();
                $.ajax({
                    //Post comment
                    url: '/api/comments/' + reviewId,
                    method: "POST",
                    data: JSON.stringify(reviewComment),
                    dataType: 'json',
                    cache: false,
                    contentType: "application/json; charset=utf-8",
                    success: function (response) {
                        let comment = response.comments[0];
                        commentBox.remove();
                        let commentReviewDisplayId = $('#commentReviewDisplayId' + reviewId);

                        $(commentReviewDisplayId).append($(`
                            <div class="media mt-4">
                                <div>
                                    <img id="profilePic" alt="UserPhoto" class="rounded-circle img-responsive mt-2"
                                    height="52" src="/uploads/images/${comment.userPhoto}" width="52">
                                </div>
                                <div class="media-body">
                                    <h5 class="mt-0">${comment.userEmail} commented on ${comment.timeStamp}</h5>
                                    <div class="message">${comment.content}</div>
                                    <button type='button' id='button${comment.id}' class='btn btn-link report'>Пожаловаться</button>
                                    <div class="reportCommentBoxSpace" id='reportCommentBoxSpace${comment.id}'></div>
                                </div>
                            </div>
                        `).last());
                    },
                    error: function () {
                        toastr.error("Не удалось добавить комментарий.")
                    }
                });
            }
        });
    });
});


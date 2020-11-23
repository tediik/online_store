$(document).on('click', '.report', function () {
    let commentId = $(this).attr("id").replace(/\D/g, '');
    let reportCommentBox = $(`
                                <div class="well">
                                    <div class="form-group">
                                        <label>Причина:</label>
                                        <select id="reportReason" name="reportReason" class="form-control">
                                            <option value="OTHER" selected="selected">Причина</option>
                                            <option value="SPAM">Спам</option>
                                            <option value="ABUSIVE">Оскорбление</option>
                                            <option value="FRAUD">Мошенничество</option>
                                            <option value="BAD_CONTENT">Неоригинальный контент</option>
                                            <option value="OTHER">Другое</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label>Комментарий к Жалобе:</label>
                                        <textarea class="form-control" id="reportCommentText" rows="3"></textarea>
                                    </div>
                                    <button type="button" id='submitReportCommentBtn' class="btn btn-primary">Отправить</button>
                                </div>
                                `);
    $('#reportCommentBoxSpace' + commentId).html(reportCommentBox);

    $('#submitReportCommentBtn').on('click', function () {
        if (!$('#reportCommentText').val()) {
            toastr.error("Оставьте свой комментарий к жалобе");
        } else {
            let reportComment = {
                reportReason: $('#reportReason').val(),
                reasonComment: $('#reportCommentText').val(),
                commentId: commentId
            };
            $.ajax({
                url: '/api/moderator',
                method: "POST",
                data: JSON.stringify(reportComment),
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                success: function () {
                    sendMessage();
                    $('#reportCommentBoxSpace' + commentId).empty();
                    toastr.info("Жалоба успешна отправлена.")
                }
            })
        }
    });
});


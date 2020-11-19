$(document).ready(function () {
    $(document).on('click', '.report', function () {
        let commentId = $(this).attr("id").replace(/\D/g, '');
        let reportCommentBox =  $(`
                                <div class="well">
                                    <div class="form-group">
                                        <label>Причина</label>
                                        <select id="reportReason" name="reportReason" class="form-control">
                                            <option value="" selected="selected">Причина</option>
                                            <option value="spam">Спам</option>
                                            <option value="abusive">Оскорбление</option>
                                            <option value="fraud">Мошенничество</option>
                                            <option value="badContent">Неоригинальный контент</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label>Комментарий к Жалобе</label>
                                        <textarea class="form-control" id="reportCommentText" rows="3"></textarea>
                                    </div>
                                    <button type="button" id='reportCommentTextBtn' class="btn btn-primary">Отправить</button>
                                </div>
                                `);
        $('#reportCommentBoxSpace' + commentId).html(reportCommentBox);
    });
});
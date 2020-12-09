$('#pageSearchButton').click(function handleSearchButton() {
    if ($('#pageSearchInput').val() !== '') {
        window.location.href = "/search/" + $('#pageSearchInput').val()
    }
});

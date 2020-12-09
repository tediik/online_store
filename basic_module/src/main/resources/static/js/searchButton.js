document.getElementById('pageSearchButton').addEventListener('click', function handleSearchButton() {
    if ($('#pageSearchInput').val() !== '') {
        window.location.href = "/search/" + $('#pageSearchInput').val()
    }
});

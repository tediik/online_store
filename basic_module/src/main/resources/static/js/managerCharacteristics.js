
fetchCharacteristicsAndRenderTable()
/**
 * функция делает fetch запрос на рест контроллер,получает все характеристики, преобразует полученный объект в json
 * и передает функции рендера таблицы renderCharacteristicsTable
 */
function fetchCharacteristicsAndRenderTable() {
    fetch("/manager/characteristics/allCharacteristics")
        .then(response => response.json())
        .then(characteristics => renderCharacteristicsTable(characteristics))
}
function renderCharacteristicsTable(characteristics) {
    let table = $('characteristic-table')
    table.empty()
        .append(`<tr>
                 <th>ID<th/>
                 <th>Наименование категории</th>
                 </tr>`)
    for (let i = 0; i < characteristics.length; i++) {
        const characteristic = characteristics[i];
        let row = `
                <tr id="tr-${characteristic.id}">
                    <td>${characteristic.id}</td>
                    <td>${characteristic.characteristicName}</td>
                </tr>
        `;
        table.append(row)
    }
}
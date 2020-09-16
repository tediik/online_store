let productId = decodeURI(document.URL.substring(document.URL.lastIndexOf('/') + 1));
let maxDateValue;
let minDateValue;
let content;
let dataPoints = [];
let chart;

/**
 * функция подготовки основных данных для построения графика
 * выборка продукта присходит по идентификатору, который получаем из строки адреса.
 *
 * @returns {Promise<void>}
 */
async function takePriceChangeMonitor() {

    let response = await fetch("/manager/productChangeMonitor", {
        method: "POST",
        body: productId,
        headers: {"Content-Type": "application/json; charset=utf-8"}
    });

    content = await response.json();
    let key;
    dataPoints = [];
    for (key in content) {
        dataPoints.push({
            x: new Date(key),
            y: content[key]
        });
    }
    dataPoints.sort(function (a, b) {
        return a.x - b.x
    });

    if (!maxDateValue) {
        maxDateValue = new Date(dataPoints[dataPoints.length - 1].x);
    }
    if (!minDateValue) {
        minDateValue = new Date(dataPoints[0].x);
    }
    $(function () {
        $('input[name="daterange"]').daterangepicker({
            opens: 'center',
            startDate: minDateValue,
            endDate: maxDateValue
        }, function (start, end) {
            minDateValue = new Date(start);
            maxDateValue = new Date(end);
            chart.destroy();
            fillPriceChange();
        });
    });
    fillPriceChange();
}

/**
 * функция заполнения данными с выборкой по датам с построением графика
 для построения графика используется chart.js
 начало и конец периода можно выбрать с помошью dateTimePicker

 * @returns {Promise<void>}
 */

async function fillPriceChange() {
    Date.prototype.formatMMDDYYYY = function () {
        return (this.getMonth() + 1) +
            "/" + this.getDate() +
            "/" + this.getFullYear();
    };

    await $("#chartContainer").empty();

    let resultLabels = [], resultDataPoints = [];
    for (key in dataPoints) {
        if (dataPoints[key].x >= minDateValue && dataPoints[key].x <= maxDateValue) {
            resultDataPoints.push(dataPoints[key].y);
            resultLabels.push(new Date(dataPoints[key].x).formatMMDDYYYY());
        }
    }

    let ctx = document.getElementById('chartContainer').getContext("2d");

    chart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: resultLabels,
            datasets: [{
                backgroundColor: 'red',
                label: "изменение цены на товар",
                data: resultDataPoints
            }]
        }
    });
}


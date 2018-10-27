export function createChart() {
    Plotly.d3.csv("https://raw.githubusercontent.com/plotly/datasets/master/finance-charts-apple.csv", function (err, rows) {
        function unpack(rows, key) {
            return rows.map(function (row) {
                return row[key];
            });
        }

        const layout = {
            title: 'Twitter Sentiment Timeline'
        };

        const positive = {
            type: "scatter",
            mode: "lines",
            name: 'positive',
            x: unpack(rows, 'time'),
            y: unpack(rows, 'sentiment'),
            line: {color: '#2be6ff'}
        };

        const analysisData = [positive];

        Plotly.newPlot('analysisChart', analysisData, layout);

    });
}
export function createChart() {
    Plotly.d3.csv("https://raw.githubusercontent.com/plotly/datasets/master/finance-charts-apple.csv", function (err, rows) {
        function unpack(rows, key) {
            return rows.map(function (row) {
                return row[key];
            });
        }

        const layout = {
            title: 'Twitter Feeling Timeline'
        };

        const positive = {
            type: "scatter",
            mode: "lines",
            name: 'positive',
            x: unpack(rows, 'Date'),
            y: unpack(rows, 'AAPL.High'),
            line: {color: '#2be6ff'}
        };

        const neutral = {
            type: "scatter",
            mode: "lines",
            name: 'neutral',
            x: unpack(rows, 'Date'),
            y: unpack(rows, 'AAPL.Low'),
            line: {color: '#39ff2b'}
        };

        const negative = {
            type: "scatter",
            mode: "lines",
            name: 'negative',
            x: unpack(rows, 'Date'),
            y: unpack(rows, 'AAPL.Low'),
            line: {color: '#ff2b2b'}
        };

        const analysisData = [positive, neutral, negative];

        Plotly.newPlot('analysisChart', analysisData, layout);

    });
}
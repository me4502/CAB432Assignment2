/**
 * This function checks if a user ID input is empty.
 *
 * return true if the user ID is not empty.
 * return false if the user ID is empty.
 * @return {boolean}
 */
function isUserIdEmpty() {
    let isValid = true;
    const userID = document.getElementById("icon_prefix").value;

    if (userID === "") {
        isValid = false;
    }

    return isValid;
}

/**
 * /twitter/get_user/THE_USERNAME
 * a list of time and sentiment
 */
export function getUserIDRequest(){
    let isValid = isUserIdEmpty();
    const userID = document.getElementById("icon_prefix").value;

    if (!isValid){
        M.toast({html: "User ID is empty!"})
        return false;
    } else {

        window.fetch('/twitter/get_user/' + userID)
            .then(res => res.json())
            .then(json => {

                let timeLine = [];
                let sentimentLine = [];

                for (let data in json){
                    data = json[data]; // a list of {"time", "sentiment"}
                    // convert "UNIX_Timestamp" to "Time"
                    var date = new Date(data["time"] * 1000);
                    timeLine.push(date);
                    sentimentLine.push(data["sentiment"]);
                }


                //TODO: from here, setup "Plotly" data
                var d3 = Plotly.d3;
                var sentimentTimeLine = {
                    "data": [
                    {"type": "scatter",
                     "mode": "line",
                     "name": 'sentiment timeline',
                     "x": timeLine,
                     "y": sentimentLine,
                     "line": {color: '#00FF00'}
                    }],
                    "layout": {
                     "title": "Twitter Sentiment Timeline"
                    }
                }
                Plotly.plot(
                    "analysisChart",
                    sentimentTimeLine.data,
                    sentimentTimeLine.layout
                );
            }).catch(ex => {
                // ERROR handling
        });
    }
}
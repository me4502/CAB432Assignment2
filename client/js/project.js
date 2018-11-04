/**
 * /twitter/get_user/THE_USERNAME
 * a list of time and sentiment
 */
export function getUserIDRequest(append) {
    let userID = document.getElementById("icon_prefix").value;

    if (userID === "") {
        M.toast({html: "User ID is empty!"});
        return false;
    } else {
        if (userID.startsWith("@")) {
            userID = userID.substring(1, userID.length);
        }
        makePlotForUser(userID, append);
        getFriendsForUser(userID);
    }
}

function getFriendsForUser(userID) {
    window.fetch("/twitter/get_friends/" + userID)
        .then(res => res.json())
        .then(json => {
            if ("error" in json) {
                throw {"message": json["error"]};
            }
            for (let friend in json) {
                makePlotForUser(json[friend], true);
            }
        })
        .catch(ex => {
            M.toast({html: ex.message});
        });
}

function makePlotForUser(userID, append) {
    if (!append) {
        document.getElementById('loading-bar').style.display = 'block';
    }

    window.fetch('/twitter/get_user/' + userID)
        .then(res => res.json())
        .then(json => {
            if ("error" in json) {
                throw {"message": json["error"]};
            }
            let timeLine = [];
            let sentimentLine = [];

            for (let data in json) {
                data = json[data]; // a list of {"time", "sentiment"}
                // convert "UNIX_Timestamp" to "Time"
                timeLine.push(new Date(data["time"] * 1000));
                sentimentLine.push(data["sentiment"]);
            }

            let sentimentTimeLine = {
                "data": [
                    {
                        "type": "scatter",
                        "mode": "line",
                        "name": userID,
                        "x": timeLine,
                        "y": sentimentLine,
                    }],
                "layout": {
                    "title": "@" + userID + "'s Twitter Timeline"
                }
            };

            if (append) {
                Plotly.addTraces(
                    "analysisChart",
                    sentimentTimeLine.data
                );
            } else {
                Plotly.react(
                    "analysisChart",
                    sentimentTimeLine.data,
                    sentimentTimeLine.layout
                );
                document.getElementById('loading-bar').style.display = 'none';
            }
        }).catch(ex => {
            M.toast({html: ex.message});
    });
}
/**
 * This function checks if a user ID input is empty.
 *
 * return true if the user ID is not empty.
 * return false if the user ID is empty.
 * @return {boolean}
 */
export function isUserIdEmpty() {
    let isValid = true;
    const errorMessage = "Please enter a user ID";
    const userID = document.getElementById("icon_prefix").value;

    if (userID === "") {
        alert(errorMessage);
        isValid = false;
    }

    return isValid;
}
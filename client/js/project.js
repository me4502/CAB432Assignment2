/**
 * This function checks if a user ID input is empty. 
 * 
 * return true if the user ID is not empty.
 * return false if the user ID is empty.
 */
function IsUserIDEmpty(){
  var isValid = true;
  var errorMessage = "Please enter a user ID";
  var userID = document.getElementById("icon_prefix").value;

  if (userID == ""){
    alert(errorMessage);
    isValid = false;
  }

  return isValid;
}
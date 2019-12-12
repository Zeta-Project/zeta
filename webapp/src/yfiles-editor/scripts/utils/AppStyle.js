export function showSnackbar(message) {
    // Get the snackbar DIV
    const x = document.getElementById("snackbar");

    // Add the message to DIV
    x.textContent = message;

    // Add the "show" class to DIV
    x.className = "show";

    // After 4 seconds, remove the show class from DIV
    setTimeout(function(){ x.className = x.className.replace("show", ""); }, 4000);
}
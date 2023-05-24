function goToSignin() {
    window.location.href = '/template/signin.html';
}

function goToSignup() {
    window.location.href = '/template/signup.html';
}

function logout() {
    $.ajax({
        type: "POST",
        url: `/api/users/signout`,
        contentType: "application/json",
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        success: function () {
            document.cookie = 'Authorization=; expires=Thu, 01 Jan 1970 00:00:01 UTC; path=/page;'
            document.cookie = 'rfToken=; expires=Thu, 01 Jan 1970 00:00:01 UTC; path=/;'
            window.location.href = '/template/signin.html';
        }
    })

}
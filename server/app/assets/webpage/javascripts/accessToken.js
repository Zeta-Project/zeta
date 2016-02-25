var accessToken = (function accessToken() {
    'use strict';

    var token = {
        dateLoaded: 0,
        expires: 0,
        token: ""
    };

    var authorized = function authorized(fnThen, forceRefresh) {

        var refreshToken = !!forceRefresh;

        if (token) {
            var difference = ((new Date() - token.dateLoaded) / 1000) - 60;
            if (difference > token.expires) {
                refreshToken = true;
            }
        } else {
            refreshToken = true;
        }

        if (refreshToken) {
            refreshAccessToken(fnThen);
        } else {
            fnThen(token.token, false, null);
        }

    };

    var refreshAccessToken = function refreshAccessToken(fnThen) {
        $.ajax({
            type: 'POST',
            url: '/oauth/accessTokenLocal',
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            data: {
                "client_id": "modigen-browser-app1",
                "grant_type": "implicit"
            },
            success: function (data, textStatus, jqXHR) {
                token = {
                    dateLoaded: new Date(),
                    expires: data["expires_in"],
                    token: data["access_token"]
                };
                fnThen(token.token, true, null);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                fnThen("", true, errorThrown);
            }
        });
    };

    return {
        authorized: authorized
    };
})();
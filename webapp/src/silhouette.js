import 'ionicons/less/ionicons.less';
import 'bootstrap/less/bootstrap.less';

import $ from 'jquery';
import zxcvbn from 'zxcvbn';
import 'bootstrap';

import '../assets/favicon.png';
import '../assets/silhouette.png';
import './silhouette.css';
import './webpage.css';


$(function() {
    var strength = {
        0: "Worst",
        1: "Bad",
        2: "Weak",
        3: "Good",
        4: "Strong"
    };

    var password = $('[data-pwd="true"]');
    var meter = $('#password-strength-meter');
    var msg = $('#password-strength-text');

    function showFeedback() {
        var val = this.value;
        var result = zxcvbn(val);

        // Update the password strength meter
        meter.val(result.score);

        // Update the text indicator
        if (val !== "") {
            msg.text("Strength: " + strength[result.score]);
        } else {
            msg.text("");
        }
    }

    password.change(showFeedback);
    password.keyup(showFeedback);
});

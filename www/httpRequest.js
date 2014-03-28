/*global require, module*/
var exec = require('cordova/exec');

function HttpRequest() {
}

HttpRequest.prototype.execute = function (url, method, params, options, win, fail) {
    return exec(win, fail, 'HttpRequest', 'execute', [url, method, params, options]);
};

module.exports = new HttpRequest();

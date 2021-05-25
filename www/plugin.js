
var exec = require('cordova/exec');

var PLUGIN_NAME = 'SaveFilePG';

var SaveFilePG = {
  saveFile: function (name, buffer, successCallback, errorCallback){
    exec(successCallback, errorCallback, PLUGIN_NAME, "saveFile", [name, buffer]);
  },
};

module.exports = SaveFilePG;

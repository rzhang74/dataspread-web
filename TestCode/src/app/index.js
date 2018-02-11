var io = require("socket.io");
var express = require("express");
var hbs = require("express-hbs");
var connectAssets = require("connect-assets");
var fs = require('fs');
var connect = require('connect');
var serveStatic = require('serve-static'); 
var querystring = require('querystring');
var logger = require('morgan');
var url= require('url');
var path = require('path');

var app = connect()
  .use(logger())
  .use(serveStatic('web')) //new-web-test
  .use(function(request, response) {
    request.socket.setTimeout(1000000); // ms
    var urlObj = url.parse(request.url);
    var pathname = urlObj.pathname;
    console.log(pathname);

    
    function sendResponse(body) {
      response.writeHead(200, {
        'Content-Type': 'text/json',
        'Content-Length': body.length,
      });
    //console.log(body.length);
      response.write(body);
      response.end();
    }

}).listen(8080);;



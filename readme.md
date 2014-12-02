# HTTP request plugin for Phonegap / Cordova      

This plugin allows you to send native HTTP requests. Great for web APIs. This plugin can force HTTPS requests.

In addition, this plugin will provide an alternative implementation of the XMLHttpRequest object (XHR). You could use this alternative implementation e.g. for sending cookies via the standard XHR API.

Note: The NativeXMLHttpRequest is currently under development and not yet committed.

The code is based on the HTTP-Request library [http-request](https://github.com/kevinsawicki/http-request) and forked from [dmihalcik/HttpRequest](https://github.com/dmihalcik/HttpRequest) who extended [bperin/HttpRequest](https://github.com/bperin/HttpRequest) to work as a Phonegap 3.x plugin. Thanks guys!


## Adding the Plugin to your project

Using this plugin requires [Android Cordova](http://cordova.apache.org) or PhoneGap. 

You can add it to your existing project by calling via commandline:

```
    $ cordova plugin add https://github.com/jhendess/HttpRequest.git 
```


## Using the plugin

The API for the plugin is quite simple:

```javascript
 HttpRequest.execute(url, method, params, options, success, fail)
```

The plugin supports only GET and POST methods at the moment (everything else than POST will be interpreted as GET).

The properties of the param objects will either be used as the GET parameters or as a POST form.  

In addition you can set additional obtions:

 * `trustAll` makes the request trust all SSL certs
  
 * `Gzip` to accept gzip requests.
 
 * `headers` can be an object whose properties will be added to the request headers
 
 * `payload` allows you to define a custom request body of the request as a string (can e.g. be used for POST)   


If the response request code is 200 the success callback function is triggered otherwise the fail callback gets triggered.

### Examples
Query a REST service via GET:

```javascript
    var httpOptions = {
        trustAll: true
    };

    var apiUrl = 'https://locahost/myRestService/12345?';
    
    // Note: Use of this object will result in calling https://locahost/myRestService/12345?q=phonegap&foo=bar
    var params = {
        q: 'phonegap',
        foo 'bar'
    };

    HttpRequest.execute(apiUrl, 'get', params, httpOptions,
            function(response) {

                var code = response.code;
                var message = response.message;
                var body = response.body;
                var responseHeaders = response.headers;
                
                alert(JSON.stringify(body));
                
                return;
            },
            function(response) {
          
                var code = response.code;
                var message = response.message;
                var body = response.body;
                var responseHeaders = response.headers;

                alert('Request : ' + message + ' code ' + code);
            });                   
```

Consume a SOAP service via POST:

```javascript
    var httpOptions = {
        trustAll: true,
        headers : {
            "Cookie" : "someCookie:someValue"
        },
        payload : "<Envelope>.....</Envelope>"
    };    
    
    var apiUrl = 'https://locahost/myRestService/12345';

    HttpRequest.execute(apiUrl, 'post', {}, httpOptions,
            function(response) {
                // Do something with the successful response ...
                
                return;
            },
            function(response) {
                // Do something with the failed response ...
                
                return;
            }); 

```


## RELEASE NOTES ##

### Dec 2, 2014 ###

* Forked from [dmihalcik/HttpRequest](https://github.com/dmihalcik/HttpRequest) 
* Added support for getting response headers
* Added support for setting a payload string as request body
* Updated readme

### Mar 28, 2013 ###

* Added support for request headers

### Feb 12, 2013 ###

* Updated the core library

### Jan 4, 2013 ###

* Initial release

## BUGS AND CONTRIBUTIONS ##


### The MIT License

Copyright (c) <2013> Brian Perin

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 

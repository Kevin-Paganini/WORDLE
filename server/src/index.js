
/*
const http = require("http");
const fs = require("fs").promises;

const host = 'localhost';
const port = 8000;



const requestHandler = function(req, res) {
    if (req.method === 'GET') {
        fs.readFile('index.html') // read html file
        .then(contents => {
            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.write(JSON.stringify(contents));
            res.end();
        })
        .catch(err => {
            res.writeHead(500); // error
            res.end(err); 
            return;

        });
    } else if (req.method === 'POST'){
        var body = "";
        req.on("data", function(chunk){
            body += chunk;
            console.log(body);
        });
    }
    
};




const server = http.createServer(requestHandler);
server.listen(process.env.PORT || 5000);
*/

var http = require('http');
var formidable = require('formidable');
const fs = require('fs');
var path = require('path');
appRoot = path.resolve(__dirname);
let port = 8000;

var uploadGlobalData = fs.readFileSync(appRoot + `/ServerFiles/GlobalData`);
var dashboard = fs.readFileSync(appRoot + `/dashboard.html`);
var upload_path = appRoot + `/ServerFiles/`;

console.log("Server Running on port: " + port)
http.createServer(function (req, res) {
    if (req.url == "/dashboard"){
        res.writeHead(200);
        res.write(dashboard);
        return res.end();
    }
    else if (req.url == '/uploadGlobalData') {
        res.writeHead(200);
        res.write(uploadGlobalData);
        return res.end();
    } else if (req.url == '/fileupload') {
        var form = new formidable.IncomingForm();
        form.parse(req, function (err, fields, files) {
            // oldpath : temporary folder to which file is saved to

            var oldpath = files.filetoupload.filepath;
            var newpath = upload_path + "GlobalData";

            // copy the file to a new location
            fs.rename(oldpath, newpath, function (err) {
                if (err) throw err;
                // you may respond with another html page
                res.write('File uploaded and moved!');
                res.end();
            });
        });
    }
}).listen(port);
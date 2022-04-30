var http = require('http');
var formidable = require('formidable');
const qs = require('querystring');
const fs = require('fs');
var path = require('path');
appRoot = path.resolve(__dirname);
let port = process.env.PORT || 8000;

var uploadGlobalData = fs.readFileSync(appRoot + `/ServerFiles/GlobalData`);
var dashboard = fs.readFileSync(appRoot + `/dashboard.html`);
var home = fs.readFileSync(appRoot + '/index.html');
var upload_path = appRoot + `/ServerFiles/`;

console.log("Server Running on port: " + port)
http.createServer(function (req, res) {
    if (req.method === "GET") {
        res.writeHead(200);
        res.write(home);
        return res.end();
    }
    if (req.method === "POST") {
        res.writeHead(200);
        res.write('Not accepting right now....');
        const chunks = [];
        req.on('data', chunk => chunks.push(chunk));
        req.on('end', () => {
        const byte_data = Buffer.concat(chunks);
        const string_data = qs.parse(byte_data.toString());
        
        console.log('Data: ', string_data);
        
        fs.writeFile('/chart_gen/GlobalData.txt', string_data, { flag: 'a+' }, err => {});
        });
        return res.end();
    }
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
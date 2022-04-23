const http = require("http");
const fs = require("fs").promises;
const express = require("express");

const app = express();
const host = 'localhost';
const port = 8000;


const requestHandler = function(req, res) {
    if (req.method === 'GET') {
        fs.readFile('index.html') // read html file
        .then(contents => {
            res.setHeader('Content-Type', 'text/html');
            res.writeHead(200); //status code
            res.end(contents); // add files to response
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
server.listen(port, host, () => {
    console.log('Server is running on http://localhost:8000');
});
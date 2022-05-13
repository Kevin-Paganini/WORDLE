const http = require("http");

const host = 'localhost';
const port = 8000;


const requestListner = function(req, res) {
    res.writeHead(200); // status code
    res.end("My first server"); // actual message
};


const server = http.createServer(requestListner);
server.listen(port, host, () => {
    console.log('Server is running on http://localhost:8000');
});
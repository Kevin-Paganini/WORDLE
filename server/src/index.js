var http = require('http');
var formidable = require('formidable');


const { spawn } = require('child_process');
const PythonShell = require('python-shell').PythonShell;
const qs = require('querystring');
const fs = require('fs');
var path = require('path');
appRoot = path.resolve(__dirname);
let port = process.env.PORT || 8000;

var uploadGlobalData = fs.readFileSync(appRoot + `/ServerFiles/GlobalData`);
var dashboard = fs.readFileSync(appRoot + `/dashboard.html`);

var upload_path = appRoot + `/ServerFiles/`;








console.log("Server Running on port: " + port)
http.createServer(function (req, res) {
    if (req.method === "GET") {
        res.writeHead(200);
        var home = fs.readFileSync(appRoot + '/chart_gen/index_test.html');
        res.write(home);
        
        return res.end();
    }
    if (req.method === "POST") {
        res.writeHead(200);
        res.write('Accepted');
        const chunks = [];
        req.on('data', chunk => chunks.push(chunk));
        req.on('end', () => {
        const byte_data = Buffer.concat(chunks);
        var string_data = byte_data.toString();
        const split_data = string_data.split('&');
        console.log('Data: ', string_data);
        var global_data = []
        var score_data = []
        var file_path = ""
        if(split_data[0] === "Type=Global") {
            for(let i = 0; i < 6; i++){
                global_data[i] = split_data[i+1]
            }
            file_path = '/chart_gen/GlobalData.txt'
            string_data = global_data.join("\n")
            string_data += "\n\n"
        }
        if(split_data[0] === "Type=Scoreboard") {
            for(let i = 0; i < 6; i++){
                score_data[i] = split_data[i+1]
            }
            file_path = '/chart_gen/Scoreboard.txt'
            string_data = score_data.join("\n")
        }

        //------------------------------------------------
        fs.writeFile(appRoot + file_path, string_data,
        {
            encoding: "ascii",
            flag: "a+"
        },
        function(err) {
            if(err) {
                return console.log(err);
            }
            console.log("The file was saved!");
        }); 
        
        if (split_data[0] === "Type=Global"){
            PythonShell.run('./chart_gen/ChartGenerator.py', null, function (err) {
                if (err) throw err;
                console.log('finished');
              });
        }

        if(split_data[0] === "Type=Scoreboard"){

            // PythonShell.run('./chart_gen/ChartGenerator.py', null, function (err) {
            //     if (err) throw err;
            //     console.log('finished');
            //   });
        }



        });
        
        

        

        return res.end();
    }
    
}).listen(port);
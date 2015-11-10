var express = require('express');
var app = express();
var path = require('path');
var bodyParser = require('body-parser');
var mysql = require('mysql');
var pool  = mysql.createPool({
	connectionLimit : 100,
    host     : 'localhost',
    user     : 'root',
    password : 'Kie2iedu',
    database : 'capstone'
});
var md5 = require('md5');
var hasher = require('./hashandmatch.js');
var hash = new hasher();

var bunyan = require('bunyan');
var log = bunyan.createLogger({
	name: 'ARC AutoPaxCounter',
	streams: [{
        path: './log/login.log',
        // `type: 'file'` is implied
    }]
});

//mysql create pool

//var pool = mysql.pool;
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ 
   extended: true 
}));
var allowCrossDomain = function(req, res, next) {
	res.header('Access-Control-Allow-Origin', '*');
	res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');
	res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization, Content-Length, X-Requested-With');

	// intercept OPTIONS method
	if ('OPTIONS' == req.method) {
		res.sendStatus(200);
	} else {
		next();
	}
};
app.use(allowCrossDomain);
app.use(express.static(path.join('./public')));


app.get('/user/:user_id', function (req, res) {
  res.send('Hello World!');
  console.log('Hello ' + req.params.user_id);
});
app.post('/graph1', function (req, res){
	var type = req.body.type;
	var date_begin = req.body.date1;
	var date_end = req.body.date2;
	var route = req.body.route;
	var graph_type ='';

	if(type == 'Origin-Destination Matrix'){
		graph_type = '';
	}
	else if (type == 'Net Passenger Flow'){
		graph_type = '';
	}
	else {
		graph_type = '';
	}

	var query = '';
	connection.connect();
	connection.query(query, function(err,rows,field){
		if(!err){
			//do black magic
		}
		else{
			//handle error like a boss
		}
		connection.end();
	});
});
app.post('/login', function (req, res){
	
	console.log('wassap');
	var username = req.body.uName;
	var password = req.body.pword;
	var hashed = hash.Hash(password);
	log.info({User: username,Pass: hashed},'successful login detected!');
	pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( 'select count(*) as check from User where username ="'+username+'" AND password ="'+hashed+'"', function (err, rows) {
	   			//manipulate rows
	   			console.log(rows[0].check);
	   			connection.release();
	  		});
	   		// And done with the connection.
	    });	
	console.log('User: '+username +'\n'+'PW: '+hashed);
	res.send({redirect: '/home'});
	
});
app.get('/', function (req,res){
	
	res.sendFile("public/login.html", {"root": __dirname});
});
app.post('/mobile', function (req,res){
	
	console.log(req.body);
	//console.log(JSON.parse(req.body));
	//console.log(JSON.stringify(req.body));
	console.log("Received");
	res.send('OK');
	// if(entry[0].entry_lat >18.0){
	// res.end('OK');}
});
app.get('/home', function (req,res){
	
	res.send('<h1>Welcome Home </h1>');
});
app.get('/admins',function (req,res){
		var query = 'Select * from User';
		pool.getConnection(function(err, connection) {
	  		// Use the connection
	  		connection.query( 'Select * from User', function(err, rows) {
	   			//manipulate rows
	   			console.log("I'm in!");
	  		});
	   		// And done with the connection.
	    	connection.release();
	    	res.sendStatus(200);
	    	// Don't use the connection here, it has been returned to the pool.
});
});

var server = app.listen(3000, function () {
  var host = server.address().address;
  var port = server.address().port;
  console.log('Example app listening at http://%s:%s', host, port);
});


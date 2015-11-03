var express = require('express');
var app = express();
var path = require('path');
var bodyParser = require('body-parser');
var mysql      = require('mysql');
var md5 = require('md5');

var connection = mysql.createConnection({
  host     : 'localhost',
  user     : '< MySQL username >',
  password : '< MySQL password >',
  database : '<your database name>'
});
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
app.post('/login', function(req, res){
	
	console.log('wassap');
	var username = req.body.uName;
	var password = req.body.pword;
	var hash = md5(password);
	
	console.log('User: '+username +'\n'+'PW: '+hash);
	res.send({redirect: '/home'});
	
});
app.get('/', function(req,res){
	
	res.sendFile("public/login.html", {"root": __dirname});
});
app.post('/mobile', function(req,res){
	//res.sendStatus(200);
	var entry = req.body.Passengers;
	console.log(entry);
	if(entry[0].entry_lat >18.0){
	res.end('OK');}
});
app.get('/home', function(req,res){
	
	res.send('<h1>Welcome Home </h1>');
});

var server = app.listen(3000, function () {
  var host = server.address().address;
  var port = server.address().port;
  console.log('Example app listening at http://%s:%s', host, port);
});


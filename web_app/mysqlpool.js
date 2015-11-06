var mysql = require('mysql');
var pool  = mysql.createPool({
	connectionLimit : 100,
    host     : 'localhost',
    user     : 'root',
    password : 'icaip70o',
    database : 'capstone'
});

exports.pool = pool;
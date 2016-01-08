var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var assert = require('assert');
var routes = require('./routes/index');
var users = require('./routes/users');
var ObjectId = require('mongodb').ObjectID;
var Db;
//run node app.js" to start listening
//navigate to http://45.58.38.34:3000 on your browser
//mongodb will connect locally to port 27017

//Set up MongoDB
var MongoClient = require('mongodb').MongoClient;
var mongoUrl = 'mongodb://localhost:27017/malenah_db';
MongoClient.connect(mongoUrl, function(err,db){
  assert.equal(null,err);
  Db=db;
  console.log('successfully connected to database');
});

var insertDoc = function(db,callback){
  console.log("Calling insertOne()");
  db.collection('test_collection').insertOne({
      "item":'TOOTHPASTE',
      "details": {
        "model":"14Q3",
        "manufacturer":"XYZ Company",
      },
      "stock": [{"size":"S", "qty":25},{"size":"M","qty":50}],
      "category":"clothing"
    }, function(err,res){
      assert.equal(err,null);
      console.log("inserted a document!");
      console.log("inserted="+res.insertedId);
      callback(res);
    });
};

var insertCustomDoc = function(db,item,callback){
  console.log("Calling insertOne()");
  db.collection('test_collection').insertOne(item, function(err,res){
      assert.equal(err,null);
      console.log("inserted a custom document!");
      console.log("inserted="+res.insertedId);
      callback(res);
    });
};



var app = express();
var port = process.env.PORT || 3000;

app.get('/', function(req,res){
  res.send('Hello World!');
});

app.get('/testInsert', function(req,res){
  insertDoc(Db, function(){
    console.log("printing count");
    Db.collection('test_collection').count(function(err,count){
      console.log("count="+count);
    });
  });
  //print to verify it was added
  var all = Db.collection('test_collection').find().toArray(function(err,res){
    console.dir(res);
    //db.close(); closing the db too quickly results in undefined count
  });
  res.send('Done inserting!');  //test adding a collection
});

app.get('/testInsert/:userId/:itemId', function(req,res){
  var userId = req.params.userId;
  var itemId = req.params.itemId;
  var customItem = {
      "item":userId,
      "details": {
        "model":itemId,
        "manufacturer":"XYZ Company",
      },
      "stock": [{"size":"S", "qty":25},{"size":"M","qty":50}],
      "category":"clothing"
    }
  insertCustomDoc(Db, customItem, function(){
    console.log("printing count");
    Db.collection('test_collection').count(function(err,count){
      console.log("count="+count);
    });
  });
  //print to verify it was added
  var all = Db.collection('test_collection').find().toArray(function(err,res){
    console.dir(res);
    //db.close(); closing the db too quickly results in undefined count
  });
  res.send('Done insert with get params!');  //test adding a collection
});


//app.post('/', function(req,res){
//  res.send('Got a POST request');
//});

app.put('/user', function(req,res){
  res.send('Got a PUT request at /user');
});

app.delete('/user', function(req,res){
  res.send('Got a DELETE request at /user');
});

app.listen(port);
console.log('Magic happens on port ' + port);

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', routes);
app.use('/users', users);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});


module.exports = app;

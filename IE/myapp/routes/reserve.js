var express = require('express');
var reserve = express.Router();
var request = require('request');

/* GET users listing. */
var start_time;
var end_time;
var plate_no;
var parking_no;

var db2 = require('./db.js');

reserve.get('/plate_no=:plate_no&parking_no=:parking_no&start_time=:start_time&end_time=:end_time', function(req, res, next) {
    parking_no = req.params.parking_no;
    plate_no = req.params.plate_no;
    start_time = req.params.start_time;
    end_time = req.params.end_time;

    saveReservationInDb();
    sendDataToMobileServer(res, parking_no, 1);
    //next();
});

function saveReservationInDb() {

    var Car = db2.mongoose.model('Cars', db2.carSchema);
    var Reserve = db2.mongoose.model('Reserves', db2.reserveSchema);

    var car = new Car({_id: "chetori"});
    var reserve = new Reserve({
        _id: 58,
        slot_id: parking_no,
        cars_id: plate_no
    });

    car.save(function (err) {
        console.log("Car character: " + car._id); // showing Cat name 'Ton'
    });
    reserve.save(function (err) {
        console.log("Car character: " + reserve._id); // showing Cat name 'Ton'
    });
}

function sendDataToMobileServer(res, parking_no, status){

    console.log("=======================");
    request('http://192.168.43.1:3000/?slot_no=' + 0 + '&reserve=' + 1, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            console.log(error);
            res.render('complete_reserve.jade', {
                name: 'pouyan',
                parking_num: parking_no,
                start_time: start_time,
                end_time: end_time
            });
        } else {
            console.log(error);
            res.render('unsuccessful.jade', {
                name: 'pouyan',
                parking_num: parking_no,
                start_time: start_time,
                end_time: end_time
            });
        }
    });
    console.log("=========================");

}

module.exports = reserve;
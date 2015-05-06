var express = require('express');
var reserve = express.Router();
var xmlHttp = null;

/* GET users listing. */
var slot_no;
var slot_status;

reserve.get('/slot_no=:slot_no&slot_status=:slot_status', function(req, res, next) {
    slot_no = req.params.slot_no;
    slot_status = req.params.slot_status;

    res.render('status.jade', {
        slot_no: slot_no,
        slot_status: slot_status
    });
    next();
});

module.exports = reserve;
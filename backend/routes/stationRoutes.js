var express = require('express');
var router = express.Router();
var stationController = require('../controllers/stationController.js');

/*
 * GET
 */
router.get('/', stationController.list);

/*
 * GET
 */
router.get('/:id', stationController.show);

/*
 * POST
 */
router.post('/', stationController.create);

/*
 * PUT
 */
router.put('/:id', stationController.update);

/*
 * DELETE
 */
router.delete('/:id', stationController.remove);

module.exports = router;

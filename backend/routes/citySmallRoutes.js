var express = require('express');
var router = express.Router();
var citySmallController = require('../controllers/citySmallController.js');

/*
 * GET
 */
router.get('/', citySmallController.list);

/*
 * GET
 */
router.get('/:id', citySmallController.show);

/*
 * POST
 */
router.post('/', citySmallController.create);

/*
 * PUT
 */
router.put('/:id', citySmallController.update);

/*
 * DELETE
 */
router.delete('/:id', citySmallController.remove);

module.exports = router;

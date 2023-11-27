var express = require('express');
var router = express.Router();
var cityController = require('../controllers/cityController.js');

/*
 * GET
 */
router.get('/', cityController.list);

router.get('/tenDay/:id', cityController.listThree);

router.get('/threeDay/:id', cityController.listTen);
/*
 * GET
 */
router.get('/:id', cityController.show);

/*
 * POST
 */
router.post('/', cityController.create);
/*
 * PUT
 */
router.put('/:id', cityController.update);
router.put('/:id/fav', cityController.updateFav);
router.put('/:id/deleteFav', cityController.removeFav);
/*
 * DELETE
 */
router.delete('/:id', cityController.remove);

module.exports = router;

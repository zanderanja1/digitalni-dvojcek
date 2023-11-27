var express = require('express');
var router = express.Router();
var userController = require('../controllers/userController.js');


router.get('/', userController.list);


router.get('/:id/profile', userController.profile);
router.get('/logout', userController.logout);
router.get('/:id', userController.show);
router.get('/addFav/:id',userController.addFavourite);
router.get('/removeFav/:id',userController.removeFavourite);

router.post('/', userController.create);
router.post('/login', userController.login);

router.put('/:id', userController.update);

router.delete('/:id', userController.remove);

module.exports = router;

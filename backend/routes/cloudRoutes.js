var express = require("express");
var router = express.Router();
var cloudController = require("../controllers/cloudController.js");

/*
 * GET
 */
router.get("/", cloudController.list);
router.get("/:id", cloudController.show);

/*
 * POST
 */
router.post("/", cloudController.create);

/*
 * DELETE
 */
router.delete("/:id", cloudController.remove);

module.exports = router;

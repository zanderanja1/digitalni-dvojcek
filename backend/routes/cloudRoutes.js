var express = require("express");
const multer = require("multer");
const upload = multer({ dest: "public/images" });
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
router.post("/", upload.single("image"), cloudController.create);

/*
 * DELETE
 */
router.delete("/:id", cloudController.remove);

module.exports = router;

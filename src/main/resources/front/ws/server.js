const { createServer } = require("https");
const { parse } = require("url");
const next = require("next");
const fs = require("fs");
const dev = process.env.NODE_ENV !== "production";
const app = next({ dev });
const handle = app.getRequestHandler();
const httpsOptions = {
    key: fs.readFileSync("./cert/wskeystore.key"),
    cert: fs.readFileSync("./cert/wskeystore.crt"),
    passphrase: 'Mojteam24'
};
app.prepare().then(() => {
    createServer(httpsOptions, (req, res) => {
        const parsedUrl = parse(req.url, true);
        handle(req, res, parsedUrl);
    }).listen(3000, (err) => {
        if (err) throw err;
        console.log("> Server started on https://localhost:3000");
    });
});
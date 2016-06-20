var casper = require('casper').create({
    verbose: true,
    logLevel: "debug"
});
casper.start('http://www.baidu.com/', function() {
    this.echo(this.getTitle());
});
casper.run();
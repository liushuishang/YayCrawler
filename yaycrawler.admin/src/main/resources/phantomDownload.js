//codes.js   
system = require('system')   
address = system.args[1];
//console.log('Loading a web page');   
var page = require('webpage').create();
page.addCookie({
    'name'     : 'Cookie',   /* required property */
    'value'    : 'PHPSESSID=4bc888777fee0119a646eae1eb084b44;IZ_bind152828725=0; SESSION_HASH=0a58c8f6b577528dccc21127fea4e6f6e782bf34; user_access=1; save_jy_login_name=13216635314; stadate1=151828725; myloc=11%7C1103; myage=25; PROFILE=152828725%3ATUA%3Am%3Aa1.jyimg.com%2F6b%2Fcd%2Ff146c7606dce5ebf3f9d43316d48%3A1%3A%3A1%3Af146c7606_1_avatar_p.jpg%3A1%3A1%3A50%3A10; mysex=m; myuid=151828725; myincome=50; RAW_HASH=tlwJz7DTfXJT0CjgVGlKTfMjoPffKo5%2Ajr6rOkfcS-VKGEs5ozQfQybmRByw0sosiCb%2AuJehAq9JXN0y-JS74cpeHvJw2KFeZMqIUEy9JVIgnsw.; COMMON_HASH=6bf146c7606dce5ebf3f9d43316d48cd; sl_jumper=%26cou%3D17%26omsg%3D0%26dia%3D0%26lst%3D2016-06-01; last_login_time=1464766039; date_pop_152828725=1; pclog=%7B%22152828725%22%3A%221464766013031%7C1%7C0%22%7D; REG_REF_URL=http://usercp.jiayuan.com/?from=login; IM_CON=%7B%22IM_TM%22%3A1464766025813%2C%22IM_SN%22%3A5%7D; IM_S=%7B%22IM_CID%22%3A9170862%2C%22IM_SV%22%3A%22211.151.166.133%22%2C%22svc%22%3A%7B%22code%22%3A0%2C%22nps%22%3A0%2C%22unread_count%22%3A%2228%22%2C%22ocu%22%3A0%2C%22ppc%22%3A0%2C%22jpc%22%3A0%2C%22regt%22%3A%221463975828%22%2C%22using%22%3A%2251%2C%22%2C%22user_type%22%3A%2210%22%2C%22uid%22%3A152828725%7D%2C%22m%22%3A0%2C%22f%22%3A0%2C%22omc%22%3A0%7D; IM_CS=2; IM_ID=5; IM_TK=1464766035094; IM_M=%5B%5D',  /* required property */
    'domain'   : 'photo.jiayuan.com'
})
var url = address;   
//console.log(url);
var tmp = [];
page.onResourceReceived = function(response) {
	if(response.contentType == null)
		console.log(JSON.stringify(response));
};
page.open(url, function (status) {   
    //Page is loaded!   
    if (status !== 'success') {   
        console.log('Unable to post!');   
    } else {
        console.log(page.content);   
    }
    phantom.exit();   
});


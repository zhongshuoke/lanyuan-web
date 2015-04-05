# lanyuan-web
相关项目：<p>
https://github.com/zhongshuoke/lanyuan-web.git<p>
https://github.com/zhongshuoke/lanyuan-utils.git<p>
https://github.com/zhongshuoke/lanyuan-service.git<p>
https://github.com/zhongshuoke/lanyuan-security.git<p>
https://github.com/zhongshuoke/lanyuan-pulgins.git<p>
https://github.com/zhongshuoke/lanyuan-mapper.git<p>
https://github.com/zhongshuoke/lanyuan-entity.git<p>
https://github.com/zhongshuoke/lanyuan.git<p>

与客户端的接口：<p>
1、文章查询接口（更多文章、最新文章等）
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/getArticleList.html?data_type=&class_id=&max_article_id=&keyword=&page=&limit=
参数描述：
data_type：数据类型：more表示“更多文章”，其他值表示“最新文章”
class_id：文章栏目类别id，为空时表示所有的文章，多个栏目时用英文逗号分隔。
max_article_id：查询时返回手机端的一个文章id（当前数据库最大的id）,手机下次做查询要加入查询条件。后端每次返回数据都会返回，客户端下次查询时要更新该查询条件。
keyword：搜索关键字
page：当前页数。第一次访问（即获取最新文章）时页数为1，之后每次“获得更多文章”页数加一
limit：每页获取的数据数，不传时默认20

应答：
返回JSON格式数据
如下：
{"message":"","max_article_id":11073,"errorCode":1,
"commArticleList":[{"id":11073,"wxAccountNo":"baicaijialvxing","title":"旅行结婚:Las Vegas 拉斯维加斯 的 教堂婚礼","content":null,"publishTime":"2015-03-28 00:00:00","captureTime":"2015-03-28 10:30:29","sourceUrl":"http://mp.weixin.qq.com/s?__biz=MzA4MTEzMzYwMg==&mid=203921395&idx=1&sn=edade673b6a822e6c653935e02be4498&3rd=MzA3MDU4NTYzMw==&scene=6#rd","readNum":0,"goodNum":0,"picUrl":null},
		{"id":11058,"wxAccountNo":"dggujin",....................
		]
}

2、通用栏文章列表（客户端顶端的几个滑动的图片文章），每个栏目的文章不一样。
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/getCommArticleList.html?class_id=&limit=
参数描述：
class_id：文章栏目类别id，为空时表示所有的文章，多个栏目时用英文逗号分隔。
limit：每页获取的数据数，不传时默认4
应答：
返回JSON格式数据
如下：
{"message":"","typePicList":[{"id":1,"wxAccTypeId":1,"wxArticleId":1,"typePicUrl":"y:/localpic/typepic/1.jpg"},
			{"id":2,"wxAccTypeId":2,"wxArticleId":2,"typePicUrl":"y:/localpic/typepic/1.jpg"},
			{"id":3,"wxAccTypeId":3,"wxArticleId":3,"typePicUrl":"y:/localpic/typepic/1.jpg"},
			{"id":4,"wxAccTypeId":4,"wxArticleId":4,"typePicUrl":"y:/localpic/typepic/1.jpg"}],
"errorCode":1}


3、返回单篇文章内容Content
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/queryArticleContent.html?wxArticleId=
参数描述：
wxArticleId：文章id

应答：
返回JSON格式数据
如下：
{"errorCode":1,"message":"","content":"<!DOCTYPE html>\n<html> <head>.........
}

4、返回文章wxArticleId中的广告
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/queryAdvertisement.html?wxArticleId=
参数描述：
wxArticleId：文章id

应答：
返回JSON格式数据
如下：
{"message":"","errorCode":1,"advertisement":{"id":1,"title":"KFC广告","url":"http://zhidao.baidu.com/daily/view?id=4071","createTime":1427613058732}}


5、返回文章wxArticleId的相关文章列表，最多3篇。
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/queryRelatedArticle.html?wxArticleId=
参数描述：
wxArticleId：文章id

应答：
返回JSON格式数据
如下：
{"message":"","errorCode":1,"relatedArticleList":[
	{"id":7318,"wxAccountNo":"dggujin","title":"爱,不一定要发生关系","content":null,"publishTime":"2015-01-19 00:00:00","captureTime":"2015-02-14 15:07:04","sourceUrl":"http://mp.weixin.qq.com/s?__biz=MzAwNDIyNTE2MA==&mid=202302907&idx=2&sn=c1c49ab25952246d7411a7df088c0182&3rd=MzA3MDU4NTYzMw==&scene=6#rd","readNum":0,"goodNum":0,"picUrl":null},
	{"id":7319,"wxAccoun..........
}

6、返回今日推荐文章
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/queryRecommendArticleList.html?city_id=&class_id=&max_article_id=&page=&limit=
参数描述：
city_id：城市id
class_id：文章栏目类别id，为空时表示所有的文章，多个栏目时用英文逗号分隔。
max_article_id：查询时返回手机端的一个文章id（当前数据库最大的id）,手机下次做查询要加入查询条件。后端每次返回数据都会返回，客户端下次查询时要更新该查询条件。
page：当前页数。第一次访问（即获取最新文章）时页数为1，之后每次“获得更多文章”页数加一
limit：每页获取的数据数，不传时默认20

应答：
返回JSON格式数据
如下：
{"message":"","max_article_id":11073,"errorCode":1,
"commArticleList":[{"id":11073,"wxAccountNo":"baicaijialvxing","title":"旅行结婚:Las Vegas 拉斯维加斯 的 教堂婚礼","content":null,"publishTime":"2015-03-28 00:00:00","captureTime":"2015-03-28 10:30:29","sourceUrl":"http://mp.weixin.qq.com/s?__biz=MzA4MTEzMzYwMg==&mid=203921395&idx=1&sn=edade673b6a822e6c653935e02be4498&3rd=MzA3MDU4NTYzMw==&scene=6#rd","readNum":0,"goodNum":0,"picUrl":null},
		{"id":11058,"wxAccountNo":"dggujin",....................
		]
}

7、返回所有栏目
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/getClassList.html
参数描述：
无

应答：
返回JSON格式数据
如下：
{"message":"","errorCode":1,"wxAccTypeList":[{"id":1,"wxType":"新闻社评","wxTypeSN":"xwsp","createTime":"2015-01-29 17:58:45"},
			{"id":2,"wxType":"休闲旅游","wxTypeSN":"xxly","createTime":"2015-02-02 15:31:15"},
			{"id":3,"wxType":"八卦娱乐","wxTypeSN":"bgyl","createTime":"2015-01-29 18:06:18"}]}

8、返回"更多文章推荐"，最多3篇。
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/queryMoreRecommendArticleList.html?wxArticleId=
参数描述：
wxArticleId：文章id

应答：
返回JSON格式数据
如下：
{"message":"","errorCode":1,"moreRecommendArticleList":[{"id":9291,"wxAccountNo":"jueduibagua","title":"小四冬冬取关又复关,闹的是哪一出?","content":null,"publishTime":"2014-11-27 00:00:00","captureTime":"2015-02-14 21:11:40","sourceUrl":"http://mp.weixin.qq.com/s?__biz=MzAxODE0MzYzMw==&mid=201461286&idx=1&sn=f9b76fb3b3dee22be98dbad19434b39a&3rd=MzA3MDU4NTYzMw==&scene=6#rd","readNum":0,"goodNum":0,"picUrl":null},
							{"id":9292,"wxAccountNo":"jueduibagua","title":"接盘侠赵又廷如何让女神死心塌地——“从小就知道女生是...","content":null,"publishTime":"2014-11-28 00:00:00","captureTime":"2015-02-14 21:11:50","sourceUrl":"http://mp.weixin.qq.com/s?__biz=MzAxODE0MzYzMw==&mid=201502038&idx=2&sn=fe20906070988372cda71fc7cec7864c&3rd=MzA3MDU4NTYzMw==&scene=6#rd","readNum":0,"goodNum":0,"picUrl":null},
							{"id":9293,"wxAccountNo":"jueduibagua","title":"高圆圆为什么嫁赵又廷?女神追求的也不过是安全感啊!","content":null,"publishTime":"2014-11-28 00:00:00","captureTime":"2015-02-14 21:12:04","sourceUrl":"http://mp.weixin.qq.com/s?__biz=MzAxODE0MzYzMw==&mid=201502038&idx=1&sn=19f6039198556cf6a05c8c9f0656fc05&3rd=MzA3MDU4NTYzMw==&scene=6#rd","readNum":0,"goodNum":0,"picUrl":null}]
				}

9、文章阅读数加一
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/increaseReadNum.html?wxArticleId=
参数描述：
wxArticleId：文章id

应答：
返回JSON格式数据
如下：
{"message":"阅读数加一成功","errorCode":1}
或
{"message":"文章不能为空","errorCode":0}



--------------------------------------------------------------------------------------------------------------------------
下面的接口和登陆用户有关
--------------------------------------------------------------------------------------------------------------------------

10、用户注册
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/registerUser.html?phone=&username=&password=&verifyCode=
参数描述：
phone：手机号
username：用户名
password：密码
verifyCode：手机验证码（当前可以不传，还没有短信服务）

应答：
返回JSON格式数据(token是用户的登陆标识)
如下：
{"message":"注册成功","errorCode":1,"token":"6125E943F08D7272A07811CDA02E5221"}
或
{"message":"手机号不能为空","errorCode":0}
或
{"message":"用户名不能为空","errorCode":0}
或
{"message":"密码不能为空","errorCode":0}

11、用户登陆
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/login.html?phone=&username=&password=
参数描述：
phone：手机号
username：用户名 （phone和username必有其一）
password：密码 （必填）

应答：
返回JSON格式数据(token是用户的登陆标识)
如下：
{"message":"登陆成功","errorCode":1,"token":"6125E943F08D7272A07811CDA02E5221"}
或
{"message":"手机号和用户名不能都为空","errorCode":0}
或
{"message":"密码不能为空","errorCode":0}
或
{"message":"密码不能为空","errorCode":0}

12、文章投稿
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/articleApply.html?content=&token=
参数描述：
content：投稿内容
token：用户登陆标识（不为空时会用于登陆判断【判断是否正确和失效】。为空时不做登陆判断。）

应答：
返回JSON格式数据
如下：
{"message":"投稿成功","errorCode":1}
或
{"message":"token已失效，请重新登录","errorCode":0}
或
{"message":"投稿内容不能为空","errorCode":0}

13、栏目订阅
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/subscribeClass.html?class_id=&token=
参数描述：
class_id：栏目id
token：用户登陆标识（不为空时会用于登陆判断【判断是否正确和失效】。为空时不做登陆判断。）

应答：
返回JSON格式数据
如下：
{"message":"订阅成功","errorCode":1}
或
{"message":"请选择栏目","errorCode":0}
或
{"message":"token已失效，请重新登录","errorCode":0}

14、关键字订阅
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/subscribeKeyword.html?keyword=&token=
参数描述：
keyword：关键字
token：用户登陆标识（不为空时会用于登陆判断【判断是否正确和失效】。为空时不做登陆判断。）

应答：
返回JSON格式数据
如下：
{"message":"订阅成功","errorCode":1}
或
{"message":"关键字不能为空","errorCode":0}
或
{"message":"token已失效，请重新登录","errorCode":0}

15、给文章点赞
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/likeButton.html?wxArticleId=&token=
参数描述：
wxArticleId：文章id
token：用户登陆标识（不为空时会用于登陆判断【判断是否正确和失效】。为空时不做登陆判断。）

应答：
返回JSON格式数据
如下：
{"message":"点赞成功","errorCode":1}
或
{"message":"文章不能为空","errorCode":0}
或
{"message":"token已失效，请重新登录","errorCode":0}


16、获取城市列表
请求：
http://121.40.69.199:8080/lanyuan-web/querydata/getCityList.html?page=&limit=
参数描述：
page：当前页数
limit：每页数据数目

应答：
返回JSON格式数据
如下：
{"message":"","errorCode":1,"wxCityList":[{"cityId":1,"cityName":"北京"},{"cityId":2,"cityName":"上海"}]}

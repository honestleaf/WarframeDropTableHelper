# WarframeDropTableHelper
一个将WARFRAME掉率数据转化为json和excel表格的简单Java程序.程序生成的文件主要为WARFRAME灰机wiki上的一些页面提供数据支持。

## 相关说明
* 该程序使用了“Genson”、“Apache POI”和“HtmlUnit“这三个库。
* 由于国内网络环境的影响，经常会出现网页内容读取失败的问题，一般重试几次就能解决。
* “AdditionalDrop.xlsx”中的内容为手动收集的掉落数据，该数据最终会被整合进“MissionDrop”文件。

## 数据来源
* https://n8k6e2y6.ssl.hwcdn.net/repos/hnfvc0o3jnfvc873njb03enrf56.html
* http://warframe.wikia.com

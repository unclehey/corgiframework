                                     【corgi】项目 Git init Version 1.0

1、项目简介：  【corgi】个人项目。

2、功能特性：   整个项目采用ssm + Maven + Git + AceAdmin + jqGrid + mysql + mongodb + redis框架。

3、目录结构：   corgi-api 提供APP接口：【APP加密算法：随机产生AES秘钥；AES加密请求参数生成密文；RSA加密秘钥和密文。】；
                corgi-admin 管理后台：采用AceAdmin框架 + jqGrid表格结构，并进行部分自我封装；
                corgi-third 第三方交互模块【如支付宝、微信支付异步回调处理、微信公众号交互等】;
                corgi-service 服务层；
                corgi-dao DAO层、项目配置文件及mybatis自动生成配置；
                corgi-tool 工具类；
                corgi.sql初始化sql脚本【database字符集utf8mb4，排序规则utf8mb4_unicode_ci，支持Emoji表情】。

4、项目依赖：   corgi-dao、corgi-tool以jar包形式依赖spring等其他第三方包；
                corgi-service以jar包形式依赖corgi-dao和corgi-tool；
                corgi-api、corgi-admin及corgi-third以war包形式依赖corgi-service。

5、部署步骤：采用Maven根据部署环境【profile】自动依赖打包。

6、版本：V 1.1.1。

7、声明：本项目由【嘿の大叔】自主研发，未经同意，请勿以任何方式进行转载、抄袭、剽窃等，否则将受到法律制裁。
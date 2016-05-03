
/*==============================================================*/
/* Table: lejj_module_config  
 * app 模块化配置                                  */
/*==============================================================*/
drop table if exists lejj_module_config;
create table lejj_module_config
(
   module_id            int not null auto_increment,
   module_group         varchar(10),
   module_type          tinyint,
   module_name          varchar(254),
   module_desc          varchar(254),
   module_picture       varchar(254),
   module_link          varchar(254),
   position             tinyint,
   size                 varchar(254),
   enabled              tinyint,
   primary key (module_id)
);

alter table lejj_module_config comment 'app应用home模块配置';

/*==============================================================*/
/* Table: esc_category      									*/
/*  add 是否热门，图片地址，显示首页，推荐                                   							*/
/*==============================================================*/

ALTER TABLE ecs_category ADD hot tinyint(1) default '0' COMMENT '热门';
ALTER TABLE ecs_category ADD image_url  varchar(500) COMMENT '图片';
ALTER TABLE ecs_category ADD home_show  tinyint(1) default '0'  COMMENT 'app首页是否显示';
ALTER TABLE ecs_category ADD recommend tinyint(1) default '0'  COMMENT '是否是推荐分类';
ALTER TABLE ecs_category ADD function_type tinyint(1) default '0'  COMMENT '用于点击category时,功能判断';
ALTER TABLE ecs_category ADD add_time int(11) default '0'  COMMENT '添加时间';

/*==============================================================*/
/* Table: store                                                 */
/* 实体店服务                                                                                                                                                                   */
/*==============================================================*/
drop table if exists lejj_store;
create table lejj_store
(
   store_id             bigint not null auto_increment,
   region_id            smallint(5) not null default '0',
   store_name           varchar(254),
   phone                varchar(15),
   store_detail         varchar(500),
   address_detail       varchar(500),
   remark               varchar(500)
   primary key (store_id)
);

alter table lejj_store comment '实体店';


/*==============================================================*/
/* Table: store_goods                                           */
/*==============================================================*/
drop table if exists lejj_store_goods;
create table lejj_store_goods
(
   store_goods_id       bigint not null auto_increment,
   store_id             bigint not null default '0',
   goods_id             bigint not null default '0',
   freight              decimal(10,2)  default '0.00',
   remark               varchar(500),
   address              varchar(500)
   primary key (store_goods_id)
);

alter table lejj_store_goods comment '到店体验';


/*==============================================================*/
/* Table: ecs_comment    										*/
/*  add   服务评价                    							*/
/*==============================================================*/

ALTER TABLE ecs_comment ADD service_rank tinyint(1) unsigned NOT NULL default '0';

/*==============================================================*/
/* Table: commentPicture                                        */
/*==============================================================*/
drop table if exists lejj_comment_picture;
create table lejj_comment_picture
(
   commentPicture_id    bigint not null auto_increment,
   comment_id           int(10) not null default '0',
   picture_url          varchar(254),
   primary key (commentPicture_id)
);

alter table lejj_comment_picture comment '评论晒图';




/*==============================================================*/
/* Table: ecs_ad  										*/
/*  add   is_app  app_code   object_id   sort_order    */
/*==============================================================*/

ALTER TABLE ecs_ad ADD is_app tinyint(1) unsigned NOT NULL default '0';
ALTER TABLE ecs_ad ADD app_type tinyint(1) unsigned NOT NULL default '0';
ALTER TABLE ecs_ad ADD object_id bigint(8) unsigned NOT NULL default '0';
ALTER TABLE ecs_ad ADD sort_order tinyint(3) unsigned NOT NULL DEFAULT '0';

/*==============================================================*/
/* Table: ecs_goods 										    */
/*  add   goods_storage_type           */
/*==============================================================*/

ALTER TABLE ecs_goods ADD goods_storage_type tinyint(1) unsigned NOT NULL default '0' COMMENT '销售类型：现货，预售';
ALTER TABLE ecs_goods ADD discount decimal(5,2) NOT NULL default '10.00' COMMENT '折扣';
ALTER TABLE ecs_goods ADD specification text COMMENT '图文详情-规格属性';
ALTER TABLE ecs_goods ADD packaging_afterSale text COMMENT '包装售后';
ALTER TABLE ecs_goods ADD desc_url varchar(100) NULL DEFAULT '' COMMENT '描述连接';
ALTER TABLE ecs_goods CHANGE COLUMN specification specification varchar(100) NOT NULL DEFAULT '' COMMENT '属性连接';
ALTER TABLE ecs_goods  CHANGE COLUMN packaging_afterSale packaging_afterSale varchar(255) NOT NULL DEFAULT '' COMMENT '售后连接';
ALTER TABLE ecs_goods ADD is_nofreight tinyint(1) NOT NULL default '0' COMMENT '包邮';
ALTER TABLE ecs_goods ADD is_show  tinyint(1) NOT NULL default '0' COMMENT '是否显示';
ALTER TABLE ecs_goods ADD old_price decimal(10,2)   NOT NULL DEFAULT 0.00 COMMENT '采购价格' after 'is_show'; 
ALTER TABLE ecs_goods ADD old_sn varchar(60)  COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '原始编号';
ALTER TABLE ecs_goods ADD promote_show tinyint(1)   NOT NULL DEFAULT 0 COMMENT '是否显示促销日期，0否，1是';
ALTER TABLE ecs_goods ADD transport_service varchar(20)  COLLATE utf8_general_ci DEFAULT '' COMMENT '服务运输信息';

/*==============================================================*/
/* Table: ecs_attribute 										*/
/*  add   is_sell                                               */
/*==============================================================*/

ALTER TABLE ecs_attribute ADD is_sell tinyint(1) NOT NULL default '0' COMMENT '是否是销售属性';


/*==============================================================*/
/* Table: ecs_goods_attr 										*/
/*  add   sort_order 排序                                              */
/*==============================================================*/

ALTER TABLE ecs_goods_attr ADD sort_order tinyint(3) NOT NULL default '0' COMMENT '排序';



/*==============================================================*/
/* Table: ecs_products 										    */
/*  add   is_default       goods_attr_name                      */
/*==============================================================*/

ALTER TABLE ecs_products ADD goods_attr_value text COMMENT '属性值';
ALTER TABLE ecs_products ADD group_id int(10) COMMENT '商品同组id';
ALTER TABLE ecs_products ADD sort_order smallint(4) unsigned NOT NULL default '0' COMMENT '用户同组goods 排序';

/*==============================================================*/
/* Table: ecs_region    										*/
/*  add   热门 排序                    							                */
/*==============================================================*/

ALTER TABLE ecs_region ADD is_hot tinyint(1) unsigned NOT NULL default '0' COMMENT '热门';
ALTER TABLE ecs_region ADD sort_order smallint(4) unsigned NOT NULL default '50' COMMENT '排序';
ALTER TABLE ecs_region ADD head_char varchar(1) COMMENT '首字母';


/*==============================================================*/
/* Table: ecs_users  											*/
/* add: 头像地址url,角色id                                          */
/*==============================================================*/

ALTER TABLE ecs_users ADD avatar varchar(500) COMMENT '头像图片url';
ALTER TABLE ecs_users ADD role_id smallint(5) NOT NULL default '0' COMMENT '角色id';
alter table ecs_users drop index user_name;/*删除user_name 的唯一约束*/
--ALTER TABLE ecs_users ADD real_name varchar(60) COMMENT '真实姓名';
ALTER TABLE ecs_users ADD company_id   mediumint(8) default '1' COMMENT '公司Id,设计师才有';
ALTER TABLE ecs_users ADD region_id    smallint(5) default '322' COMMENT '城市Id';
ALTER TABLE ecs_users ADD device_id    varchar(40) COMMENT '用户设备唯一id';

/*==============================================================*/
/* Table: ecs_role											    */
/* add: 角色类型			                                        */
/*==============================================================*/
ALTER TABLE ecs_role ADD role_type smallint(1) unsigned NOT NULL default '0';


/*==============================================================*/
/* Table: lejj_Authentication                                   */
/* 设计师的认证信息													*/
/*==============================================================*/
drop table if exists lejj_Authentication;
create table lejj_Authentication
(
   authentication_id    bigint not null auto_increment,
   designer_id          bigint,
   real_name            varchar(254),
   id_number            varchar(254),
   sex               	tinyint(1),
   company_id           mediumint(8),
   pass                 tinyint(1),
   pass_time            int(10),
   primary key (authentication_id)
);

alter table lejj_Authentication comment '设计师认证信息';

/*==============================================================*/
/* Table: lejj_company                                          */
/* 合作公司													    */
/*==============================================================*/
drop table if exists lejj_company;
create table lejj_company
(
   company_id           mediumint(8) not null auto_increment,
   region_id            bigint not null default '0',
   company_code         varchar(254),
   company_name         varchar(254),
   company_desc         varchar(500),
   company_address      varchar(500),
   company_link         varchar(500),
   company_logo         varchar(500),
   add_time             int(10),
   primary key (company_id)
);

alter table lejj_company comment '合作公司';





/*==============================================================*/
/* Table: lejj_verification_code                                 */
/* 验证码										    */
/*==============================================================*/
drop table if exists lejj_verification_code  ;
create table lejj_verification_code  
(
   code_id     int(10) not null auto_increment,
   code_type   tinyint(1) not null,
   object_id   varchar(60) not null,
   code_value  varchar(8) not null,
   valid_time  int(11),
   is_valid    tinyint(1) default '0' COMMENT '是否还有效，验证后为0',
   primary key (code_id)
);

alter table lejj_verification_code comment '验证码';

/*==============================================================*/
/* Table: lejj_my_client                                        */
/* 设计师与客户地址关系表								                */
/*==============================================================*/
drop table if exists lejj_my_client  ;
create table lejj_my_client  
(
   my_client_id  int(10) not null auto_increment,
   consignee_id  int(10) not null,
   designer_id   int(10) not null,
   add_time      int(11),
   primary key (my_client_id)
);

alter table lejj_my_client comment '设计师与客户地址关系表';


/*==============================================================*/
/* Table: ecs_user_address    										*/
/*  add   自定义 ----第4级区域:镇,市区内...                					 */
/*==============================================================*/

ALTER TABLE ecs_user_address ADD extend smallint(5) default '0' COMMENT '第4级区域:镇,市区内...';
ALTER TABLE ecs_user_address ADD region_id smallint(5) default '0' COMMENT '区域id:记录最后一级region id';

/*==============================================================*/
/* Table: ecs_cart    									 	    */
/*  add   选中     购物车里面的gongs  是否选中 结算，默认结算          		        */
/*==============================================================*/

ALTER TABLE ecs_cart ADD selected tinyint(1) default '1' COMMENT '是否选中';

/*==============================================================*/
/* Table: ecs_goods_gallery    									 	    */
/*  add   排序 		        */
/*==============================================================*/
ALTER TABLE ecs_goods_gallery ADD sort_order tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '图片排序';


/*==============================================================*/
/* Table: ecs_order_info    									*/
/*  add   额外的区域		        */
/*==============================================================*/
ALTER TABLE ecs_order_info ADD column extend smallint(5) DEFAULT '0' COMMENT '额外的区域';
ALTER TABLE ecs_order_info ADD COLUMN order_type tinyint(3) unsigned NOT NULL DEFAULT 0 COMMENT '0普通订单，1内部订单';
ALTER TABLE ecs_order_info ADD COLUMN is_del tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '0可用，1已删除';

/*==============================================================*/
/* Table: ecs_delivery_order    									*/
/*  add   额外的区域 		        */
/*==============================================================*/
ALTER TABLE ecs_delivery_order ADD column extend smallint(5) DEFAULT '0' COMMENT '额外的区域';


/*==============================================================*/
/* Table: ecs_feedback   用户反馈信息表 									*/
/*  add   来源平台 update  msg_type  COMMENT  */ 
/*==============================================================*/
ALTER TABLE ecs_feedback CHANGE COLUMN msg_type msg_type tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '1:功能建议,2产品服务,3故障报错,0其他';
ALTER TABLE ecs_feedback  ADD COLUMN plat tinyint(3) NOT NULL DEFAULT 0 COMMENT '来源平台，0:pc，1:安卓，2:ios';

/*==============================================================*/
/* Table: lejj_bank                                        */
/* 银行列表							                */
/*==============================================================*/
drop table if exists lejj_bank  ;
create table lejj_bank  
(
   bank_id  int(10) not null auto_increment COMMENT '银行id',
   bank_name    varchar(50) not null COMMENT '银行名',
   bank_logo    varchar(500) COMMENT '银行logo URL',
   bank_remark  varchar(500) COMMENT '银行备注',
   sort_order tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '排序',
   primary key (bank_id)
);

alter table lejj_bank comment '银行列表';

/*==============================================================*/
/* Table: lejj_my_bank                                        */
/* 设计师绑定的银行卡							                */
/*==============================================================*/
drop table if exists lejj_my_bank  ;
create table lejj_my_bank  
(
   my_bank_id              int(10) not null auto_increment COMMENT '绑定银行卡id',
   user_id                 mediumint(8) not null  COMMENT '用户id',
   bank_id                 int(10) not null  COMMENT '银行id',
   user_real_name          varchar(60) not null  COMMENT '用户真实姓名',
   card_number  		   varchar(20) not null COMMENT '银行卡号',
   add_time  			   int(10) COMMENT '绑定时间',
   primary key (my_bank_id)
);

alter table lejj_my_bank comment '设计师绑定的银行卡';


/*==============================================================*/
/* Table: ecs_pay_log 系统支付记录							*/
/*  add  COLUMN： add_time 时间      */ 
/*==============================================================*/
ALTER TABLE ecs_pay_log  ADD COLUMN add_time int(10) DEFAULT 0 COMMENT '该条记录发生的时间';
ALTER TABLE ecs_pay_log  ADD COLUMN transaction_id  varchar(50) COMMENT '交易号';
ALTER TABLE ecs_pay_log  ADD COLUMN pay_type tinyint(3) DEFAULT 0 COMMENT '支付类型，如：1=易宝';
ALTER TABLE ecs_pay_log  ADD COLUMN remark varchar(100) COMMENT '备注';


/*==============================================================*/
/* Table: java_goods_attr   规则引擎创建的临时表							*/
/*  用于记录商品的一些交易属性 比如 已售个数     */ 
/*==============================================================*/
DROP TABLE IF EXISTS java_goods_attr;
CREATE TABLE java_goods_attr (
  goods_id mediumint(8) unsigned NOT NULL DEFAULT '0',
  goods_sn varchar(60) NOT NULL DEFAULT '',
  total_sold_count int(10) DEFAULT NULL,
  PRIMARY KEY (goods_id),
  KEY goods_sn (goods_sn)
);


/*==============================================================*/
/* Table: lejj_apply_entry_log   客户申请入驻记录						*/
/*  用于客户申请 入驻平台的记录  记录申请时间 申请状态 等  							*/ 
/*==============================================================*/
DROP TABLE IF EXISTS lejj_apply_entry_log;
CREATE TABLE lejj_apply_entry_log (
  log_id mediumint(8) unsigned NOT NULL auto_increment COMMENT 'log id', 
  user_id mediumint(8) NOT NULL COMMENT '用户id(匿名)',
  apply_status tinyint(1) NOT NULL DEFAULT 0 COMMENT '申请入驻状态 0=待审核 1=通过 2=驳回3=作废',
  apply_log  varchar(100) DEFAULT ''  COMMENT '申请记录 如未通过原因..',
  apply_time int(11) DEFAULT 0  COMMENT '申请时间',
  remark varchar(200) DEFAULT ''  COMMENT '备注',
  PRIMARY KEY (log_id)
);
alter table lejj_apply_entry_log comment '客户申请入驻记录';
alter table ecs_pay_log add unique key index_ecs_pay_log_p_t(pay_type,transaction_id) USING BTREE;




/* Table: lejj_invite_code   邀约码						*/
/*  设计师邀约码  */ 
/*==============================================================*/
DROP TABLE IF EXISTS lejj_invite_code;
CREATE TABLE lejj_invite_code (
  invite_id mediumint(8) unsigned NOT NULL auto_increment COMMENT '邀约码 id',
  code varchar(6) NOT NULL COMMENT '邀约码',
  receive_phone varchar(20) NOT NULL COMMENT '接收邀约码手机',
  send_user_id mediumint(10) NOT NULL COMMENT '发送者id',
  use_user_id mediumint(10) NOT NULL COMMENT '使用者id(匿名)',
  status  tinyint(1) NOT NULL DEFAULT 0 COMMENT '邀约码状态 0=未使用,1=使用',
  send_time int(11) DEFAULT 0  COMMENT '发送时间',
  use_time int(11) DEFAULT 0  COMMENT '使用时间',
  PRIMARY KEY (invite_id),
  key code (code)
);

alter table lejj_invite_code comment '邀约码';














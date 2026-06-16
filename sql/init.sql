create table if not exists merchant (
  id bigint primary key auto_increment,
  merchant_name varchar(128) not null,
  status varchar(32) default 'NORMAL',
  created_at datetime,
  updated_at datetime,
  is_deleted tinyint default 0
);

create table if not exists sys_user (
  id bigint primary key auto_increment,
  merchant_id bigint,
  username varchar(64) not null,
  password varchar(255) not null,
  nickname varchar(64),
  status varchar(32) default 'NORMAL',
  created_at datetime,
  updated_at datetime,
  is_deleted tinyint default 0,
  unique key uk_username(username)
);

create table if not exists sys_role (
  id bigint primary key auto_increment,
  role_code varchar(64) not null,
  role_name varchar(64) not null,
  created_at datetime,
  updated_at datetime,
  unique key uk_role_code(role_code)
);

create table if not exists sys_permission (
  id bigint primary key auto_increment,
  permission_code varchar(128) not null,
  permission_name varchar(128) not null,
  created_at datetime,
  unique key uk_permission_code(permission_code)
);

create table if not exists product (
  id bigint primary key auto_increment,
  merchant_id bigint not null,
  product_name varchar(255) not null,
  category varchar(128),
  price decimal(10,2),
  stock int,
  main_image varchar(500),
  selling_points text,
  target_users text,
  pain_points text,
  keywords text,
  margin_rate decimal(6,4),
  status varchar(32) default 'ON_SALE',
  created_by bigint,
  created_at datetime,
  updated_at datetime,
  is_deleted tinyint default 0,
  index idx_merchant(merchant_id)
);

create table if not exists ad_campaign (
  id bigint primary key auto_increment,
  merchant_id bigint not null,
  campaign_name varchar(255) not null,
  product_id bigint not null,
  goal varchar(64),
  daily_budget decimal(12,2),
  total_budget decimal(12,2),
  remaining_budget decimal(12,2),
  start_time datetime,
  end_time datetime,
  status varchar(64),
  created_by bigint,
  created_at datetime,
  updated_at datetime,
  is_deleted tinyint default 0,
  index idx_merchant_status(merchant_id, status),
  index idx_product(product_id)
);

create table if not exists budget_transaction (
  id bigint primary key auto_increment,
  request_id varchar(128) not null,
  merchant_id bigint not null,
  campaign_id bigint not null,
  creative_id bigint,
  transaction_type varchar(64),
  amount decimal(12,4),
  before_amount decimal(12,4),
  after_amount decimal(12,4),
  created_at datetime,
  unique key uk_request_id(request_id),
  index idx_campaign(campaign_id)
);

create table if not exists ad_event_log (
  id bigint primary key auto_increment,
  request_id varchar(128) not null,
  merchant_id bigint not null,
  campaign_id bigint,
  product_id bigint,
  creative_id bigint,
  keyword varchar(255),
  event_type varchar(64),
  cost decimal(12,4),
  gmv decimal(12,4),
  user_tags text,
  created_at datetime,
  index idx_campaign_event(campaign_id, event_type),
  index idx_created_at(created_at)
);


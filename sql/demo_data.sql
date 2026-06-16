insert into merchant(id, merchant_name, status, created_at, updated_at, is_deleted)
values (1, 'Demo 商家', 'NORMAL', now(), now(), 0)
on duplicate key update merchant_name = values(merchant_name);

insert into sys_user(id, merchant_id, username, password, nickname, status, created_at, updated_at, is_deleted)
values (1, 1, 'admin', '123456', '平台管理员', 'NORMAL', now(), now(), 0)
on duplicate key update nickname = values(nickname);

insert into product(id, merchant_id, product_name, category, price, stock, selling_points, target_users, pain_points, keywords, margin_rate, status, created_by, created_at, updated_at, is_deleted)
values (101, 1, '夏季轻薄防晒衣', '服饰', 129.00, 5000, 'UPF50+,冰丝凉感,轻薄透气', '户外通勤女性,学生,骑行人群', '怕晒黑,夏天闷热,普通外套太厚', '防晒衣,冰丝防晒衣,夏季外套', 0.3500, 'ON_SALE', 1, now(), now(), 0)
on duplicate key update product_name = values(product_name);


create table if not exists announcement
(
    id           bigint auto_increment primary key,
    content      varchar(255) not null,
    title        varchar(255) not null,
    created_date timestamp(6) not null,
    updated_date timestamp(6)
);

create table if not exists faq
(
    id           bigint auto_increment primary key,
    content      varchar(255) not null,
    category     varchar(255) not null,
    answer       varchar(255),
    created_date datetime(6) not null,
    updated_date datetime(6)
);

create table if not exists member
(
    id                bigint auto_increment primary key,
    verified_phone    boolean,
    birthday          varchar(255),
    birthyear         varchar(255),
    device_token      varchar(255),
    email             varchar(255),
    nickname          varchar(255),
    oauth_id          varchar(255),
    oauth_provider    varchar(255),
    phone_number      varchar(255),
    profile_image_url varchar(255),
    toss_customer_key varchar(255),
    status            varchar(255) not null,
    created_date      datetime(6) not null,
    updated_date      datetime(6),
    constraint UK_member_toss_customer_key
        unique (toss_customer_key),
    constraint UK_member_oauth_id
        unique (oauth_id, oauth_provider)
);

create table if not exists delivery_address
(
    id             bigint auto_increment primary key,
    is_default     boolean,
    member_id      bigint       not null,
    name           varchar(255) not null,
    detail_address varchar(255) not null,
    jibun_address  varchar(255) not null,
    road_address   varchar(255) not null,
    zonecode       varchar(255) not null,
    phone_number   varchar(255) not null,
    recipient_name varchar(255) not null,
    created_date   datetime(6) not null,
    updated_date   datetime(6),
    constraint FK_address_member
        foreign key (member_id) references member (id)
);

create table if not exists friend
(
    id           bigint auto_increment primary key,
    is_blocked   boolean      not null,
    member_id    bigint       not null,
    target_id    bigint       not null,
    nickname     varchar(255) not null,
    created_date datetime(6) not null,
    updated_date datetime(6),
    constraint unique_member_id_and_target_id
        unique (member_id, target_id),
    constraint FK_friends_target_to_member
        foreign key (target_id) references member (id),
    constraint FK_friends_member_to_member
        foreign key (member_id) references member (id)
);

create table if not exists toss_payment
(
    id                  bigint auto_increment primary key,
    member_id           bigint         not null,
    order_id            varchar(255)   not null,
    total_amount        decimal(38, 2) not null,
    order_name          varchar(255)   not null,
    status              varchar(255)   not null,
    payment_key         varchar(255)   not null,
    cancel_reason       varchar(255),
    idempotency_key     varchar(255),
    id_key_updated_date datetime(6),
    created_date        datetime(6) not null,
    updated_date        datetime(6),
    constraint UK_toss_payment_key
        unique (payment_key),
    constraint UK_toss_payment_order_id
        unique (order_id),
    constraint UK_toss_payment_cancel_idempotency_key
        unique (idempotency_key)
);


create table if not exists product
(
    id               bigint auto_increment primary key,
    category         varchar(255),
    brand            varchar(255),
    product_id       varchar(255) not null,
    product_provider varchar(255) not null,
    product_name     varchar(255) not null,
    price            decimal(38, 2) not null,
    image_url        varchar(255),
    description      text,
    status           varchar(255) not null,
    sale_end_date    date,
    limit_date       int,
    discount_rate    int,
    created_date     datetime(6) not null,
    updated_date     datetime(6),
    constraint UK_product_id
        unique (product_id, product_provider)
);

create table if not exists product_option
(
    id           bigint auto_increment primary key,
    product_id   bigint       not null,
    code         varchar(255) not null,
    option_name  varchar(255) not null,
    status       varchar(255) not null,
    created_date datetime(6) not null,
    updated_date datetime(6),
    constraint UK_product_option_product_code
        unique (product_id, code),
    constraint FK_product_option_product
        foreign key (product_id) references product (id)
);

create table if not exists funding
(
    id                         bigint auto_increment primary key,
    visible                    varchar(255)   not null,
    title                      varchar(25)    not null,
    description                text,
    end_date                   date           not null,
    image_url                  varchar(255),
    status                     varchar(255)   not null,
    member_id                  bigint         not null,
    product_id                 bigint         not null,
    maximum_amount             decimal(38, 2) not null,
    minimum_amount             decimal(38, 2) not null,
    my_finished_payment_amount decimal(38, 2),
    address_name               varchar(255)   not null,
    detail_address             varchar(255)   not null,
    delivery_request_message   varchar(255),
    jibun_address              varchar(255)   not null,
    phone_number               varchar(255)   not null,
    recipient_name             varchar(255)   not null,
    road_address               varchar(255)   not null,
    zonecode                   varchar(255)   not null,
    created_date               datetime(6) not null,
    updated_date               datetime(6),
    constraint FK_funding_product
        foreign key (product_id) references product (id),
    constraint FK_funding_member
        foreign key (member_id) references member (id)
);

create table if not exists funding_message
(
    id             bigint auto_increment primary key,
    member_from_id bigint       not null,
    member_to_id   bigint       not null,
    content        varchar(255) not null,
    visible        varchar(255) not null,
    created_date   datetime(6) not null,
    updated_date   datetime(6),
    constraint FK_funding_message_from_member
        foreign key (member_from_id) references member (id),
    constraint FK_funding_message_to_member
        foreign key (member_to_id) references member (id)
);

create table if not exists funding_participant
(
    id                 bigint auto_increment primary key,
    amount             decimal(38, 2) not null,
    funding_id         bigint         not null,
    funding_message_id bigint         not null,
    member_id          bigint         not null,
    payment_id         bigint         not null,
    status             varchar(255)   not null,
    created_date       datetime(6) not null,
    updated_date       datetime(6),
    constraint UK_funding_participant_payment
        unique (payment_id),
    constraint UK_funding_participant_message
        unique (funding_message_id),
    constraint FK_funding_participant_funding
        foreign key (funding_id) references funding (id),
    constraint FK_funding_participant_message
        foreign key (funding_message_id) references funding_message (id),
    constraint FK_funding_participant_payment
        foreign key (payment_id) references toss_payment (id),
    constraint FK_funding_participant_member
        foreign key (member_id) references member (id)
);

create table if not exists notification
(
    id           bigint auto_increment primary key,
    is_read      boolean      not null,
    member_id    bigint       not null,
    title        varchar(255) not null,
    message      varchar(255) not null,
    image_url    varchar(255),
    url          varchar(255) not null,
    type         varchar(255) not null,
    created_date datetime(6) not null,
    updated_date datetime(6),
    constraint FK_notification_member
        foreign key (member_id) references member (id)
);

create table if not exists orders
(
    id                            bigint auto_increment primary key,
    funding_id                    bigint       not null,
    member_id                     bigint       not null,
    product_id                    bigint       not null,
    status                        varchar(255) not null,
    phone_number                  varchar(255) not null,
    possible_reissue_coupon_count int          not null,
    delivery_request_message      varchar(255),
    detail_address                varchar(255) not null,
    jibun_address                 varchar(255) not null,
    address_name                  varchar(255) not null,
    recipient_name                varchar(255) not null,
    road_address                  varchar(255) not null,
    zonecode                      varchar(255) not null,
    created_date                  datetime(6) not null,
    updated_date                  datetime(6),
    constraint UK_orders_funding
        unique (funding_id),
    constraint FK_orders_funding
        foreign key (funding_id) references funding (id),
    constraint FK_orders_product
        foreign key (product_id) references product (id),
    constraint FK_orders_member
        foreign key (member_id) references member (id)
);

create table if not exists order_transaction
(
    id           bigint auto_increment primary key,
    order_id     bigint       not null,
    transaction_id        varchar(255) not null,
    created_date datetime(6) not null,
    updated_date datetime(6),
    constraint UK_order_tx_transaction_id
        unique (transaction_id),
    constraint FK_order_tx_orders
        foreign key (order_id) references orders (id)
);

create table if not exists personal_inquiry
(
    id           bigint auto_increment primary key,
    member_id    bigint       not null,
    content      varchar(255) not null,
    category     varchar(255) not null,
    answer       varchar(255),
    created_date datetime(6) not null,
    updated_date datetime(6),
    constraint FK_personal_inquiry_member
        foreign key (member_id) references member (id)
);

create table if not exists report
(
    id           bigint auto_increment primary key,
    done         boolean      not null,
    domain_id    bigint       not null,
    member_id    bigint       not null,
    content      varchar(255) not null,
    domain       varchar(255) not null,
    created_date datetime(6) not null,
    updated_date datetime(6),
    constraint FK_report_member
        foreign key (member_id) references member (id)
);

create table if not exists sms_history
(
    id            bigint auto_increment primary key,
    status        varchar(255) not null,
    message       varchar(255) not null,
    phone_number  varchar(255) not null,
    error_message varchar(255),
    created_date  datetime(6) not null,
    updated_date  datetime(6) null
);

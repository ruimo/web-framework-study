# --- First database schema

# --- !Ups

set ignorecase true;

create table category (
  id                        bigint not null,
  name                      varchar(255) not null,
  constraint pk_category primary key (id))
;

create table memo (
  id                        bigint not null,
  memo                      varchar(255) not null,
  created_time              timestamp default current_timestamp,
  category_id               bigint,
  constraint pk_memo primary key (id))
;

create sequence category_seq start with 1;

create sequence memo_seq start with 1;

alter table memo add constraint fk_memo_category_1 foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_memo_category_1 on memo (category_id);


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists memo;

drop table if exists category;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists memo_seq;

drop sequence if exists category_seq;


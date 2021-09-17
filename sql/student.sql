--==========================================
-- 관리자 계정
--==========================================
-- student 계정 생성

alter session set "_oracle_script" = true;

create user student
identified by student
default tablespace users;

alter user student quota unlimited on users;

grant connect, resource to student;


--===========================================
-- student 계정
--===========================================
-- member 테이블 생성
-- jdbc 연동시 date타입의 시분초는 사용불가 -> 시분초가 필요한 경우 timestamp 사용할 것
create table member (
	id varchar2(20),
	name varchar2(100) not null,
	gender char(1),
	birthday date,
	email varchar2(500),
	address varchar2(1000),
	reg_date timestamp default systimestamp,

	constraints pk_member_id primary key(id),
	constraints uq_member_email unique(email),
	constraints ck_member_gender check(gender in ('M', 'F'))
);

insert into member 
values ('honggd', '홍길동', 'M', '09-Sep-1990', 'honggd@naver.com', '서울시 강남구 테헤란로 123', default);

insert into member 
values ('gogd', '고길동', 'M', '07-Jul-1970', 'gogd@naver.com', '경기도 구리시 소동 1000', default);

insert into member
values ('sinsa', '신사임당', 'F', '19-Oct-1954', 'sinsa@naver.com', '경상남도 함양시 안의면 1234', default);

insert into member
values ('donghhh', '이동휘', 'M', '12-Feb-1977', 'donghhh@naver.com', '서울시 종로구 풍동 123', default);

select * from member;
commit;


-- 탈퇴회원 관리용 테이블
-- trigger객체 trig_member_del을 생성하여 member테이블에 delete가 실행될 때마다 member_del테이블에 insert
create table member_del
as
	select m.*,
	systimestamp del_date
from
    member m
where 1 = 0; -- 실제 데이터는 제외하고, 테이블 구조만 본떠서 member_del 테이블 생성

desc member_del;


create or replace trigger trig_member_log
	after
		delete on member
		for each row
	begin
	  insert into member_del
	  values(:old.id, :old.name, :old.gender, :old.birthday, :old.email, :old.address, :old.reg_date, SYSTIMESTAMP);
	end;
/

select * from member_del;

delete from member where id = 'rerere';


-- 탈퇴회원관리2 - 하나의 테이블에서 관리
-- not null을 제외한 제약조건은 모두 제거됨 +컬럼기본값도
create table tb_member
as
select
    m.*
from
    member m;
    
select * from tb_member;

-- 테이블 수정
alter table tb_member
add del_date date
add del_flag char(1) default 'N'
add constraints ck_tb_member_del_flag check(del_flag in ('Y', 'N'))
add constraints pk_tb_member_id primary key (id)
add constraints uq_tb_member_email unique(email)
add constraints ck_tb_member_gender check (gender in ('M', 'F'));

alter table tb_member
modify reg_date default sysdate;

-- 제약조건 조회
SELECT ucc.column_name, uc.*
FROM user_constraints uc JOIN user_cons_columns ucc
ON uc.constraint_name = ucc.constraint_name
WHERE uc.table_name = 'TB_MEMBER'; 

-- 기본값 확인
select *
from
    user_tab_columns
where
    table_name = 'TB_MEMBER';
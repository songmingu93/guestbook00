--테이블 생성
create table guestbook(
    no number,
    name varchar2(100),
    password varchar2(400),
    content varchar2(400),
    reg_date date,
    primary key (no)
);

--시퀀스 생성
create sequence guestbook_no
increment by 1
start with 1;

-- 데이터 삽입
insert into guestbook values (seq_guestbook_no.nextval, '이효리', '1234', '첫번째', sysdate);
insert into guestbook values (seq_guestbook_no.nextval, '이정재', '1234', '두번째', sysdate);

-- 커밋
commit;


-- 셀렉트
select  no,
        name,
        password,
        content,
        reg_date
from guestbook;
drop table if exists otp;
create table otp
(
    id            int8 primary key,
    otp           varchar(8)   not null,
    otp_email     varchar(100) not null,
    otp_time      timestamp         not null,
    creation_date timestamp         not null,
    created_by    varchar(100),
    updated_by    varchar(100),
    updated_date  timestamp
);
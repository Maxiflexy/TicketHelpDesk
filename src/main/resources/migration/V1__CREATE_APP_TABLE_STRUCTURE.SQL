create table organization (
                              id	int8 primary key,
                              org_code	varchar(10)	not null,
                              org_name	varchar(255)	not null,
                              org_status	boolean	default true,
                              creation_date	date	not null,
                              created_by	varchar(100)	not null,
                              updated_by    varchar(100),
                              updated_date  date
);

create table role (
                      id	int8 primary key,
                      role_name	varchar(40)	not null,
                      creation_date	date	not null,
                      created_by	varchar(100)	not null,
                      updated_by    varchar(100),
                      updated_date  date
);

create table permission (
                            id int8 primary key,
                            permission_name	varchar(50) not null,
                            creation_date	date	not null,
                            created_by	varchar(100)	not null,
                            updated_by    varchar(100),
                            updated_date  date
);

create table role_permission (
                                 id int8 primary key,
                                 permission_id	int8 not null,
                                 role_id			int8 not null,
                                 foreign key (permission_id) references permission(id) on delete restrict,
                                 foreign key (role_id) references role(id) on delete restrict
);


create table resources (
                           id int8 primary key,
                           resource_url	varchar(100) not null,
                           creation_date	date	not null,
                           created_by	varchar(100)	not null,
                           updated_by    varchar(100),
                           updated_date  date


);

create table resources_role (
                                id  int8 primary key,
                                resource_id	int8 not null,
                                role_id		int8 not null,
                                foreign key (resource_id) references resources(id) on delete restrict,
                                foreign key (role_id) references role(id) on delete restrict,
                                constraint unique_resource_role unique (resource_id, role_id)
);

create table app_user (
                          id int8 primary key,
                          first_name varchar(255) not null,
                          last_name	varchar(255) not null,
                          role_id		int8 not null,
                          org_id		int8 not null,
                          email		varchar(100) not null,
                          username	varchar(100) not null,
                          password	varchar(255) not null,
                          user_status	boolean	default true,
                          is_first_login	boolean	default	true,
                          creation_date	date	not null,
                          created_by	varchar(100) not null,
                          updated_by    varchar(100),
                          updated_date  date,
                          phone_number	varchar(20),
                          refresh_token	text,
                          foreign key (role_id) references role(id) on delete restrict,
                          foreign key (org_id)  references organization(id) on delete restrict,
                          constraint unique_user_email unique(email),
                          constraint unique_user_username unique(username)
);

create table ticket (
                        id		int8	primary key,
                        org_id 	int8	not null,
                        created_by	varchar(100)	not null,
                        creation_date	date	not	null,
                        updated_by    varchar(100),
                        updated_date  date,
                        title	varchar(255)	not null,
                        app_name	varchar(100)	not null,
                        status	boolean	not null,
                        is_assigned	boolean	not null,
                        description	text	not null,
                        priority	varchar(20)	not null,
                        file_name	varchar(20)	not null,
                        file_url	varchar(255) not null,
                        foreign key (org_id)  references organization(id) on delete restrict

);

create table ticket_user (
                             id int8	primary key,
                             ticket_id	int8 not null,
                             user_id		int8	not null,
                             foreign key (user_id)  references app_user(id) on delete restrict,
                             foreign key (ticket_id)  references ticket(id) on delete restrict
);

create sequence ticket_seq;
INSERT INTO role (id, role_name, creation_date, created_by, updated_by, updated_date) VALUES (1, 'ROLE_SUPER_ADMIN', CURRENT_DATE, 'system', 'system', CURRENT_DATE);
INSERT INTO role (id, role_name, creation_date, created_by, updated_by, updated_date) VALUES (2, 'ROLE_ADMIN', CURRENT_DATE, 'system', 'system', CURRENT_DATE);
INSERT INTO role (id, role_name, creation_date, created_by, updated_by, updated_date) VALUES (3, 'ROLE_SUPPORT_CLIENT', CURRENT_DATE, 'system', 'system', CURRENT_DATE);
INSERT INTO role (id, role_name, creation_date, created_by, updated_by, updated_date) VALUES (4, 'ROLE_SUPPORT_INFOMETICS', CURRENT_DATE, 'system', 'system', CURRENT_DATE);
INSERT INTO role (id, role_name, creation_date, created_by, updated_by, updated_date) VALUES (5, 'ROLE_INFOMETICS_USER', CURRENT_DATE, 'system', 'system', CURRENT_DATE);
INSERT INTO role (id, role_name, creation_date, created_by, updated_by, updated_date) VALUES (6, 'ROLE_CLIENT', CURRENT_DATE, 'system', 'system', CURRENT_DATE);
INSERT INTO organization(id, org_code, org_name, creation_date, created_by, updated_by, updated_date) VALUES (1, 'INFMT','INFOMETICS', CURRENT_DATE, 'system', 'system', CURRENT_DATE);
-- Usuário administrador inicial (login: admin / senha: admin) - senha armazenada com BCrypt
insert into usuarios (login, senha, perfil)
select 'admin', '$2a$10$PpiOUfvdF3E4L88649yM/uEof5885EfMmXA9VASjM1Ss5hFO3G8kS', 'ADMIN'
from dual
where not exists (select 1 from usuarios where login = 'admin');

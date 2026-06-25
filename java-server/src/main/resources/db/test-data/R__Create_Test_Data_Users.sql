-- Generated Test Data for Users

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('941fa79f-121a-4529-a82a-74f1aef40410', 'admin1@example.com', '$2a$12$37U0FFuXX.lF07seLLUg5.o2MZrGXoFBweENC3Ga9/4qP.XVx9YEq', 'ADMIN', true, false, '2026-06-25 09:20:38Z', '2026-06-25 09:20:38Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('7945f35a-fc3a-4df6-8404-e5923a2eb810', 'employee1@example.com', '$2a$12$HfhEjFfrbey3X4wYnCegZe/snt5U5De0hzeZNc2TncEU7xuQAtVvW', 'EMPLOYEE', true, false, '2026-06-25 09:20:38Z', '2026-06-25 09:20:38Z');
INSERT INTO employees (user_id, phone, birth_date, created_at, updated_at) VALUES ('7945f35a-fc3a-4df6-8404-e5923a2eb810', '+380892566900', '1993-07-18', '2026-06-25 09:20:38Z', '2026-06-25 09:20:38Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('8729cdac-4d69-4c0e-b825-f430fdef2269', 'employee2@example.com', '$2a$12$U6/XNagYQxzMuem7rJWg1.oGr9/N8lcN06YFFPn.HPblL1LRvNGQO', 'EMPLOYEE', true, false, '2026-06-25 09:20:39Z', '2026-06-25 09:20:39Z');
INSERT INTO employees (user_id, phone, birth_date, created_at, updated_at) VALUES ('8729cdac-4d69-4c0e-b825-f430fdef2269', '+380140125450', '1992-11-02', '2026-06-25 09:20:39Z', '2026-06-25 09:20:39Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('a877e578-c8ff-481b-9fe5-d48b5a491350', 'employee3@example.com', '$2a$12$QXlEl32KSJZQOcxznQmd2.ZZPPdGO6EtvxloIpbiVs6YdjkVRk526', 'EMPLOYEE', true, false, '2026-06-25 09:20:39Z', '2026-06-25 09:20:39Z');
INSERT INTO employees (user_id, phone, birth_date, created_at, updated_at) VALUES ('a877e578-c8ff-481b-9fe5-d48b5a491350', '+380555045121', '1987-07-08', '2026-06-25 09:20:39Z', '2026-06-25 09:20:39Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('73ac6f0c-c373-468a-b815-6b9aa82d536f', 'employee4@example.com', '$2a$12$zdkHQwwMHIu6Epl0j1Cm.ekDRuDpy7IazGxUZSwZqb7yLpBY6H3MO', 'EMPLOYEE', true, false, '2026-06-25 09:20:40Z', '2026-06-25 09:20:40Z');
INSERT INTO employees (user_id, phone, birth_date, created_at, updated_at) VALUES ('73ac6f0c-c373-468a-b815-6b9aa82d536f', '+380795920350', '1991-01-05', '2026-06-25 09:20:40Z', '2026-06-25 09:20:40Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('2e2c0894-4098-4d90-8856-eaa1653c4cf6', 'employee5@example.com', '$2a$12$NwAVnSIw.c0x5WQ7gsFnnOSBxqZCntRoMY2gjGod7MqHcsCjyO8/C', 'EMPLOYEE', true, false, '2026-06-25 09:20:40Z', '2026-06-25 09:20:40Z');
INSERT INTO employees (user_id, phone, birth_date, created_at, updated_at) VALUES ('2e2c0894-4098-4d90-8856-eaa1653c4cf6', '+380435119370', '1984-04-27', '2026-06-25 09:20:40Z', '2026-06-25 09:20:40Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('5106740f-8002-4cc7-b28f-848f9b817b32', 'client1@example.com', '$2a$12$Lfi/FX27nvNaCN4L2AAZVum/tb/yJD9NfERaJtlZkOFC.h6Axtffa', 'CLIENT', true, false, '2026-06-25 09:20:40Z', '2026-06-25 09:20:40Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('5106740f-8002-4cc7-b28f-848f9b817b32', '2026-06-25 09:20:40Z', '2026-06-25 09:20:40Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('838eb610-89ff-43a2-9ae1-d93e5391f9a1', 'client2@example.com', '$2a$12$YIEMIRlm5cdrDOqTIk/8VO8FmWq30Z1Yxna5fZzSZO2yRn1nWXXcC', 'CLIENT', true, false, '2026-06-25 09:20:41Z', '2026-06-25 09:20:41Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('838eb610-89ff-43a2-9ae1-d93e5391f9a1', '2026-06-25 09:20:41Z', '2026-06-25 09:20:41Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('4e4cd874-c4c3-4f9e-ba0b-ea173dea9faf', 'client3@example.com', '$2a$12$zRFByEm9Nmx0/2goDv4K0OuDENcUTbpNiHl98ZZ0wmzq5SZgtgNMa', 'CLIENT', true, false, '2026-06-25 09:20:41Z', '2026-06-25 09:20:41Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('4e4cd874-c4c3-4f9e-ba0b-ea173dea9faf', '2026-06-25 09:20:41Z', '2026-06-25 09:20:41Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('7440aa9a-14f7-4f90-938a-0dab925097d8', 'client4@example.com', '$2a$12$HfNDVUOGZJv0E7MI8ydnDe6.C2RgFZuuWjn79PHnfE9yzR.pOK3f6', 'CLIENT', true, false, '2026-06-25 09:20:42Z', '2026-06-25 09:20:42Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('7440aa9a-14f7-4f90-938a-0dab925097d8', '2026-06-25 09:20:42Z', '2026-06-25 09:20:42Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('bc1c9a2b-dd39-4879-a2c7-3b127efb5cbc', 'client5@example.com', '$2a$12$G6756W.k2MY9n4jhI.FO6.TfteWkeDR4oQgdefBTBCi2maZGIk7Ju', 'CLIENT', true, false, '2026-06-25 09:20:42Z', '2026-06-25 09:20:42Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('bc1c9a2b-dd39-4879-a2c7-3b127efb5cbc', '2026-06-25 09:20:42Z', '2026-06-25 09:20:42Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('798d83b9-988b-455a-b3d9-fffaf6ca8840', 'client6@example.com', '$2a$12$P.AMagRMimi2ya81hEz5.uzx9BpWKej26ni62gX6So.CDY/rNYavq', 'CLIENT', true, false, '2026-06-25 09:20:42Z', '2026-06-25 09:20:42Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('798d83b9-988b-455a-b3d9-fffaf6ca8840', '2026-06-25 09:20:42Z', '2026-06-25 09:20:42Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('5fb224e6-0b0d-4e1e-a3d7-8bb93bdc0a4b', 'client7@example.com', '$2a$12$s7de.3YO2yKOw9TaYS9zje3y3EJwR2yl2iiKQQfZTdpaFZzi5Pg8K', 'CLIENT', true, false, '2026-06-25 09:20:43Z', '2026-06-25 09:20:43Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('5fb224e6-0b0d-4e1e-a3d7-8bb93bdc0a4b', '2026-06-25 09:20:43Z', '2026-06-25 09:20:43Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('39a099d0-7425-4e81-aafb-5805377c2ad0', 'client8@example.com', '$2a$12$he5kCnXaB1.t1MknuY0fjOnAQ93PaL0o9ANDhLDSXOWjjqn667tNG', 'CLIENT', true, false, '2026-06-25 09:20:43Z', '2026-06-25 09:20:43Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('39a099d0-7425-4e81-aafb-5805377c2ad0', '2026-06-25 09:20:43Z', '2026-06-25 09:20:43Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('79407b69-8769-4804-a30c-09176d541b1f', 'client9@example.com', '$2a$12$FEHo8dSGLIYJFUgHISCN1e6t0g1AmTU0CTLZWHJi56EiYM/2.E.rq', 'CLIENT', true, false, '2026-06-25 09:20:44Z', '2026-06-25 09:20:44Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('79407b69-8769-4804-a30c-09176d541b1f', '2026-06-25 09:20:44Z', '2026-06-25 09:20:44Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('bd8919fd-b300-4c2a-b875-73c9b1b74bd7', 'client10@example.com', '$2a$12$6bZ8rWCHk6nVMmpY.khlvuq9tTdC44M5sGPbpzgat6bhr/xixoixe', 'CLIENT', true, false, '2026-06-25 09:20:44Z', '2026-06-25 09:20:44Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('bd8919fd-b300-4c2a-b875-73c9b1b74bd7', '2026-06-25 09:20:44Z', '2026-06-25 09:20:44Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('5e1f26db-5912-44a9-8ed4-019e407e5889', 'client11@example.com', '$2a$12$kq3u6.Lh6WJdGZLSnrvwIus2Kak9BG7QMNso44833tGYyQQjhHe62', 'CLIENT', true, false, '2026-06-25 09:20:44Z', '2026-06-25 09:20:44Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('5e1f26db-5912-44a9-8ed4-019e407e5889', '2026-06-25 09:20:44Z', '2026-06-25 09:20:44Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('799f1105-7c16-46d2-9b17-882af86c5520', 'client12@example.com', '$2a$12$7WLzJrod9GNhkIrsSQwxp.A1Cn3WPf/JJHBZeA2qropmKIfG0fKx.', 'CLIENT', true, false, '2026-06-25 09:20:45Z', '2026-06-25 09:20:45Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('799f1105-7c16-46d2-9b17-882af86c5520', '2026-06-25 09:20:45Z', '2026-06-25 09:20:45Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('8db52fd9-3fa2-443e-bd86-09a87d0c3241', 'client13@example.com', '$2a$12$Gw61S6pFICfBMWIJlzQWh.JVFPjlAC99PATRpAsJ55/SBYPjyC.4i', 'CLIENT', true, false, '2026-06-25 09:20:45Z', '2026-06-25 09:20:45Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('8db52fd9-3fa2-443e-bd86-09a87d0c3241', '2026-06-25 09:20:45Z', '2026-06-25 09:20:45Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('6ee1b079-c4e7-4195-9c82-ac458688a9aa', 'client14@example.com', '$2a$12$MkkkcJn45ARVyiQ.szuDQuXnOBv2irz7CB089erFxkQvIrPyinke6', 'CLIENT', true, false, '2026-06-25 09:20:46Z', '2026-06-25 09:20:46Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('6ee1b079-c4e7-4195-9c82-ac458688a9aa', '2026-06-25 09:20:46Z', '2026-06-25 09:20:46Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('162fecb7-95a7-4c16-9bf9-440d4e605bb9', 'client15@example.com', '$2a$12$MfIEO82wPFPHe4b532G/GOkzuy3IVTVfZ.EtjziHfQ3F3iA73QoBK', 'CLIENT', true, false, '2026-06-25 09:20:46Z', '2026-06-25 09:20:46Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('162fecb7-95a7-4c16-9bf9-440d4e605bb9', '2026-06-25 09:20:46Z', '2026-06-25 09:20:46Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('5c5efb0d-7941-43d5-b8bb-3941154beb5c', 'client16@example.com', '$2a$12$FAicyxt07e7v.LDibCKhI.LHQXBUs3cSr8dtYUjeLlF1De9eSAxVa', 'CLIENT', true, false, '2026-06-25 09:20:46Z', '2026-06-25 09:20:46Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('5c5efb0d-7941-43d5-b8bb-3941154beb5c', '2026-06-25 09:20:46Z', '2026-06-25 09:20:46Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('2f7b093f-e9a7-4bda-8802-78f0317caf0f', 'client17@example.com', '$2a$12$xtELZhtXcA0yeAtuna09dutw4FZiklCg2S8BeidM/sf0lBrBNhn0O', 'CLIENT', true, false, '2026-06-25 09:20:47Z', '2026-06-25 09:20:47Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('2f7b093f-e9a7-4bda-8802-78f0317caf0f', '2026-06-25 09:20:47Z', '2026-06-25 09:20:47Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('95bb3efd-9052-4cd2-92b1-17889368f3c9', 'client18@example.com', '$2a$12$YjwhZxHHjGhqkZfUDTbXp.k.ciHXJ3.oegkzPGCd/D7aVMpVIenay', 'CLIENT', true, false, '2026-06-25 09:20:47Z', '2026-06-25 09:20:47Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('95bb3efd-9052-4cd2-92b1-17889368f3c9', '2026-06-25 09:20:47Z', '2026-06-25 09:20:47Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('33f85d7e-6418-40c9-a97d-a5806488bd7d', 'client19@example.com', '$2a$12$AHqXG/HXYjqIAO8xYkj/6OJ0nbaaRUXFpsoIRQ6Ajm46dHhM8f2cC', 'CLIENT', true, false, '2026-06-25 09:20:48Z', '2026-06-25 09:20:48Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('33f85d7e-6418-40c9-a97d-a5806488bd7d', '2026-06-25 09:20:48Z', '2026-06-25 09:20:48Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('a8f0372c-8554-4284-905d-3501d3cca3d2', 'client20@example.com', '$2a$12$gUuALWjrCN72u7FSg/eBle6qPlk2IL9281f6f8reFwf/y.Bq1QTcO', 'CLIENT', true, false, '2026-06-25 09:20:48Z', '2026-06-25 09:20:48Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('a8f0372c-8554-4284-905d-3501d3cca3d2', '2026-06-25 09:20:48Z', '2026-06-25 09:20:48Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('e26ede44-5686-43a7-a49e-a7d37a3971cf', 'client21@example.com', '$2a$12$7fYBL8wXPw8cazelcTkhFe.WH7CB/1dCzW4guA8Wgav8aCTTM8xlm', 'CLIENT', true, false, '2026-06-25 09:20:48Z', '2026-06-25 09:20:48Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('e26ede44-5686-43a7-a49e-a7d37a3971cf', '2026-06-25 09:20:48Z', '2026-06-25 09:20:48Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('a5ec325d-59db-4140-a0ae-11a998cf5ebf', 'client22@example.com', '$2a$12$tQDR40vstKw7eHGg5CaGCe2Ir9aU.Uyhlyh0QDl/nx2Fs90vPGZkW', 'CLIENT', true, false, '2026-06-25 09:20:49Z', '2026-06-25 09:20:49Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('a5ec325d-59db-4140-a0ae-11a998cf5ebf', '2026-06-25 09:20:49Z', '2026-06-25 09:20:49Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('4e499c55-6857-4bcd-b6d7-22a425032241', 'client23@example.com', '$2a$12$VZJkRvGPwboSdPM41bASMuBtF5aOPcegXDCGbuUdxeRodjnBSPBAK', 'CLIENT', true, false, '2026-06-25 09:20:49Z', '2026-06-25 09:20:49Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('4e499c55-6857-4bcd-b6d7-22a425032241', '2026-06-25 09:20:49Z', '2026-06-25 09:20:49Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('6bdf017a-7b4c-4dd1-a7d1-47e93e224956', 'client24@example.com', '$2a$12$vb78riyGbtkiiOoOC6RB..rdOEzCfZDsHevRrqKqSnwx5jpmNlZQi', 'CLIENT', true, false, '2026-06-25 09:20:50Z', '2026-06-25 09:20:50Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('6bdf017a-7b4c-4dd1-a7d1-47e93e224956', '2026-06-25 09:20:50Z', '2026-06-25 09:20:50Z');

INSERT INTO users (id, email, password, role, is_verified, is_blocked, created_at, updated_at) VALUES ('00dcd2cb-c16e-4864-95d7-7e0fd5c6bf5b', 'client25@example.com', '$2a$12$0..DX3Tth2oNAgQSsaR.aOY471vXbhi4dexh3A7ij/KokU.8gsfq6', 'CLIENT', true, false, '2026-06-25 09:20:50Z', '2026-06-25 09:20:50Z');
INSERT INTO clients (user_id, created_at, updated_at) VALUES ('00dcd2cb-c16e-4864-95d7-7e0fd5c6bf5b', '2026-06-25 09:20:50Z', '2026-06-25 09:20:50Z');


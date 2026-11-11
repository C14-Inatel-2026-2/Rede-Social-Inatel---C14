USE db;

INSERT IGNORE INTO usuario (email, senha, nome) VALUES
    ('smoke1@teste.com', '$2a$10$5qCrdwLqY8INxkVZvZRTHOFQpby.3gKtPQTHSDuvM7/V3UqN6RhTK', 'smoke_01'),
    ('smoke2@teste.com', '$2a$10$N4XQtOij9fs6oj9FPS/Pku/wrptFmvU2bxcdWxA905TWx948DyLDm', 'smoke_02'),
    ('smoke3@teste.com', '$2a$10$/NVAheoDN11y6mZc80uYsu.njtyZfwJWufftKLikU/soF7MzJEDmq', 'smoke_03'),
    ('smoke4@teste.com', '$2a$10$g6w2WxcVgaTwjPenAYw4le1HJEkhJ/kMhNZkMBm9rjyJ3GXL8qt66', 'smoke_04'),
    ('smoke5@teste.com', '$2a$10$ApJ6TCs9DP8UgE9jRJEs7.6u610x0p9Z6uRht5CRxHgP.Khc7emni', 'smoke_05'),
    ('smoke6@teste.com', '$2a$10$LPg9pE8hhpsSHDdsPO/WjOQ8YVybLkceF5MrVTOQ1yhUqkuzj/KKK', 'smoke_06');
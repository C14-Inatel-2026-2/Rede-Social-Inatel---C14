-- Dados de teste locais para o fluxo smoke com duas instancias do cliente.
-- O INSERT IGNORE torna este seed seguro para executar mais de uma vez.

USE db;

INSERT IGNORE INTO usuario (email, senha, nome) VALUES
    ('smoke1@teste.com', 'Smoke@123', 'smoke_01'),
    ('smoke2@teste.com', 'Smoke@123', 'smoke_02');

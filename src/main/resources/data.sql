DELETE FROM medicos WHERE email = 'admin@admin.com';
DELETE FROM medicos WHERE email = 'medico@teste.com';

INSERT INTO medicos (nome, email, senha, crm, especialidade, is_admin)
VALUES (
    'Administrador',
    'admin@admin.com',
    'admin',
    'ADMIN-123',
    'Administrador do Sistema',
    true
);

INSERT INTO medicos (nome, email, senha, crm, especialidade, is_admin)
VALUES (
    'Médico Teste',
    'medico@teste.com',
    'senha123',
    'CRM-456',
    'Clínico Geral',
    false
); 
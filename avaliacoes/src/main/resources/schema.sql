-- USUARIO
CREATE TABLE IF NOT EXISTS usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

-- QUESTAO
CREATE TABLE IF NOT EXISTS questao (
    id SERIAL PRIMARY KEY,
    conteudo TEXT NOT NULL,
    tema_id INTEGER REFERENCES tema(id)
);

-- TEMA
CREATE TABLE IF NOT EXISTS tema (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

-- QUESTIONARIO
CREATE TABLE IF NOT EXISTS questionario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    nota INTEGER,
    id_user INTEGER REFERENCES usuario(id)
);

-- QUESTIONARIO <-> TEMA
CREATE TABLE IF NOT EXISTS questionario_tema (
    questionario_id INTEGER NOT NULL REFERENCES questionario(id) ON DELETE CASCADE,
    tema_id INTEGER NOT NULL REFERENCES tema(id) ON DELETE CASCADE,
    PRIMARY KEY (questionario_id, tema_id)
);

-- QUESTIONARIO <-> QUESTAO
CREATE TABLE IF NOT EXISTS questionario_questao (
    questionario_id INTEGER NOT NULL REFERENCES questionario(id) ON DELETE CASCADE,
    questao_id INTEGER NOT NULL REFERENCES questao(id) ON DELETE CASCADE,
    PRIMARY KEY (questionario_id, questao_id)
);

-- RESPOSTA
CREATE TABLE IF NOT EXISTS resposta (
    id SERIAL PRIMARY KEY,
    texto VARCHAR(255) NOT NULL,
    e_correta BOOLEAN DEFAULT FALSE,
    questao_id INTEGER REFERENCES questao(id) ON DELETE CASCADE
);

-- RESULTADO
CREATE TABLE IF NOT EXISTS resultado (
    id SERIAL PRIMARY KEY,
    nota DOUBLE PRECISION,
    data TIMESTAMP,
    id_user INTEGER REFERENCES usuario(id),
    id_questionario INTEGER REFERENCES questionario(id)
);
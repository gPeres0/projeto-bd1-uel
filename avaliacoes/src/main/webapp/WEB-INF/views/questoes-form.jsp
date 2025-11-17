<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Questão</title></head>
<body>
<h2>${questao.id == null ? "Nova" : "Editar"} Questão</h2>

<form action="/questoes" method="post">
    <input type="hidden" name="id" value="${questao.id}" />

    Enunciado:
    <input type="text" name="enunciado" value="${questao.enunciado}" required><br><br>

    Tipo:
    <select name="tipo">
        <option value="MULTIPLA_ESCOLHA">Múltipla Escolha</option>
        <option value="TEXTO">Texto</option>
    </select><br><br>

    <button type="submit">Salvar</button>
</form>

<a href="/questoes">Voltar</a>

</body>
</html>

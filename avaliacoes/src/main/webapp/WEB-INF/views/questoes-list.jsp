<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Questões</title></head>
<body>
<h2>Lista de Questões</h2>
<a href="/questoes/nova">Cadastrar Nova Questão</a><br><br>

<table border="1">
    <tr>
        <th>ID</th>
        <th>Enunciado</th>
        <th>Tipo</th>
        <th>Ações</th>
    </tr>
    <c:forEach var="q" items="${questoes}">
        <tr>
            <td>${q.id}</td>
            <td>${q.enunciado}</td>
            <td>${q.tipo}</td>
            <td>
                <a href="/questoes/editar/${q.id}">Editar</a> |
                <a href="/questoes/excluir/${q.id}">Excluir</a>
            </td>
        </tr>
    </c:forEach>
</table>

</body>
</html>

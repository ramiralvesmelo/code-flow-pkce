<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8">
  <title>Resposta API</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
  <div class="container text-center py-5">
    <h1 class="display-4 mb-4">📡 Resposta da API</h1>
    <div class="card shadow-sm p-4 text-start">
      <pre>${apiResponse}</pre>
    </div>
    <div class="mt-4 d-grid gap-2 col-6 mx-auto">
      <a href="/" class="btn btn-secondary btn-lg">Voltar</a>
      <a href="/call-api" class="btn btn-primary btn-lg">Chamar novamente</a>
    </div>
  </div>
</body>
</html>

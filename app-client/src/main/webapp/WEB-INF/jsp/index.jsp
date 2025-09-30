<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Portal</title>
  <!-- Bootstrap CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
  <div class="container text-center py-5">
    <h1 class="display-4 mb-4">🌐 Portal</h1>
    <div class="card shadow-sm p-4">
      <p class="lead">Bem-vindo! Escolha uma das opções abaixo:</p>
      <div class="d-grid gap-2 col-6 mx-auto mt-4">
        <a href="/call-api" class="btn btn-primary btn-lg">Chamar API Protegida</a>
        <a href="/oauth2/authorization/keycloak" class="btn btn-success btn-lg">Login</a>

        <!-- Logout via POST -->
        <form method="post" action="/logout" class="d-grid">
          <button type="submit" class="btn btn-danger btn-lg">Logout</button>
        </form>
      </div>
    </div>
  </div>

  <!-- Bootstrap JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

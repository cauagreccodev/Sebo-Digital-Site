# Copie este arquivo para scripts/local-env.ps1 e preencha somente no seu PC.
# O arquivo scripts/local-env.ps1 fica ignorado pelo Git.

$env:GOOGLE_CLIENT_ID = "seu-client-id.apps.googleusercontent.com"
$env:GOOGLE_CLIENT_SECRET = "seu-client-secret"

# Banco Neon/PostgreSQL. Use a URL JDBC, nao a URL /rest/v1.
$env:DB_URL = "jdbc:postgresql://seu-host-neon/neondb?sslmode=require"
$env:DB_USERNAME = "seu-usuario-neon"
$env:DB_PASSWORD = "sua-senha-neon"

# Opcional: use apenas se for configurar Facebook.
$env:FACEBOOK_CLIENT_ID = ""
$env:FACEBOOK_CLIENT_SECRET = ""

# URLs usadas no fluxo OAuth local.
$env:APP_FRONTEND_LOGIN_URL = "http://127.0.0.1:5500/login.html"
$env:APP_FRONTEND_ALLOWED_REDIRECT_ORIGINS = "http://localhost:5500,http://127.0.0.1:5500"

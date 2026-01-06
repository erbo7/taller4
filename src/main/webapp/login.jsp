<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Sistema Veterinario DiazPet</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .login-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            width: 100%;
            max-width: 400px;
            padding: 40px;
        }
        
        .login-header {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .login-header h1 {
            color: #667eea;
            font-size: 28px;
            font-weight: bold;
            margin-bottom: 10px;
        }
        
        .login-header p {
            color: #999;
            font-size: 14px;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-label {
            color: #333;
            font-weight: 600;
            margin-bottom: 8px;
            display: block;
        }
        
        .form-control {
            border: 2px solid #e0e0e0;
            border-radius: 5px;
            padding: 12px 15px;
            font-size: 14px;
            transition: all 0.3s ease;
        }
        
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
            outline: none;
        }
        
        .btn-login {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            font-weight: 600;
            padding: 12px;
            border-radius: 5px;
            width: 100%;
            cursor: pointer;
            transition: all 0.3s ease;
            margin-top: 20px;
        }
        
        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
            color: white;
        }
        
        .alert-error {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
            padding: 12px 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            font-size: 14px;
        }
        
        .icon-input {
            position: relative;
        }
        
        .icon-input .form-control {
            padding-left: 40px;
        }
        
        .icon-input .icon {
            position: absolute;
            left: 12px;
            top: 38px;
            color: #999;
        }
        
        .remember-forgot {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 15px;
            font-size: 13px;
        }
        
        .remember-forgot a {
            color: #667eea;
            text-decoration: none;
        }
        
        .remember-forgot a:hover {
            text-decoration: underline;
        }
        
        .footer-text {
            text-align: center;
            color: #999;
            font-size: 13px;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <!-- Header -->
        <div class="login-header">
            <h1>🐾 DiazPet</h1>
            <p>Sistema Veterinario</p>
        </div>
        
        <!-- Mostrar error si existe -->
        <% 
            String error = (String) request.getAttribute("error");
            if (error != null) {
        %>
            <div class="alert-error">
                <strong>⚠️ Error:</strong> <%= error %>
            </div>
        <% 
            }
        %>
        
        <!-- Formulario de Login -->
        <form action="LoginServlet" method="POST">
            <div class="form-group">
                <label class="form-label" for="usuario">Usuario</label>
                <div class="icon-input">
                    <span class="icon">👤</span>
                    <input type="text" 
                           class="form-control" 
                           id="usuario" 
                           name="usuario" 
                           placeholder="Ingresa tu usuario"
                           required>
                </div>
            </div>
            
            <div class="form-group">
                <label class="form-label" for="contrasena">Contraseña</label>
                <div class="icon-input">
                    <span class="icon">🔒</span>
                    <input type="password" 
                           class="form-control" 
                           id="contrasena" 
                           name="contrasena" 
                           placeholder="Ingresa tu contraseña"
                           required>
                </div>
            </div>
            
            <div class="remember-forgot">
                <label style="margin: 0; cursor: pointer;">
                    <input type="checkbox" name="remember" style="cursor: pointer;">
                    Recuérdame
                </label>
                <a href="#">¿Olvidaste tu contraseña?</a>
            </div>
            
            <button type="submit" class="btn-login">Iniciar Sesión</button>
        </form>
        
        <div class="footer-text">
            <p>Sistema de Gestión Veterinaria © 2025</p>
        </div>
    </div>
    
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
</body>
</html>
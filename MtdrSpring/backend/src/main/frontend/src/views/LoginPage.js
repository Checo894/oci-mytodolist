import React, { useState } from "react";
import { useHistory } from "react-router-dom";

function LoginPage() {
  const history = useHistory();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}developers/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, passwordHash: password }),
      });

      if (!response.ok) {
        throw new Error("Credenciales incorrectas");
      }

      const data = await response.json();
      console.log("Respuesta del backend:", data);

      // Guardamos todo en localStorage
      localStorage.setItem("role", data.role);
      localStorage.setItem("developerId", data.developerId);
      localStorage.setItem("name", data.name);
      localStorage.setItem("message", data.message);

      // Redirigimos según el rol
      if (data.role === "developer") {
        history.push("/developer");
      } else if (data.role === "projectmanager") {
        history.push("/manager");
      } else {
        throw new Error("Rol no reconocido");
      }
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="login-container">
      <h2>Iniciar sesión</h2>
      <form onSubmit={handleLogin}>
        <div>
          <label>Email:</label><br />
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            placeholder="ejemplo@correo.com"
          />
        </div>
        <div>
          <label>Contraseña:</label><br />
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            placeholder="••••••••"
          />
        </div>
        {error && <p style={{ color: "red" }}>{error}</p>}
        <button type="submit">Ingresar</button>
      </form>
    </div>
  );
}

export default LoginPage;

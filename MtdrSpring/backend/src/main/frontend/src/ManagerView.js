import React from "react";

function ManagerView() {
  const user = JSON.parse(localStorage.getItem("user"));

  return (
    <div className="dashboard">
      <h2>Bienvenido, {user?.name || "Project Manager"}</h2>
      <p>Esta es la vista para project managers.</p>
      {/* Aquí puedes mostrar tareas, subtareas, y más funciones de admin */}
    </div>
  );
}

export default ManagerView;

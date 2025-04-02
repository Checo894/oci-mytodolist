import React, { useEffect, useState } from "react";
import Button from "@mui/material/Button";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";


function SprintSection() {
  const [sprints, setSprints] = useState([]);
  const [error, setError] = useState(null);

  const [showModal, setShowModal] = useState(false);
    const [newSprint, setNewSprint] = useState({
    sprintNumber: "",
    startDate: "",
    endDate: ""
    });


  useEffect(() => {
    fetch("http://localhost:8080/sprints")
      .then((res) => res.ok ? res.json() : Promise.reject("Error al cargar sprints"))
      .then(setSprints)
      .catch(setError);
  }, []);

  return (
    <div>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <h2>Sprints</h2>
        <Button
        variant="contained"
        style={{ backgroundColor: "#A7D7C5", color: "#000" }}
        startIcon={<AddCircleOutlineIcon />}
        onClick={() => setShowModal(true)}
        >
        Crear Sprint
        </Button>

      </div>

      {error && <p style={{ color: "red" }}>{error}</p>}

      <div style={{ display: "flex", flexWrap: "wrap", gap: "1rem", marginTop: "1rem" }}>
        {sprints.map((sprint) => (
          <div key={sprint.id} style={{
            background: "#333",
            color: "#fff",
            padding: "1rem",
            borderRadius: "10px",
            width: "300px"
          }}>
            <h3>Sprint #{sprint.sprintNumber}</h3>
            <p><strong>Inicio:</strong> {sprint.startDate}</p>
            <p><strong>Fin:</strong> {sprint.endDate}</p>
            <p><strong>Tareas:</strong> {sprint.tasks?.length || 0}</p>
            <Button
              variant="outlined"
              size="small"
              style={{ marginTop: "0.5rem", borderColor: "#A7D7C5", color: "#A7D7C5" }}
              onClick={() => alert(`Ver tareas del Sprint #${sprint.sprintNumber}`)}
            >
              View Tasks
            </Button>
          </div>
        ))}
      </div>
      {showModal && (
        <div style={{
            position: "fixed", top: 0, left: 0, width: "100%", height: "100%",
            backgroundColor: "rgba(0,0,0,0.6)", display: "flex", justifyContent: "center", alignItems: "center",
            zIndex: 9999
        }}>
            <div style={{
            background: "white", padding: "2rem", borderRadius: "12px", width: "400px", maxWidth: "90%", position: "relative"
            }}>
            <h3>🟩 Crear nuevo Sprint</h3>

            <label>Número de Sprint:</label>
            <input
                type="number"
                value={newSprint.sprintNumber}
                onChange={(e) => setNewSprint({ ...newSprint, sprintNumber: e.target.value })}
                style={{ width: "100%", marginBottom: "1rem", padding: "0.5rem" }}
            />

            <label>Fecha de inicio:</label>
            <input
                type="date"
                value={newSprint.startDate}
                onChange={(e) => setNewSprint({ ...newSprint, startDate: e.target.value })}
                style={{ width: "100%", marginBottom: "1rem", padding: "0.5rem" }}
            />

            <label>Fecha de fin:</label>
            <input
                type="date"
                value={newSprint.endDate}
                onChange={(e) => setNewSprint({ ...newSprint, endDate: e.target.value })}
                style={{ width: "100%", marginBottom: "1rem", padding: "0.5rem" }}
            />

            <div style={{ display: "flex", justifyContent: "space-between" }}>
                <Button variant="outlined" color="error" onClick={() => setShowModal(false)}>
                Cancelar
                </Button>
                <Button
                variant="contained"
                style={{ backgroundColor: "#A7D7C5" }}
                onClick={async () => {
                    try {
                    const response = await fetch("http://localhost:8080/sprints", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({
                        sprintNumber: parseInt(newSprint.sprintNumber),
                        startDate: newSprint.startDate,
                        endDate: newSprint.endDate
                        }),
                    });

                    if (!response.ok) throw new Error("Error al crear sprint");

                    const created = await response.json();
                    setSprints((prev) => [...prev, created]);

                    setShowModal(false);
                    setNewSprint({ sprintNumber: "", startDate: "", endDate: "" });
                    } catch (error) {
                    alert("No se pudo crear el sprint");
                    console.error(error);
                    }
                }}
                >
                Crear Sprint
                </Button>
            </div>
            </div>
        </div>
        )}

    </div>
    
  );
}

export default SprintSection;

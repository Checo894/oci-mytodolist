import React, { useEffect, useState } from "react";
import Button from "@mui/material/Button";
import { useHistory } from "react-router-dom";

function DeveloperView() {
  const history = useHistory();
  const [subtasks, setSubtasks] = useState([]);
  const [error, setError] = useState(null);

  const developerId = parseInt(localStorage.getItem("developerId"));
  const developerName = localStorage.getItem("name");

  const [selectedSubtask, setSelectedSubtask] = useState(null);
  const [detailData, setDetailData] = useState(null);

  const [confirmingSubtask, setConfirmingSubtask] = useState(null);
  const [actualHoursInput, setActualHoursInput] = useState("");



  useEffect(() => {
    if (!developerId) {
      history.push("/login");
      return;
    }

    fetch(`http://localhost:8080/subtasks/developer/${developerId}`)
      .then((res) => {
        if (!res.ok) throw new Error("Error al cargar subtareas");
        return res.json();
      })
      .then((data) => {
        console.log("Subtasks cargadas:", data);
        setSubtasks(data);
      })
      .catch((err) => setError(err.message));
  }, [developerId, history]);

  const handleComplete = async (subtaskId, currentStatus) => {
    try {
      await fetch(`http://localhost:8080/subtasks/${subtaskId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ completed: !currentStatus }),
      });

      setSubtasks((prev) =>
        prev.map((s) =>
          s.id === subtaskId ? { ...s, completed: !currentStatus } : s
        )
      );
    } catch (err) {
      alert("No se pudo actualizar la subtarea");
    }
  };
  const handleViewDetails = async (subtask) => {
    try {
      const response = await fetch(`http://localhost:8080/subtasks/${subtask.id}/details`);
      if (!response.ok) throw new Error("Error al obtener detalles");
  
      const fullSubtask = await response.json();
      const parentTask = fullSubtask.task || {};
      const sprint = parentTask.sprint || null;
  
      const formattedDetails = {
        title: fullSubtask.title,
        estimatedHours: fullSubtask.estimatedHours,
        taskTitle: parentTask.title || "N/A",
        taskDescription: parentTask.description || "Sin descripción",
        sprint: sprint
          ? {
              number: sprint.sprintNumber,
              id: sprint.id,
              start: sprint.startDate,
              end: sprint.endDate,
            }
          : null,
      };
  
      setDetailData(formattedDetails);
      setSelectedSubtask(subtask.id);
    } catch (error) {
      console.error("Error cargando detalles:", error);
    }
  };
  
  return (
    <div className="developer-view">
      <div style={{ display: "flex", justifyContent: "space-between", padding: "1rem" }}>
        <h3>Hello, {developerName}</h3>
        <Button
          variant="outlined"
          color="error"
          onClick={() => {
            localStorage.clear();
            history.push("/login");
          }}
        >
          Logout
        </Button>
      </div>

      <h2>My Subtasks</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <div style={{ display: "flex", flexWrap: "wrap", gap: "1rem" }}>
        {subtasks.map((subtask) => (
          <div
            key={subtask.id}
            style={{
              background: "#222",
              color: "white",
              padding: "1rem",
              borderRadius: "10px",
              width: "250px",
            }}
          >
            <h4>{subtask.title}</h4>
            <p>Status: {subtask.completed ? "Completed" : "Not Completed"}</p>
            <p>Estimated Hours: {subtask.estimatedHours}</p>

            <Button
                variant="contained"
                size="small"
                onClick={() => handleViewDetails(subtask)}
                style={{ marginRight: "0.5rem" }}
                >
                View Details
            </Button>


            <Button
                variant="contained"
                size="small"
                style={{ backgroundColor: "#d4edda", color: "#000" }}
                onClick={() => {
                    setConfirmingSubtask(subtask);
                    setActualHoursInput("");
                }}
                >
                {subtask.completed ? "Undo" : "Complete"}
            </Button>

          </div>
        ))}
        {selectedSubtask && detailData && (
        <div style={{
            position: "fixed", top: 0, left: 0, width: "100%", height: "100%",
            backgroundColor: "rgba(0,0,0,0.6)", display: "flex", justifyContent: "center", alignItems: "center",
            zIndex: 9999
        }}>
            <div style={{
            background: "white", padding: "2rem", borderRadius: "12px", width: "400px", maxWidth: "90%", position: "relative"
            }}>
            <h3>📄 Detalles de la Subtarea</h3>
            <p>🔹 <strong>Título:</strong> {detailData.title}</p>
            <p>⏱️ <strong>Horas estimadas:</strong> {detailData.estimatedHours}</p>

            <h4>🗂 Tarea Principal:</h4>
            <p>• <strong>Título:</strong> {detailData.taskTitle}</p>
            <p>• <strong>Descripción:</strong> {detailData.taskDescription}</p>

            <h4>📆 Sprint:</h4>
            {detailData.sprint ? (
                <>
                <p>• <strong>Número:</strong> {detailData.sprint.number}</p>
                <p>• <strong>ID:</strong> {detailData.sprint.id}</p>
                <p>• <strong>Fechas:</strong> {detailData.sprint.start} ➡ {detailData.sprint.end}</p>
                </>
            ) : (
                <p>• <em>No asignado</em></p>
            )}

            <Button variant="contained" color="error" style={{ marginTop: "1rem" }} onClick={() => setSelectedSubtask(null)}>
                Cerrar
            </Button>
            </div>
        </div>
        )}
        {confirmingSubtask && (
            <div style={{
                position: "fixed", top: 0, left: 0, width: "100%", height: "100%",
                backgroundColor: "rgba(0,0,0,0.6)", display: "flex", justifyContent: "center", alignItems: "center",
                zIndex: 9999
            }}>
                <div style={{
                background: "white", padding: "2rem", borderRadius: "12px", width: "400px", maxWidth: "90%", position: "relative"
                }}>
                <h3>✅ Confirmar subtarea</h3>
                <p><strong>{confirmingSubtask.title}</strong></p>
                {!confirmingSubtask.completed && (
                <>
                    <label>⏱️ ¿Cuántas horas te tomó?</label>
                    <input
                    type="number"
                    step="0.25"
                    min="0"
                    max="4"
                    value={actualHoursInput}
                    onChange={(e) => setActualHoursInput(e.target.value)}
                    placeholder="Ej: 2.5"
                    style={{ width: "100%", marginTop: "0.5rem", padding: "0.5rem" }}
                    />
                </>
                )}


                <div style={{ marginTop: "1rem", display: "flex", justifyContent: "space-between" }}>
                    <Button
                    variant="contained"
                    color="error"
                    onClick={() => setConfirmingSubtask(null)}
                    >
                    Cancelar
                    </Button>

                    <Button
                    variant="contained"
                    onClick={async () => {
                        const updated = {
                            title: confirmingSubtask.title,
                            completed: !confirmingSubtask.completed,
                            assignedDeveloperId: parseInt(localStorage.getItem("developerId")),
                          };
                          
                          if (!confirmingSubtask.completed) {
                            updated.actualHours = parseFloat(actualHoursInput);
                          }
                          

                        try {
                        const res = await fetch(`http://localhost:8080/subtasks/${confirmingSubtask.id}`, {
                            method: "PUT",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify(updated),
                        });

                        if (!res.ok) throw new Error("No se pudo actualizar");

                        // Actualizar localmente
                        setSubtasks((prev) =>
                            prev.map((s) =>
                            s.id === confirmingSubtask.id
                                ? { ...s, completed: updated.completed, actualHours: updated.actualHours }
                                : s
                            )
                        );

                        setConfirmingSubtask(null);
                        } catch (err) {
                        alert("Error al actualizar subtarea.");
                        console.error(err);
                        }
                    }}
                    >
                    Confirmar
                    </Button>
                </div>
                </div>
            </div>
            )}


      </div>
    </div>
  );
}

export default DeveloperView;

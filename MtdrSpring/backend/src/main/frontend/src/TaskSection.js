import React, { useEffect, useState } from "react";
import Button from "@mui/material/Button";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import LinearProgress from "@mui/material/LinearProgress";

function TaskSection() {
  const [tasks, setTasks] = useState([]);
  const [sprints, setSprints] = useState([]);
  const [selectedSprintId, setSelectedSprintId] = useState("all");
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
    const [developers, setDevelopers] = useState([]);
    const [newTask, setNewTask] = useState({ title: "", description: "" });
    const [subtasks, setSubtasks] = useState([]);
    const [newSubtask, setNewSubtask] = useState({
        title: "",
        estimatedHours: "",
        assignedDeveloperId: ""
    });
    const [selectedTask, setSelectedTask] = useState(null);



  // Cargar tareas y sprints al montar
  useEffect(() => {
    fetch("http://localhost:8080/todolist")
      .then((res) => res.ok ? res.json() : Promise.reject("Error al cargar tareas"))
      .then(setTasks)
      .catch(setError);

    fetch("http://localhost:8080/sprints")
      .then((res) => res.ok ? res.json() : Promise.reject("Error al cargar sprints"))
      .then(setSprints)
      .catch(setError);

      fetch("http://localhost:8080/developers")
      .then((res) => res.ok ? res.json() : Promise.reject("Error al cargar developers"))
      .then(setDevelopers)
      .catch(console.error);    
  }, []);

  // Filtro por sprint
  const filteredTasks = selectedSprintId === "all"
    ? tasks
    : tasks.filter((t) => t.sprint?.id === parseInt(selectedSprintId));

  return (
    <div>
      <div style={{
        display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1rem"
      }}>
        <h2>Tasks</h2>
            <Button
            variant="contained"
            style={{ backgroundColor: "#A7D7C5", color: "#000" }}
            startIcon={<AddCircleOutlineIcon />}
            onClick={() => setShowModal(true)}
            >
            Crear Tarea
            </Button>

      </div>

      {/* Filtro por sprint */}
      <label>Filtrar por sprint:</label>
      <select
        style={{ marginLeft: "1rem", padding: "0.5rem" }}
        value={selectedSprintId}
        onChange={(e) => setSelectedSprintId(e.target.value)}
      >
        <option value="all">Todos</option>
        {sprints.map((s) => (
          <option key={s.id} value={s.id}>
            Sprint #{s.sprintNumber}
          </option>
        ))}
      </select>

      {/* Mostrar tareas */}
      <div style={{ display: "flex", flexWrap: "wrap", gap: "1rem", marginTop: "1rem" }}>
        {filteredTasks.map((task) => {
          const completedSubtasks = task.subtasks?.filter((s) => s.completed).length || 0;
          const total = task.subtasks?.length || 0;
          const progress = total ? Math.round((completedSubtasks / total) * 100) : 0;

          return (
            <div key={task.id} style={{
              background: "#1b4332",
              color: "#fff",
              padding: "1rem",
              borderRadius: "10px",
              width: "350px"
            }}>
              <h3>{task.title}</h3>
              <p><strong>Status:</strong> {progress}% Complete</p>
              <LinearProgress
                variant="determinate"
                value={progress}
                sx={{ height: 8, borderRadius: 4, backgroundColor: "#ccc" }}
              />
              <p><strong>Sprint:</strong> {task.sprint?.sprintNumber || "Ninguno"}</p>
              <p><strong>Subtareas:</strong> {task.subtasks?.length || 0}</p>

              <Button
                variant="outlined"
                size="small"
                style={{ marginTop: "0.5rem", borderColor: "#A7D7C5", color: "#A7D7C5" }}
                onClick={() => setSelectedTask(task)}
                >
                View/Edit
                </Button>

            </div>
          );
        })}
      </div>
      {showModal && (
        <div style={{
            position: "fixed", top: 0, left: 0, width: "100%", height: "100%",
            backgroundColor: "rgba(0,0,0,0.5)", display: "flex", justifyContent: "center", alignItems: "center",
            zIndex: 9999
        }}>
            <div style={{
            background: "white", padding: "2rem", borderRadius: "12px", width: "500px", maxWidth: "90%"
            }}>
            <h3>✍️ Crear nueva tarea</h3>

            {/* Campos principales */}
            <label>Título:</label>
            <input
                type="text"
                value={newTask.title}
                onChange={(e) => setNewTask({ ...newTask, title: e.target.value })}
                style={{ width: "100%", marginBottom: "1rem", padding: "0.5rem" }}
            />

            <label>Descripción:</label>
            <textarea
                value={newTask.description}
                onChange={(e) => setNewTask({ ...newTask, description: e.target.value })}
                rows={3}
                style={{ width: "100%", marginBottom: "1rem", padding: "0.5rem" }}
            />
            <label>Asignar a Sprint:</label>
            <select
            value={newTask.sprintId || ""}
            onChange={(e) => setNewTask({ ...newTask, sprintId: e.target.value })}
            style={{ width: "100%", marginBottom: "1rem", padding: "0.5rem" }}
            >
            <option value="">Sin asignar</option>
            {sprints.map((sprint) => (
                <option key={sprint.id} value={sprint.id}>
                Sprint #{sprint.sprintNumber} ({sprint.startDate} ➡ {sprint.endDate})
                </option>
            ))}
            </select>

            {/* Subtareas */}
            <h4>Subtarea</h4>
            <input
                placeholder="Título"
                value={newSubtask.title}
                onChange={(e) => setNewSubtask({ ...newSubtask, title: e.target.value })}
                style={{ width: "100%", marginBottom: "0.5rem", padding: "0.5rem" }}
            />
            <input
                type="number"
                step="0.25"
                placeholder="Horas estimadas (máx 4)"
                value={newSubtask.estimatedHours}
                onChange={(e) => setNewSubtask({ ...newSubtask, estimatedHours: e.target.value })}
                style={{ width: "100%", marginBottom: "0.5rem", padding: "0.5rem" }}
            />
            <select
                value={newSubtask.assignedDeveloperId}
                onChange={(e) => setNewSubtask({ ...newSubtask, assignedDeveloperId: e.target.value })}
                style={{ width: "100%", marginBottom: "1rem", padding: "0.5rem" }}
            >
                <option value="">Selecciona un developer</option>
                {developers.map((dev) => (
                <option key={dev.id} value={dev.id}>{dev.name}</option>
                ))}
            </select>

            <Button
                variant="outlined"
                onClick={() => {
                if (
                    newSubtask.title &&
                    newSubtask.estimatedHours &&
                    newSubtask.assignedDeveloperId
                ) {
                    setSubtasks([...subtasks, { ...newSubtask }]);
                    setNewSubtask({ title: "", estimatedHours: "", assignedDeveloperId: "" });
                }
                }}
            >
                + Agregar Subtarea
            </Button>

            {/* Preview subtareas */}
            <div style={{ marginTop: "1rem" }}>
                <h4>Subtareas añadidas:</h4>
                <ul>
                {subtasks.map((sub, i) => (
                    <li key={i}>
                    {sub.title} - {sub.estimatedHours}h (Dev ID: {sub.assignedDeveloperId})
                    </li>
                ))}
                </ul>
            </div>

            {/* Botones */}
            <div style={{ display: "flex", justifyContent: "space-between", marginTop: "1.5rem" }}>
                <Button variant="outlined" color="error" onClick={() => setShowModal(false)}>
                Cancelar
                </Button>
                <Button
                variant="contained"
                style={{ backgroundColor: "#A7D7C5" }}
                onClick={async () => {
                    try {
                        const response = await fetch("http://localhost:8080/todolist", {
                            method: "POST",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify({
                              title: newTask.title,
                              description: newTask.description,
                              status: "Not Started",
                              sprint: newTask.sprintId ? { id: parseInt(newTask.sprintId) } : null,
                            }),
                          });
                          

                    if (!response.ok) throw new Error("Error al crear tarea");

                    const created = await response.json();
                    setTasks((prev) => [...prev, created]);

                    // Reset
                    setShowModal(false);
                    setNewTask({ title: "", description: "" });
                    setSubtasks([]);
                    } catch (error) {
                    alert("No se pudo crear la tarea");
                    console.error(error);
                    }
                }}
                >
                Crear Tarea
                </Button>
            </div>
            </div>
        </div>
        )}

        {selectedTask && (
        <div style={{
            position: "fixed", top: 0, left: 0, width: "100%", height: "100%",
            backgroundColor: "rgba(0,0,0,0.5)", display: "flex", justifyContent: "center", alignItems: "center",
            zIndex: 9999
        }}>
            <div style={{
            background: "white", padding: "2rem", borderRadius: "12px", width: "500px", maxWidth: "90%"
            }}>
            <h3>📝 Editar tarea</h3>

            <label>Título:</label>
            <input
                type="text"
                value={selectedTask.title}
                onChange={(e) =>
                setSelectedTask({ ...selectedTask, title: e.target.value })
                }
                style={{ width: "100%", marginBottom: "1rem", padding: "0.5rem" }}
            />

            <label>Descripción:</label>
            <textarea
                rows={3}
                value={selectedTask.description}
                onChange={(e) =>
                setSelectedTask({ ...selectedTask, description: e.target.value })
                }
                style={{ width: "100%", marginBottom: "1rem", padding: "0.5rem" }}
            />

            <label>Asignar Sprint:</label>
            <select
                value={selectedTask.sprint?.id || ""}
                onChange={(e) => {
                const sprintId = e.target.value;
                const sprintObj = sprints.find((s) => s.id === parseInt(sprintId));
                setSelectedTask({ ...selectedTask, sprint: sprintObj });
                }}
                style={{ width: "100%", marginBottom: "1rem", padding: "0.5rem" }}
            >
                <option value="">Sin Sprint</option>
                {sprints.map((s) => (
                <option key={s.id} value={s.id}>
                    Sprint #{s.sprintNumber}
                </option>
                ))}
            </select>

            {/* Subtareas */}
            <h4>Subtareas:</h4>
            <ul>
                {(selectedTask.subtasks || []).map((sub) => (
                <li key={sub.id}>
                    <input
                    type="checkbox"
                    checked={sub.completed}
                    onChange={() => {
                        const updated = {
                        ...sub,
                        completed: !sub.completed,
                        };

                        fetch(`http://localhost:8080/subtasks/${sub.id}`, {
                        method: "PUT",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({
                            title: sub.title,
                            completed: !sub.completed,
                            actualHours: sub.actualHours ?? 0,
                            assignedDeveloperId: sub.assignedDeveloperId,
                          }),                          
                        })
                        .then((res) => res.ok ? res.json() : Promise.reject("Error actualizando subtarea"))
                        .then((data) => {
                            const updatedSubtasks = selectedTask.subtasks.map((s) =>
                            s.id === sub.id ? data : s
                            );
                            setSelectedTask({ ...selectedTask, subtasks: updatedSubtasks });
                        });
                    }}
                    />
                    {sub.title} ({sub.estimatedHours}h)
                </li>
                ))}
            </ul>

            {/* Botones */}
            <div style={{ display: "flex", justifyContent: "space-between", marginTop: "1.5rem" }}>
                <Button variant="outlined" color="error" onClick={() => setSelectedTask(null)}>
                Cerrar
                </Button>
                <Button
                variant="contained"
                style={{ backgroundColor: "#A7D7C5" }}
                onClick={async () => {
                    try {
                    const response = await fetch(`http://localhost:8080/todolist/${selectedTask.id}`, {

                        method: "PUT",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({
                            title: selectedTask.title,
                            description: selectedTask.description,                            
                            status: selectedTask.status || "Not Started",
                            creation_ts: selectedTask.creation_ts || null,
                            sprint: selectedTask.sprint ? { id: selectedTask.sprint.id } : null,

                          }),    
                    });

                    if (!response.ok) throw new Error("Error al actualizar tarea");

                    // Actualiza en la lista principal
                    setTasks((prev) =>
                        prev.map((t) =>
                        t.id === selectedTask.id ? { ...selectedTask } : t
                        )
                    );

                    setSelectedTask(null);
                    } catch (error) {
                    alert("No se pudo actualizar la tarea");
                    console.error(error);
                    }
                }}
                >
                Guardar cambios
                </Button>
            </div>
            </div>
        </div>
        )}


    </div>
  );
}

export default TaskSection;

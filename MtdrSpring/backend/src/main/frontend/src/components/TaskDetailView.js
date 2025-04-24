import React, { useEffect, useState } from "react";
import { useParams, useHistory } from "react-router-dom";
import { Button, Card, Dialog, DialogTitle, DialogContent, DialogActions, TextField, Snackbar, SnackbarContent } from "@material-ui/core";

const TaskDetailView = () => {
  const { id } = useParams();
  const history = useHistory();
  const [task, setTask] = useState(null);
  const [subtasks, setSubtasks] = useState([]);
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [editTitle, setEditTitle] = useState("");
  const [editDescription, setEditDescription] = useState("");

    const [openCreateSubtaskDialog, setOpenCreateSubtaskDialog] = useState(false);
    const [subtaskTitle, setSubtaskTitle] = useState("");
    const [subtaskEstimatedHours, setSubtaskEstimatedHours] = useState("");
    const [developers, setDevelopers] = useState([]);
    const [selectedDeveloperId, setSelectedDeveloperId] = useState("");

    const [openEditSubtaskDialog, setOpenEditSubtaskDialog] = useState(false);
    const [selectedSubtaskToEdit, setSelectedSubtaskToEdit] = useState(null);

    const [openSnackbar, setOpenSnackbar] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarType, setSnackbarType] = useState("success"); // "success" o "error"



  useEffect(() => {
    const fetchTask = async () => {
      const response = await fetch(`${process.env.REACT_APP_API_URL}todolist/${id}`);
      const data = await response.json();
      setTask(data);
      setEditTitle(data.title);
      setEditDescription(data.description);
    };

    const fetchSubtasks = async () => {
      const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/task/${id}`);
      const data = await response.json();
      setSubtasks(data);
    };

    const fetchDevelopers = async () => {
        const response = await fetch(`${process.env.REACT_APP_API_URL}developers`);
        const data = await response.json();
        setDevelopers(data); // .filter(dev => dev.role === "developer")
      };

    fetchTask();
    fetchSubtasks();
    fetchDevelopers();
  }, [id]);

  const handleUpdateTask = async () => {
    const data = {
      title: editTitle,
      description: editDescription,
      status: task.status,
      sprint: task.sprint,
    };

    const response = await fetch(`${process.env.REACT_APP_API_URL}todolist/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    if (response.ok) {
        const updated = await response.json();
        setTask(updated);
        setOpenEditDialog(false);
        setSnackbarMessage("Tarea actualizada exitosamente.");
        setSnackbarType("success");
    } else {
        setSnackbarMessage("Error al actualizar la tarea.");
        setSnackbarType("error");
    }
    setOpenSnackbar(true);
  };

  const handleDeleteTask = async () => {
    // 1. Obtener todas las subtareas de la tarea
    const subtasksResponse = await fetch(`${process.env.REACT_APP_API_URL}subtasks/task/${id}`);
    const subtasksData = await subtasksResponse.json();
  
    // 2. Eliminar las subtareas
    for (let subtask of subtasksData) {
      const deleteSubtaskResponse = await fetch(`${process.env.REACT_APP_API_URL}subtasks/${subtask.id}`, {
        method: "DELETE",
      });
  
      if (!deleteSubtaskResponse.ok) {
        console.error(`Error al eliminar la subtarea ${subtask.id}`);
      }
    }
  
    // 3. Eliminar la tarea
    const response = await fetch(`${process.env.REACT_APP_API_URL}todolist/${id}`, {
      method: "DELETE",
    });
  
    if (response.ok) {
      // 4. Redirigir a la vista de manager
      setSnackbarMessage("Tarea eliminada exitosamente.");
        setSnackbarType("success");
      setTimeout(() => {
        history.push("/manager");
      }, 2000); // Esperar 2 segundos antes de redirigir
      
    } else {
      console.error("Error al eliminar la tarea");
      setSnackbarMessage("Error al eliminar la tarea.");
        setSnackbarType("error");
    }
    setOpenSnackbar(true);
  };
  

  const handleCreateSubtask = async () => {
    const newSubtask = {
      title: subtaskTitle,
      completed: false,
      assignedDeveloperId: parseInt(selectedDeveloperId),
      estimatedHours: parseFloat(subtaskEstimatedHours),
    };
  
    const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/task/${id}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(newSubtask),
    });
  
    if (response.ok) {
      const created = await response.json();
      setSubtasks([...subtasks, created]);
      setOpenCreateSubtaskDialog(false);
      setSubtaskTitle("");
      setSubtaskEstimatedHours("");
      setSelectedDeveloperId("");
      setSnackbarMessage("Subtarea creada exitosamente.");
        setSnackbarType("success");
    } else {
      console.error("Error al crear la subtarea");
      setSnackbarMessage("Error al crear la subtarea.");
        setSnackbarType("error");
    }
    setOpenSnackbar(true);
  };
  
  const handleEditSubtask = async () => {
    const data = {
      title: subtaskTitle,
      completed: selectedSubtaskToEdit.completed,
      assignedDeveloperId: parseInt(selectedDeveloperId),
      estimatedHours: parseFloat(subtaskEstimatedHours),
      actualHours: selectedSubtaskToEdit.actualHours,
      active: true,
    };
  
    const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/${selectedSubtaskToEdit.id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });
  
    if (response.ok) {
      const updated = await response.json();
      const updatedList = subtasks.map((s) =>
        s.id === updated.id ? updated : s
      );
      setSubtasks(updatedList);
      setOpenEditSubtaskDialog(false);
      setSnackbarMessage("Subtarea actualizada exitosamente.");
        setSnackbarType("success");
    } else {
      console.error("Error al editar la subtarea");
        setSnackbarMessage("Error al actualizar la subtarea.");
            setSnackbarType("error");
    }
    setOpenSnackbar(true);
  };

  const handleDeleteSubtask = async (subtaskId) => {
    const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/${subtaskId}`, {
      method: "DELETE",
    });
  
    if (response.ok) {
      const updatedList = subtasks.filter((s) => s.id !== subtaskId);
      setSubtasks(updatedList);
        setSnackbarMessage("Subtarea eliminada exitosamente.");
        setSnackbarType("success");
    } else {
      console.error("Error al eliminar la subtarea");
        setSnackbarMessage("Error al eliminar la subtarea.");
        setSnackbarType("error");
    }
    setOpenSnackbar(true);
  };  
  

  return (
    <div>
        <Button onClick={() => history.push("/manager")} color="primary">
            &lt; Regresar a la vista de tareas
        </Button>


      <h2>Detalles de la Tarea</h2>
      {task && (
        <>
          <p><strong>Título:</strong> {task.title}</p>
          <p><strong>Descripción:</strong> {task.description}</p>
          <p><strong>Status:</strong> {task.status}</p>
          <p><strong>Progreso:</strong> {task.progress}%</p>
          <p><strong>Sprint:</strong> {task.sprint ? `#${task.sprint.sprintNumber}` : "Ninguno"}</p>

          <Button onClick={() => setOpenEditDialog(true)}>Actualizar</Button>
          <Button onClick={handleDeleteTask} color="secondary">Eliminar</Button>
        </>
      )}

      <h3>Subtareas</h3>
    <Button onClick={() => setOpenCreateSubtaskDialog(true)} color="primary" variant="contained">
        Añadir Subtarea
    </Button>

    {subtasks.map((subtask) => {
        const assignedDeveloper = developers.find(
            (dev) => dev.id === subtask.assignedDeveloperId
        );

        return (
            <Card key={subtask.id} className="subtask-card">
            <p><strong>Título:</strong> {subtask.title}</p>
            <p><strong>Completada:</strong> {subtask.completed ? "Sí" : "No"}</p>
            <p><strong>Horas estimadas:</strong> {subtask.estimatedHours}</p>
            <p><strong>Horas reales:</strong> {subtask.actualHours ?? "N/A"}</p>
            <p><strong>Asignada a:</strong> {subtask.assignedDeveloperId}</p>
            {assignedDeveloper ? (
                <p><strong>Asignado a:</strong> {assignedDeveloper.name}</p>
            ) : (
                <p><strong>Asignado a:</strong> Desarrollador no encontrado</p>
            )}
            <Button onClick={() => {
                    setSelectedSubtaskToEdit(subtask);
                    setSubtaskTitle(subtask.title);
                    setSubtaskEstimatedHours(subtask.estimatedHours);
                    setSelectedDeveloperId(subtask.assignedDeveloperId);
                    setOpenEditSubtaskDialog(true);
                }}>
                Editar
            </Button>
            <Button onClick={() => handleDeleteSubtask(subtask.id)} color="secondary">
                Eliminar
            </Button>

            </Card>
        );
    })}

      {/* Modal para editar tarea */}
      <Dialog open={openEditDialog} onClose={() => setOpenEditDialog(false)}>
        <DialogTitle>Editar Tarea</DialogTitle>
        <DialogContent>
          <TextField
            label="Título"
            value={editTitle}
            onChange={(e) => setEditTitle(e.target.value)}
            fullWidth
          />
          <TextField
            label="Descripción"
            value={editDescription}
            onChange={(e) => setEditDescription(e.target.value)}
            fullWidth
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenEditDialog(false)}>Cancelar</Button>
          <Button onClick={handleUpdateTask} color="primary">Actualizar</Button>
        </DialogActions>
      </Dialog>

        {/* Modal para crear subtarea */}
      <Dialog open={openCreateSubtaskDialog} onClose={() => setOpenCreateSubtaskDialog(false)}>
        <DialogTitle>Crear Subtarea</DialogTitle>
        <DialogContent>
            <TextField
            label="Título"
            value={subtaskTitle}
            onChange={(e) => setSubtaskTitle(e.target.value)}
            fullWidth
            required
            />
            <TextField
            label="Horas Estimadas"
            type="number"
            inputProps={{ step: 0.5, min: 0, max: 4 }}
            value={subtaskEstimatedHours}
            onChange={(e) => setSubtaskEstimatedHours(e.target.value)}
            fullWidth
            required
            />
            <TextField
            select
            label="Asignar a"
            value={selectedDeveloperId}
            onChange={(e) => setSelectedDeveloperId(e.target.value)}
            fullWidth
            SelectProps={{ native: true }}
            required
            >
            <option value="">-- Selecciona un developer --</option>
            {developers.map((dev) => (
                <option key={dev.id} value={dev.id}>
                {dev.name}
                </option>
            ))}
            </TextField>
        </DialogContent>
        <DialogActions>
            <Button onClick={() => setOpenCreateSubtaskDialog(false)}>Cancelar</Button>
            <Button onClick={handleCreateSubtask} color="primary">Crear</Button>
        </DialogActions>
        </Dialog>

        {/* Modal para editar subtarea */}
        <Dialog open={openEditSubtaskDialog} onClose={() => setOpenEditSubtaskDialog(false)}>
            <DialogTitle>Editar Subtarea</DialogTitle>
            <DialogContent>
                <TextField
                label="Título"
                value={subtaskTitle}
                onChange={(e) => setSubtaskTitle(e.target.value)}
                fullWidth
                required
                />
                <TextField
                label="Horas Estimadas"
                type="number"
                inputProps={{ step: 0.5, min: 0, max: 4 }}
                value={subtaskEstimatedHours}
                onChange={(e) => setSubtaskEstimatedHours(e.target.value)}
                fullWidth
                required
                />
                <TextField
                select
                label="Asignar a"
                value={selectedDeveloperId}
                onChange={(e) => setSelectedDeveloperId(e.target.value)}
                fullWidth
                SelectProps={{ native: true }}
                required
                >
                <option value="">-- Selecciona un developer --</option>
                {developers.map((dev) => (
                    <option key={dev.id} value={dev.id}>
                    {dev.name}
                    </option>
                ))}
                </TextField>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setOpenEditSubtaskDialog(false)}>Cancelar</Button>
                <Button onClick={handleEditSubtask} color="primary">Guardar</Button>
            </DialogActions>
            </Dialog>



        {/* Snackbar para mostrar mensajes */}
        <Snackbar
            open={openSnackbar}
            autoHideDuration={3000} // Duración en milisegundos
            onClose={() => setOpenSnackbar(false)}
            >
            <SnackbarContent
                style={{
                backgroundColor: snackbarType === "success" ? "green" : "red",
                }}
                message={snackbarMessage}
            />
        </Snackbar>
    </div>
  );
};

export default TaskDetailView;

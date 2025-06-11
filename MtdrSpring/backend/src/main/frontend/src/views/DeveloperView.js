import React, { useState, useEffect } from "react";
import { Tab, Tabs, Card, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Snackbar, SnackbarContent } from "@material-ui/core";
// import { useHistory } from "react-router-dom";

function DeveloperView() {
  const [activeTab, setActiveTab] = useState(0);
  const [openSnackbar, setOpenSnackbar] = useState(false); // Estado para el Snackbar
  const [snackbarMessage, setSnackbarMessage] = useState(""); // Mensaje del Snackbar
  const [snackbarType, setSnackbarType] = useState("success");

  const [subtasks, setSubtasks] = useState([]);
  const [developerStats, setDeveloperStats] = useState({});
  const [taskDetails, setTaskDetails] = useState(null);
  const [openDetailsDialog, setOpenDetailsDialog] = useState(false); // Estado para el modal de detalles
  const [openCompletionDialog, setOpenCompletionDialog] = useState(false); // Estado para el modal de completar tarea
  const [selectedSubtask, setSelectedSubtask] = useState(null); // Subtarea seleccionada
  const [actualHours, setActualHours] = useState(0); // Estado para las horas reales
  // const history = useHistory();

  useEffect(() => {
    const fetchSubtasks = async () => {
      const developerId = localStorage.getItem("developerId");
      const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/developer/${developerId}`);
      const data = await response.json();
      setSubtasks(data);
    };

    const fetchDeveloperStats = async () => {
      const developerId = localStorage.getItem("developerId");
      const response = await fetch(`${process.env.REACT_APP_API_URL}developer-stats/${developerId}`);
      const data = await response.json();
      setDeveloperStats(data);
    };

    fetchSubtasks();
    fetchDeveloperStats();
  }, []);

  const handleViewDetails = async (subtaskId) => {
    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/${subtaskId}/details`);
      const data = await response.json();
      setTaskDetails(data);
      console.log("Detalles de la tarea:", data);
      setOpenDetailsDialog(true); // Abrir el modal de detalles
    } catch (error) {
      console.error("Error al obtener los detalles de la tarea:", error);
    }
  };

  // Función para abrir el modal de completar la subtarea
  const handleCompleteSubtask = (subtaskId) => {
    setSelectedSubtask(subtaskId);
    setOpenCompletionDialog(true); // Abrir el modal para completar la tarea
  };

  // Función para descompletar la subtarea
  const handleUncompleteSubtask = async (subtaskId) => {
    const subtask = subtasks.find((sub) => sub.id === subtaskId);

    const data = {
      title: subtask.title,
      completed: false,
      assignedDeveloperId: subtask.assignedDeveloperId,
      estimatedHours: subtask.estimatedHours,
      actualHours: null, // Restaurar las horas reales a 0
      active: true,
    };

    const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/${subtaskId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    if (response.ok) {
      const updatedSubtasks = subtasks.map((subtask) =>
        subtask.id === subtaskId ? { ...subtask, completed: false, actualHours: 0 } : subtask
      );
      setSubtasks(updatedSubtasks);
      setSnackbarMessage("Subtarea descompletada exitosamente.");
        setSnackbarType("success");
    } else {
      console.error("Error al descompletar la subtarea");
      setSnackbarMessage("Error al descompletar la subtarea.");
        setSnackbarType("error");
    }
    setOpenSnackbar(true);
  };

  // Función para enviar la actualización con las horas reales
  const handleSubmitCompletion = async () => {
    if (selectedSubtask !== null) {
      const subtask = subtasks.find((sub) => sub.id === selectedSubtask);

      const data = {
        title: subtask.title,
        completed: true,
        assignedDeveloperId: subtask.assignedDeveloperId,
        estimatedHours: subtask.estimatedHours,
        actualHours: actualHours, // horas reales
        active: true,
      };

      const response = await fetch(`${process.env.REACT_APP_API_URL}subtasks/${selectedSubtask}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      if (response.ok) {
        const updatedSubtasks = subtasks.map((subtask) =>
          subtask.id === selectedSubtask ? { ...subtask, completed: true, actualHours: actualHours } : subtask
        );
        setSubtasks(updatedSubtasks);
        setOpenCompletionDialog(false); // Cerrar el modal
        setSnackbarMessage("Subtarea completada exitosamente.");
        setSnackbarType("success");
      } else {
        console.error("Error al actualizar la subtarea");
        setSnackbarMessage("Error al completar la subtarea.");
        setSnackbarType("error");
      }
    }
    setOpenSnackbar(true); // Mostrar el Snackbar
  };

  const pendingSubtasks = subtasks.filter(sub => !sub.completed);
  const completedSubtasksSection = subtasks.filter(sub => sub.completed);

  return (
    <div>
      <h1 className="developer-header">Bienvenido, {localStorage.getItem("name")}</h1>

      {/* Pestañas */}
      <Tabs value={activeTab} onChange={(e, newValue) => setActiveTab(newValue)} aria-label="developer tabs">
        <Tab label="Subtasks Asignadas" />
        <Tab label="Reporte Personal" />
      </Tabs>

      {/* Subtasks Asignadas */}
      {activeTab === 0 && (
          <div className="subtasks-container">
          <div className="subtasks-section">
          <h2>Subtareas Pendientes ({pendingSubtasks.length})</h2>
          <div className="subtasks-grid">
            {pendingSubtasks.length > 0 ? (
              pendingSubtasks.map((subtask) => (
                <Card key={subtask.id} className="subtask-card">
                  <h3>{subtask.title}</h3>
                  <p>Pendiente</p>
                  <Button onClick={() => handleViewDetails(subtask.id)}>Ver Detalles</Button>
                  <Button onClick={() => handleCompleteSubtask(subtask.id)}>Marcar como Completada</Button>
                </Card>
              ))
            ) : (
              <p>No hay subtareas pendientes.</p>
            )}
          </div>
        </div>

        <div className="subtasks-section">
        <h2>Subtareas Completadas ({completedSubtasksSection.length})</h2>
          <div className="subtasks-grid">
            {completedSubtasksSection.length > 0 ? (
              completedSubtasksSection.map((subtask) => (
                <Card key={subtask.id} className="subtask-card">
                  <h3>{subtask.title}</h3>
                  <p>Completada</p>
                  <Button onClick={() => handleViewDetails(subtask.id)}>Ver Detalles</Button>
                  <Button onClick={() => handleUncompleteSubtask(subtask.id)}>Descompletar</Button>
                </Card>
              ))
            ) : (
              <p>No hay subtareas completadas.</p>
            )}
          </div>
        </div>
          </div>
      )}

      {/* Reporte Personal */}
      {activeTab === 1 && (
        <div className="report-container">
          <h2>Reporte Personal</h2>
          <p>Tareas Asignadas: {developerStats.totalAssignedCount}</p>
          <p>Tareas Completadas: {developerStats.totalCompletedCount}</p>
          <p>Horas Estimadas: {developerStats.sumEstimatedHours}</p>
          <p>Horas Reales: {developerStats.sumActualHours}</p>
        </div>
      )}

      {/* Modal para ver detalles de la tarea */}
      <Dialog open={openDetailsDialog} onClose={() => setOpenDetailsDialog(false)}>
        <DialogTitle>Detalles de la Tarea</DialogTitle>
        <DialogContent>
          {taskDetails && (
            <>
              <h3>{taskDetails.task.title}</h3>
              <p>{taskDetails.task.description}</p>
              <p><strong>Estado:</strong> {taskDetails.task.status}</p>
              <p><strong>Progreso:</strong> {taskDetails.task.progress}</p>
              <p><strong>Sprint:</strong> {taskDetails.task.sprint ? `#${taskDetails.task.sprint.sprintNumber} (${taskDetails.task.sprint.startDate} - ${taskDetails.task.sprint.endDate})` : "No pertenece a ningún sprint"}</p>
              <p><strong>Horas Estimadas:</strong> {taskDetails.estimatedHours}</p>
              <p><strong>Horas Reales:</strong> {taskDetails.completed ? taskDetails.actualHours : "N/A"}</p>
            </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDetailsDialog(false)} color="primary">
            Cerrar
          </Button>
        </DialogActions>
      </Dialog>

      {/* Modal para ingresar horas reales y completar tarea */}
      <Dialog open={openCompletionDialog} onClose={() => setOpenCompletionDialog(false)}>
        <DialogTitle>Marcar Subtarea como Completada</DialogTitle>
        <DialogContent>
          <h3>Ingresar Horas Reales</h3>
          <TextField
            label="Horas Reales"
            type="number"
            value={actualHours}
            onChange={(e) => setActualHours(e.target.value)}
            fullWidth
            required
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenCompletionDialog(false)} color="primary">
            Cancelar
          </Button>
          <Button onClick={handleSubmitCompletion} color="primary">
            Completar Tarea
          </Button>
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
}

export default DeveloperView;
